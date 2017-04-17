package edu.umich.powertutor.battery;

import java.lang.reflect.Field;

/**
 * Created by hanxin on 4/13/17.
 */

public class ReflectionUtils {

    public static int getId(String fieldName, String resName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = Class.forName("com.android.internal.R$" + fieldName);

        Field field = clazz.getField(resName);
        field.setAccessible(true);
        return field.getInt(null);
    }

}
