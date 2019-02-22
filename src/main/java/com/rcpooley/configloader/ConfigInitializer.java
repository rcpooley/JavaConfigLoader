package com.rcpooley.configloader;

import java.lang.reflect.Field;

public interface ConfigInitializer {

    void initialize(Object object, Field field, Object value) throws ConfigException, IllegalAccessException;

}
