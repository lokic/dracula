package com.github.lokic.dracula.eventbus.publisher;

import com.github.lokic.javaext.Types;
import com.google.common.reflect.TypeToken;
import com.github.lokic.dracula.event.Event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PublisherManagement {

    public Map<Class<? extends Event>, Publisher<? extends Event>> publishers;

    public PublisherManagement() {
        publishers = new ConcurrentHashMap<>();
    }

    /**
     * 如果publisher不存在，则添加；否则抛异常
     *
     * @param eventClazz
     * @param publisher
     * @param <E>
     */
    public <E extends Event> void addPublisher(Class<E> eventClazz, Publisher<E> publisher) {
        if (hasPublishersForEvent(eventClazz)) {
            throw new IllegalStateException(String.format("eventClass = %s exist publisher", eventClazz.getName()));
        }
        publishers.put(eventClazz, publisher);
    }

    public <E extends Event> void addPublisher(Publisher<E> publisher) {
        Class<E> eventClazz = Types.cast(findEventClassSmart(publisher));
        addPublisher(eventClazz, publisher);
    }


    /**
     * https://gist.github.com/dgageot/bda57296107ca6a0e9df
     *
     * Lambda class name: test.Toto$$Lambda$1/1199823423
     * Implementation synthetic method: lambda$main$0
     * @param publisher
     * @return
     */
    Class<?> findEventClassSmart(Publisher<?> publisher) {
        // Not a lambda
        if (isNotLambda(publisher)) {
            return findEventClass(publisher);
        } else {
            return findEventClassOfLambda(publisher);
        }
    }

    private boolean isNotLambda(Publisher<?> publisher){
        String functionClassName = publisher.getClass().getName();
        int lambdaMarkerIndex = functionClassName.indexOf("$$Lambda$");
        return lambdaMarkerIndex == -1;
    }

    private Class<?> findEventClass(Publisher<?> publisher) {
        return TypeToken.of(publisher.getClass())
               .resolveType(Publisher.class.getTypeParameters()[0])
               .getRawType();
//        return publisher.getClass().getMethods()[0].getParameterTypes()[0];
    }

    private  Class<?> findEventClassOfLambda(Publisher<?> lambdaPublisher){
        String lambdaClassName = lambdaPublisher.getClass().getName();
        int lambdaMarkerIndex = lambdaClassName.indexOf("$$Lambda$");

        String declaringClassName = lambdaClassName.substring(0, lambdaMarkerIndex);
        int lambdaIndex = Integer.parseInt(lambdaClassName.substring(lambdaMarkerIndex + 9, lambdaClassName.lastIndexOf('/')));

        Class<?> declaringClass;
        try {
            declaringClass = Class.forName(declaringClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to find lambda's parent class " + declaringClassName);
        }

        for (Method method : declaringClass.getDeclaredMethods()) {
            if (method.isSynthetic()
                    && method.getName().startsWith("lambda$")
                    && method.getName().endsWith("$" + (lambdaIndex - 1))
                    && Modifier.isStatic(method.getModifiers())) {
                return method.getParameterTypes()[0];
            }
        }

        throw new IllegalStateException("Unable to find lambda's implementation method");
    }


    /**
     * 如果publisher不存在，则添加；否则忽略
     *
     * @param eventClazz
     * @param publisher
     * @param <E>
     */
    public <E extends Event> void addPublisherIfNotExist(Class<E> eventClazz, Publisher<E> publisher) {
        if (!hasPublishersForEvent(eventClazz)) {
            publishers.put(eventClazz, publisher);
        }
    }

    public <E extends Event> Optional<Publisher<E>> findPublisherForEvent(E event) {
        return Optional.ofNullable(findPublisher(Types.getClass(event)));
    }

    private <E extends Event> boolean hasPublishersForEvent(Class<E> eventClazz) {
        return publishers.containsKey(eventClazz);
    }

    private <E extends Event> Publisher<E> findPublisher(Class<E> eClazz) {
        return Types.cast(publishers.get(eClazz));
    }

    public <E extends Event> void processEvent(E event) {
        findPublisherForEvent(event)
                .ifPresent(p -> p.publish(event));
    }
}
