package cn.kecms.library.kapidoc.parser;

import cn.kecms.library.kapidoc.models.ApiEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EnumParser {
    private final Class<?> handle;

    public EnumParser(Class<?> handle) {
        this.handle = handle;
    }

    public List<ApiEnum> parse() {
        List<ApiEnum> res = new ArrayList<>();
        String valueName = this.getValueName();
        Object[] enumConstants = this.handle.getEnumConstants();

        if (valueName == null) {
            for (Object enumConstant : enumConstants) {
                ApiEnum apiEnum = new ApiEnum();
                apiEnum.setValue(enumConstant);
                apiEnum.setLabel(enumConstant.toString());
                res.add(apiEnum);
            }

            return res;
        }
        try {

            for (Object enumConstant : enumConstants) {
                Method method = this.handle.getDeclaredMethod("get" + valueName.substring(0, 1).toUpperCase() + valueName.substring(1));
                Object value = method.invoke(enumConstant);
                ApiEnum apiEnum = new ApiEnum();
                apiEnum.setValue(value);
                apiEnum.setLabel(enumConstant.toString());
                res.add(apiEnum);
            }
        } catch (Exception ignored) {
        }
        return res;
    }

    /**
     * 取得枚举值的字段名
     */
    private String getValueName() {
        Field[] declaredFields = this.handle.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(JsonValue.class)) {
                return declaredField.getName();
            }
        }
        return null;
    }
}
