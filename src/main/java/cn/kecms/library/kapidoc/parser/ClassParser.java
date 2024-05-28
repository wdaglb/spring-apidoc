package cn.kecms.library.kapidoc.parser;

import cn.kecms.library.kapidoc.models.ApiEnum;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@Slf4j
public class ClassParser {
    private final Components components;

    private final Class<?> handle;

    private Schema<Object> schema;

    public ClassParser(Components components, Class<?> handle) {
        this.components = components;
        this.handle = handle;
    }

    /**
     * 解析类型
     */
    public Schema<?> parse() {
        this.schema = new Schema<>();
        if (this.handle == null) {
            return this.schema;
        }
        if (this.handle.isEnum()) {
            List<ApiEnum> enumList = new EnumParser(this.handle).parse();
            if (!enumList.isEmpty()) {
                String typeName = this.getTypeName(enumList.get(0).getValue().getClass().getTypeName());
                this.schema.setType(typeName);
                for (ApiEnum apiEnum : enumList) {
                    this.schema.addEnumItemObject(apiEnum.getValue());
                }
            }
            return this.schema;
        }
        String typeName = this.getTypeName();
        if (this.isArray() || typeName.equals("list") || typeName.equals("set")) {
            this.schema.setType("array");
            // Type type = ((ParameterizedType) this.handle.getGenericInterfaces()[0]).getActualTypeArguments()[0];
            //
            // if (this.components.getSchemas() != null) {
            //     Schema<?> schema = this.components.getSchemas().get(type.getTypeName());
            //     if (schema != null) {
            //         this.schema.setItems(schema);
            //         return this.schema;
            //     }
            // }
            // ClassParser classParser = new ClassParser(this.components, (Class<?>) type);
            // Schema<?> parse = classParser.parse();
            // this.schema.setItems(parse);
            // this.components.addSchemas(this.handle.getName(), parse);
            return this.schema;
        }
        if (!this.isSystemClass()) {
            if (this.components.getSchemas() != null) {
                Schema<?> schema = this.components.getSchemas().get(this.handle.getSimpleName());
                if (schema != null) {
                    this.schema.set$ref("#/components/schemas/" + this.handle.getSimpleName());
                    return this.schema;
                }
            }
            Schema<?> schema = new Schema<>();
            schema.setType("object");

            // 解析字段
            Field[] declaredFields = this.handle.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                Type type = declaredField.getGenericType();

                if (type instanceof ParameterizedType) {
                    Type genericType = ((ParameterizedType) type).getActualTypeArguments()[0];
                    ClassParser classParser = new ClassParser(this.components, (Class<?>) genericType);
                    Schema<?> parse = classParser.parse();

                    Schema<?> arr = new Schema<>();
                    arr.setType("array");
                    arr.setItems(parse);

                    schema.addProperty(declaredField.getName(), arr);
                } else {
                    ClassParser classParser = new ClassParser(this.components, declaredField.getType());
                    Schema<?> parse = classParser.parse();
                    schema.addProperty(declaredField.getName(), parse);
                }
            }
            this.components.addSchemas(this.handle.getSimpleName(), schema);

            this.schema.set$ref("#/components/schemas/" + this.handle.getSimpleName());
            return this.schema;
        }
        if (typeName.equals("date")) {
            this.schema.setType("string");
            this.schema.setFormat("date-time");
            return this.schema;
        }

        this.schema.setType(this.getTypeName());
        // this.schema.setName(this.handle.getName());

        // this.components.addSchemas(this.handle.getName(), this.schema);
        return this.schema;
    }

    /**
     * 获取当前类型名称
     */
    private String getTypeName() {
        return this.getTypeName(this.handle.getTypeName());
    }

    private String getTypeName(String type) {
        String name = type.replace("java.lang.", "")
                .replace("java.util.", "")
                .replace("sun.util.", "")
                .toLowerCase();
        if (name.equals("int")) {
            return "integer";
        }
        return name.replace("long", "integer");
    }

    /**
     * 是否数组
     */
    private boolean isArray() {
        return this.handle.isArray();
    }

    /**
     * 是否内置类型
     */
    private boolean isSystemClass() {
        if (this.handle.isPrimitive()) {
            return true;
        }
        String type = this.handle.getTypeName();
        return type.startsWith("java.lang") || type.startsWith("java.util") || type.startsWith("sun.util");
    }

    /**
     * 是否内置类型
     */
    private boolean isSystemClass(Field field) {
        if (field.getType().isPrimitive()) {
            return true;
        }
        String type = field.getType().getTypeName();
        return type.startsWith("java.lang") || type.startsWith("java.util") || type.startsWith("sun.util");
    }
}
