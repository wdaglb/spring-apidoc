package cn.kecms.library.kapidoc.parser;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.kecms.library.kapidoc.convert.FieldNameConvert;
import cn.kecms.library.kapidoc.models.ApiParam;
import cn.kecms.library.kapidoc.utils.CommentUtil;
import cn.kecms.library.kapidoc.utils.ParamUtil;
import com.github.therapi.runtimejavadoc.*;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamParser {
    private final Components components;

    private final Object parameter;

    @Getter
    private final String title;

    @Getter
    private final String description;

    @Getter
    private Schema<?> schema;

    private final FieldNameConvert fieldNameConvert;

    private Class<?> parameterType;

    private String parent = "";

    private boolean isQuery = false;

    public ParamParser(Components components, Parameter parameter, String title, String description) {
        this.components = components;
        this.parameter = parameter;
        this.title = title;
        this.description = description;
        this.schema = new Schema<>();
        this.fieldNameConvert = SpringUtil.getBean(FieldNameConvert.class);
        this.parameterType = this.getParameterType();
        this.parseParameter();
    }

    public ParamParser(Components components, Field field, String title, String description) {
        this.components = components;
        this.parameter = field;
        this.title = title;
        this.description = description;
        this.schema = new Schema<>();
        this.fieldNameConvert = SpringUtil.getBean(FieldNameConvert.class);
        this.parameterType = this.getParameterType();
        this.parseParameter();
    }

    public ParamParser(Components components, Type type) {
        this.components = components;
        this.parameter = type;
        this.title = "";
        this.description = "";
        this.schema = new Schema<>();
        this.fieldNameConvert = SpringUtil.getBean(FieldNameConvert.class);
        this.parameterType = this.getParameterType();
        this.parseParameter();
    }

    /**
     * 获取字段名
     */
    private String getName() {
        String str;
        if (this.parameter instanceof Parameter) {
            str = ((Parameter) this.parameter).getName();
        } else if (this.parameter instanceof Class) {
            str = ((Class<?>) this.parameter).getSimpleName();
        } else {
            str = ((Field) this.parameter).getName();
        }
        str = this.fieldNameConvert.convert(str);
        if (StrUtil.isNotBlank(this.parent)) {
            str = this.parent + "." + str;
        }
        return str;
    }

    /**
     * 获取字段类型
     */
    private Class<?> getParameterType() {
        if (this.parameter instanceof Parameter) {
            return ((Parameter) this.parameter).getType();
        } else if (this.parameter instanceof Class) {
            return (Class<?>) this.parameter;
        } else {
            return ((Field) this.parameter).getType();
        }
    }

    /**
     * 解析参数结构
     */
    private void parseParameter() {
        if (StrUtil.isNotBlank(this.title)) {
            this.schema.setDescription(this.title);
        }
        if (this.parameterType.getName().startsWith("java.")) {
            String typeName = this.getTypeName(this.parameterType.getName());
            this.schema.setType(typeName);
            this.schema.setName(this.getName());
            if (typeName.equals("array")) {
                ParameterizedType genericType = ParamUtil.getGenericType(this.parameter);
                ParamParser paramParser = this.parseGeneric(genericType);
                if (paramParser != null) {
                    this.schema.setItems(paramParser.schema);
                }
            }
        } else {
            this.schema.setType("object");
            if (this.components.getSchemas() == null || !this.components.getSchemas().containsKey(this.parameterType.getSimpleName())) {
                this.parseModel(this.parameterType);
                this.components.addSchemas(this.parameterType.getSimpleName(), this.schema);
            } else if (this.components.getSchemas() != null) {
                this.schema = this.components.getSchemas().get(this.parameterType.getSimpleName());
            }
        }
    }

    /**
     * 获取类型名称
     */
    private String getTypeName(String name) {
        String[] split = name.split("\\.");
        String typeName = split[split.length - 1].toLowerCase();

        if (ListUtil.of("list", "set", "collection").contains(typeName)) {
            return "array";
        }
        if (typeName.equalsIgnoreCase("date")) {
            return "string";
        }
        if (typeName.equalsIgnoreCase("map")) {
            return "object";
        }
        if (typeName.equalsIgnoreCase("long")) {
            return "integer";
        }
        return typeName;
    }

    /**
     * 解析范型类型
     */
    private ParamParser parseGeneric(ParameterizedType type) {
        Type clazz = type.getActualTypeArguments()[0];
        if (clazz.getTypeName().startsWith("java.")) {
            return new ParamParser(this.components, clazz);
        }
        return null;
        // return new ParamParser(this.components, clazz, RuntimeJavadoc.getJavadoc((Class<?>) clazz));
    }

    private void parseModel(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            FieldJavadoc javadoc = RuntimeJavadoc.getJavadoc(declaredField);
            ParamParser paramParser = new ParamParser(this.components, declaredField, javadoc.getComment().toString(), "");

            if (paramParser.schema.getName() == null) {
                continue;
            }
            String parentName = this.fieldNameConvert.convert(declaredField.getName());
            if (StrUtil.isNotBlank(parentName)) {
                if (StrUtil.isBlank(this.parent)) {
                    paramParser.parent = parentName + ".";
                } else {
                    paramParser.parent = this.parent + parentName + ".";
                }
            }
            this.schema.addProperty(parentName, paramParser.schema);
            if (declaredField.isAnnotationPresent(NotBlank.class) ||
                    declaredField.isAnnotationPresent(NotNull.class) ||
                    declaredField.isAnnotationPresent(NonNull.class) ||
                    declaredField.isAnnotationPresent(org.springframework.lang.NonNull.class) ||
                    declaredField.isAnnotationPresent(NotEmpty.class)) {
                this.schema.addRequiredItem(declaredField.getName());
            }
        }
    }

    /**
     * 转换为参数
     */
    public io.swagger.v3.oas.models.parameters.Parameter toParameter() {
        Schema<Object> schema = new Schema<>();
        schema.setName(this.getName());
        schema.setType(this.schema.getType());

        if (this.schema.getType().equals("object")) {
            schema.set$ref("#/components/schemas/" + this.parameterType.getSimpleName());
        } else if (this.schema.getType().equals("array")) {
            schema.setItems(this.schema.getItems());
        }

        io.swagger.v3.oas.models.parameters.Parameter parameter = new io.swagger.v3.oas.models.parameters.Parameter();
        parameter.setName(this.getName());
        parameter.setSchema(schema);
        parameter.setDescription(this.schema.getDescription());
        if (this.isPathVariable()) {
            parameter.setIn("path");
        } else {
            parameter.setIn("query");
        }
        return parameter;
    }

    private boolean isPathVariable() {
        if (this.parameter instanceof Parameter) {
            return ((Parameter) this.parameter).isAnnotationPresent(PathVariable.class);
        } else {
            return ((Field) this.parameter).isAnnotationPresent(PathVariable.class);
        }
    }

    private boolean isRequestParam() {
        if (this.parameter instanceof Parameter) {
            return ((Parameter) this.parameter).isAnnotationPresent(RequestParam.class);
        } else {
            return ((Field) this.parameter).isAnnotationPresent(RequestParam.class);
        }
    }

    private boolean isRequestBody() {
        if (this.parameter instanceof Parameter) {
            return ((Parameter) this.parameter).isAnnotationPresent(org.springframework.web.bind.annotation.RequestBody.class);
        } else {
            return ((Field) this.parameter).isAnnotationPresent(org.springframework.web.bind.annotation.RequestBody.class);
        }
    }

    /**
     * 转换为RequestBody
     */
    public RequestBody toRequestBody() {
        Class<?> parameterType = this.getParameterType();
        RequestBody requestBody = new RequestBody();
        Content content = new Content();
        Schema<Object> schema = new Schema<>();

        schema.$ref("#/components/schemas/" + parameterType.getSimpleName());

        MediaType mediaType = new MediaType();
        mediaType.setSchema(schema);

        content.addMediaType("application/json", mediaType);

        requestBody.content(content);
        return requestBody;
    }
}
