package com.github.lokic.dracula.eventbus;

import com.google.common.reflect.TypeToken;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class GenericTypes {


    public static Class<?> getGeneric(Object object, Class<?> clazz) {
        if (isLambda(object)) {
            return getGenericForLambda(object);
        } else {
            return getGenericForObject(object, clazz);
        }
    }

    private static boolean isLambda(Object object) {
        String functionClassName = object.getClass().getName();
        int lambdaMarkerIndex = functionClassName.indexOf("$$Lambda$");
        return lambdaMarkerIndex != -1;
    }

    private static Class<?> getGenericForObject(Object object, Class<?> clazz) {
        return TypeToken.of(object.getClass())
                .resolveType(clazz.getTypeParameters()[0])
                .getRawType();
    }

    /**
     * https://github.com/benjiman/lambda-type-references/blob/master/src/main/java/com/benjiweber/typeref/MethodFinder.java
     *
     * @param lambda
     * @return
     */
    private static Class<?> getGenericForLambda(Object lambda) {
        return TypeToken.of(method(lambda).getParameters()[0].getParameterizedType()).getRawType();
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
}
