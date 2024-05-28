package cn.kecms.library.kapidoc;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.kecms.library.kapidoc.convert.FieldNameConvert;
import cn.kecms.library.kapidoc.models.Api;
import cn.kecms.library.kapidoc.models.ApiDoc;
import cn.kecms.library.kapidoc.models.ApiRequestBody;
import cn.kecms.library.kapidoc.parser.ClassParser;
import cn.kecms.library.kapidoc.parser.MethodParser;
import cn.kecms.library.kapidoc.parser.ParamParser;
import cn.kecms.library.kapidoc.parser.TypeParser;
import com.github.therapi.runtimejavadoc.*;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.Getter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class KApiDoc {
    protected String basePackage;

    @Getter
    protected final OpenAPI openAPI;

    protected final FieldNameConvert fieldNameConvert;

    public KApiDoc(String basePackage) {
        this.basePackage = basePackage;
        this.openAPI = new OpenAPI();
        this.openAPI.setInfo(new io.swagger.v3.oas.models.info.Info().title("API文档").version("1.0"));
        this.openAPI.setComponents(new Components());
        this.fieldNameConvert = SpringUtil.getBean(FieldNameConvert.class);
    }

    public void scan() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(this.basePackage);
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String definitionName : beanDefinitionNames) {
            Object object = context.getBean(definitionName);
            Class<?> objectClass = object.getClass();
            RestController restController = objectClass.getDeclaredAnnotation(RestController.class);
            if (restController == null) {
                continue;
            }
            TypeParser typeParser = new TypeParser(objectClass);
            Collection<Method> methods = typeParser.parse();

            // 添加标签
            if (StrUtil.isNotBlank(typeParser.getTitle())) {
                Tag tag = new Tag();
                tag.setName(typeParser.getTitle());
                this.openAPI.addTagsItem(tag);
            }

            for (Method method : methods) {
                MethodJavadoc methodJavadoc = RuntimeJavadoc.getJavadoc(method);
                MethodParser methodParser = new MethodParser(method, methodJavadoc);

                PathItem pathItem = new PathItem();
                if (StrUtil.isNotBlank(typeParser.getPath())) {
                    this.openAPI.path(typeParser.getPath() + methodParser.getPath(), pathItem);
                } else {
                    this.openAPI.path(methodParser.getPath(), pathItem);
                }

                Operation operation = new Operation();
                operation.setSummary(methodParser.getTitle());
                if (StrUtil.isNotBlank(typeParser.getTitle())) {
                    operation.setTags(Collections.singletonList(typeParser.getTitle()));
                }
                switch (methodParser.getRequestMethod().toLowerCase()) {
                    case "post":
                        pathItem.post(operation);
                        break;
                    case "get":
                        pathItem.get(operation);
                        break;
                    case "put":
                        pathItem.put(operation);
                        break;
                    case "patch":
                        pathItem.patch(operation);
                        break;
                    case "delete":
                        pathItem.delete(operation);
                        break;
                    default:
                        break;
                }

                this.parseParam(operation, method, methodJavadoc);
                this.parseResponse(operation, method, methodJavadoc);
            }

        }
    }

    /**
     * 解析入参
     */
    private void parseParam(Operation operation, Method method, MethodJavadoc methodJavadoc) {
        Map<String, String> paramComments = this.getParamCommentMap(method, methodJavadoc);
        for (Parameter parameter : method.getParameters()) {
            // body
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                Class<?> type = parameter.getType();

                Schema<?> parse = new ClassParser(this.openAPI.getComponents(), type).parse();

                io.swagger.v3.oas.models.parameters.RequestBody body = new io.swagger.v3.oas.models.parameters.RequestBody();
                body.setContent(new Content().addMediaType("application/json", new MediaType().schema(parse)));
                operation.requestBody(body);
                // io.swagger.v3.oas.models.parameters.RequestBody body = paramParser.toRequestBody();
                // operation.requestBody(body);
                continue;
            }

            Schema<?> parse = new ClassParser(this.openAPI.getComponents(), parameter.getType()).parse();
            // operation.addParametersItem(paramParser.toParameter());
        }
    }

    /**
     * 解析参数注释
     */
    private Map<String, String> getParamCommentMap(Method method, MethodJavadoc javadoc) {
        Map<String, String> result = new HashMap<>();
        Map<String, Comment> comments = javadoc.getParams().stream().collect(Collectors.toMap(ParamJavadoc::getName, ParamJavadoc::getComment));

        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            String name = parameter.getName();
            if (comments.containsKey(name)) {
                result.put(name, comments.get(name).toString());
            } else {
                ClassJavadoc paramJavadoc = RuntimeJavadoc.getJavadoc(parameter.getType());

                result.put(name, paramJavadoc.getComment().toString());
            }
        }
        return result;
    }

    /**
     * 解析响应参数
     */
    private void parseResponse(Operation operation, Method method, MethodJavadoc methodJavadoc) {
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class) {
            return;
        }
        ClassParser classParser = new ClassParser(this.openAPI.getComponents(), returnType);
        Schema<?> schema = classParser.parse();

        Content content = new Content();
        MediaType mediaType = new MediaType();
        mediaType.setSchema(schema);
        // mediaType.setSchema(paramParser.getSchema());
        content.addMediaType("application/json", mediaType);

        ApiResponse response = new ApiResponse();
        // response.setDescription(paramParser.getDescription());
        response.setContent(content);

        ApiResponses responses = new ApiResponses();
        responses.put("200", response);
        operation.setResponses(responses);
    }
}
