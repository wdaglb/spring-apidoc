package cn.kecms.library.kapidoc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ParamUtil {
    /**
     * 获取范型类型
     */
    public static ParameterizedType getGenericType(Object clazz) {
        ParameterizedType parameterizedType;
        if (clazz instanceof Parameter) {
            return (ParameterizedType) ((Parameter) clazz).getParameterizedType();
        }
        if (clazz instanceof List) {
            Class<?> cls = clazz.getClass();
            Type genericSuperclass = cls.getGenericSuperclass();
            return getGenericType(genericSuperclass);
        }
        return (ParameterizedType) ((Field) clazz).getGenericType();
    }
}
