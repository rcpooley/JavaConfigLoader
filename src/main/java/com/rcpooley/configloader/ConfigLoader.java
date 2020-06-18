package com.rcpooley.configloader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigLoader {

    private static final Map<Class<?>, ConfigInitializer> initializers;

    private static final ConfigInitializer defaultInitializer;

    public static void addDefaultClassInitializer(final Class<?> clazz) {
        initializers.put(clazz, (object, field, value) -> {
            Object val = value;

            Class<?> fieldType = field.getType();
            if (PrimitiveUtils.primitiveMap.containsKey(fieldType)) {
                fieldType = PrimitiveUtils.getLiteralClass(fieldType);
            }

            // Convert value to correct number type if number
            if (Number.class.isAssignableFrom(fieldType)) {
                val = PrimitiveUtils.castNumber((Number) value, field.getType());
            }

            // Set the field with the casted value
            field.set(object, val);
        });
    }

    public static void addInitializer(Class<?> clazz, ConfigInitializer initializer) {
        initializers.put(clazz, initializer);
    }

    static {
        initializers = new HashMap<>();

        // Add initializer for all primitive and literal types
        for (Class<?> clazz : PrimitiveUtils.primitiveMap.keySet()) {
            addDefaultClassInitializer(clazz);
            addDefaultClassInitializer(PrimitiveUtils.getLiteralClass(clazz));
        }

        // Add initializr for String
        addDefaultClassInitializer(String.class);

        // Add initializer for List
        addInitializer(List.class, (object, field, value) -> {
            assertClass(JSONArray.class, value.getClass());
            JSONArray arr = (JSONArray) value;

            // Get list type
            Class<?> listType;
            try {
                ParameterizedType type = (ParameterizedType) field.getGenericType();
                listType = (Class<?>) type.getActualTypeArguments()[0];
            } catch (Exception e) {
                listType = null;
            }

            List list = new ArrayList();

            for (Object obj : arr) {
                if (obj instanceof JSONObject && listType != null) {
                    obj = inject(listType, (JSONObject) obj);
                }
                list.add(obj);
            }

            field.set(object, list);
        });

        // Set default initializer
        defaultInitializer = (object, field, value) -> {
            assertClass(JSONObject.class, value.getClass());

            Object newValue = inject(field.getType(), (JSONObject) value);

            field.set(object, newValue);
        };
    }

    public static <T> T loadJSON(InputStream input, Class<? extends T> clazz) throws ConfigException {
        Object parsed;
        try {
            parsed = JSONValue.parseWithException(new InputStreamReader(input));
        } catch (Exception e) {
            throw new ConfigException(e);
        }

        assertClass(JSONObject.class, parsed.getClass());

        JSONObject json = (JSONObject) parsed;

        return (T) inject(clazz, json);
    }

    private static Object inject(Class<?> clazz, JSONObject json) throws ConfigException {
        Object instance;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConfigException(e);
        }

        for (Object rawKey : json.keySet()) {
            assertClass(String.class, rawKey.getClass(), "key", rawKey);
            String key = (String) rawKey;
            Field field;
            try {
                field = clazz.getDeclaredField(key);
            } catch (Exception e) {
                throw new ConfigException("Could not find field corresponding to key " + key + " in class " + clazz);
            }
            setField(instance, field, json.get(key));
        }

        return instance;
    }

    private static void setField(Object instance, Field field, Object rawValue) throws ConfigException {
        Class<?> fType = field.getType();

        // Get the initializer
        ConfigInitializer initializer;
        if (initializers.containsKey(fType)) {
            initializer = initializers.get(fType);
        } else {
            initializer = defaultInitializer;
            for (Class<?> clazz : initializers.keySet()) {
                if (clazz.isAssignableFrom(fType)) {
                    initializer = initializers.get(clazz);
                }
            }
        }

        // Attempt to set the field
        try {
            boolean accessable = field.isAccessible();
            field.setAccessible(true);
            initializer.initialize(instance, field, rawValue);
            field.setAccessible(accessable);
        } catch (Exception e) {
            throw new ConfigException("Failed to initialize field", e,
                    "field", fieldString(field),
                    "value", rawValue);
        }
    }

    private static String fieldString(Field field) {
        return field.getDeclaringClass().getName() + " -> " + field.getName();
    }

    private static void assertClass(Class<?> expected, Class<?> actual, Object... tags) throws ConfigException {
        if (expected != actual) {
            throw new ConfigException("Expected " + expected.getName() + " but got " + actual.getName(), tags);
        }
    }
}
