package com.rcpooley.configloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PrimitiveUtils {

    /**
     * Maps primitive class to literal class
     */
    public static final Map<Class, Class> primitiveMap;

    /**
     * Maps literal class to primitive class
     */
    public static final Map<Class, Class> literalMap;

    /**
     * Maps literal class to its primitive value method name
     */
    private static final Map<Class, String> literalCastMethods;

    static {
        primitiveMap = new HashMap<>();
        primitiveMap.put(Byte.TYPE, Byte.class);
        primitiveMap.put(Short.TYPE, Short.class);
        primitiveMap.put(Integer.TYPE, Integer.class);
        primitiveMap.put(Long.TYPE, Long.class);
        primitiveMap.put(Float.TYPE, Float.class);
        primitiveMap.put(Double.TYPE, Double.class);
        primitiveMap.put(Boolean.TYPE, Boolean.class);
        primitiveMap.put(Character.TYPE, Character.class);

        literalMap = new HashMap<>();

        for (Class<?> clazz : primitiveMap.keySet()) {
            literalMap.put(primitiveMap.get(clazz), clazz);
        }

        literalCastMethods = new HashMap<>();

        for (Class<?> clazz : literalMap.keySet()) {
            Class<?> primitiveClass = getPrimitiveClass(clazz);
            String methodName = primitiveClass.getSimpleName() + "Value";
            literalCastMethods.put(clazz, methodName);
        }
    }

    /**
     * Provides the primitive class corresponding to the literal class
     *
     * @param literalClass The literal or primitive class
     * @return The primitive class
     */
    public static Class<?> getPrimitiveClass(Class<?> literalClass) {
        // Return if class is already a primitive class
        if (primitiveMap.containsKey(literalClass)) {
            return literalClass;
        }

        if (!literalMap.containsKey(literalClass)) {
            throw new IllegalArgumentException("Input class " + literalClass.getName() + " is not a java literal class");
        }

        return literalMap.get(literalClass);
    }

    /**
     * Provides the literal class corresponding to the primitive class
     *
     * @param primitiveClass The primitive or literal class
     * @return The literal class
     */
    public static Class<?> getLiteralClass(Class<?> primitiveClass) {
        // Return if class is already a literal class
        if (literalMap.containsKey(primitiveClass)) {
            return primitiveClass;
        }

        if (!primitiveMap.containsKey(primitiveClass)) {
            throw new IllegalArgumentException("Input class " + primitiveClass.getName() + " is not a java primitive class");
        }

        return primitiveMap.get(primitiveClass);
    }

    public static Object castNumber(Number number, Class<?> newType) {
        Class<?> literalClass = getLiteralClass(newType);

        String castMethodName = literalCastMethods.get(literalClass);

        try {
            Method castMethod = number.getClass().getMethod(castMethodName);
            return castMethod.invoke(number);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ClassCastException("Failed to cast " + number + " of type " + number.getClass().getName() + " to " + literalClass.getName() + ": " + e);
        }
    }
}
