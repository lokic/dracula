package com.github.lokic.dracula.eventbus.publisher;

import com.github.lokic.javaext.Types;
import com.google.common.reflect.TypeToken;
import com.github.lokic.dracula.event.Event;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
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

    /**
     * https://github.com/benjiman/lambda-type-references/blob/master/src/main/java/com/benjiweber/typeref/MethodFinder.java
     *
     * @param lambdaPublisher
     * @return
     */
    private  Class<?> findEventClassOfLambda(Publisher<?> lambdaPublisher){
        return TypeToken.of(method(lambdaPublisher).getParameters()[0].getParameterizedType()).getRawType();
    }

    private static Method method(Object lambda) {
        SerializedLambda serialized = serialized(lambda);
        Class<?> containingClass = getContainingClass(serialized);
        return Arrays.stream(containingClass.getDeclaredMethods())
                .filter(method -> Objects.equals(method.getName(), serialized.getImplMethodName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private static SerializedLambda serialized(Object lambda) {
        try {
            Method writeMethod = lambda.getClass().getDeclaredMethod("writeReplace");
            writeMethod.setAccessible(true);
            return (SerializedLambda) writeMethod.invoke(lambda);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static Class<?> getContainingClass(SerializedLambda lambda) {
        try {
            String className = lambda.getImplClass().replaceAll("/", ".");
            return Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
