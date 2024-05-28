package cn.kecms.library.kapidoc.models;

import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Data
public class RequestOption {
    private String path;

    private String method;

    public RequestOption(Method method) {
        if (this.hasAnnotation(method, RequestMapping.class)) {
            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
            this.path = this.getPath(annotation);
            this.method = annotation.method()[0].name();
            return;
        }
        if (this.hasAnnotation(method, GetMapping.class)) {
            GetMapping annotation = method.getAnnotation(GetMapping.class);
            this.path = this.getPath(annotation);
            this.method = "GET";
            return;
        }
        if (this.hasAnnotation(method, PostMapping.class)) {
            PostMapping annotation = method.getAnnotation(PostMapping.class);
            this.path = this.getPath(annotation);
            this.method = "POST";
            return;
        }
        if (this.hasAnnotation(method, PutMapping.class)) {
            PutMapping annotation = method.getAnnotation(PutMapping.class);
            this.path = this.getPath(annotation);
            this.method = "PUT";
            return;
        }
        if (this.hasAnnotation(method, DeleteMapping.class)) {
            DeleteMapping annotation = method.getAnnotation(DeleteMapping.class);
            this.path = this.getPath(annotation);
            this.method = "DELETE";
            return;
        }
        if (this.hasAnnotation(method, PatchMapping.class)) {
            PatchMapping annotation = method.getAnnotation(PatchMapping.class);
            this.path = this.getPath(annotation);
            this.method = "PATCH";
            return;
        }
    }

    private String getPath(Object requestMapping) {
        try {
            String path = this.getPathByObject(requestMapping, "path");
            if (path != null) {
                return path;
            }
            path = this.getPathByObject(requestMapping, "value");
            if (path != null) {
                return path;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private String getPathByObject(Object objectData, String key) throws Exception {
        Method path = objectData.getClass().getDeclaredMethod(key);
        Object object = path.invoke(objectData);
        if (object instanceof String[]) {
            String[] paths = (String[]) object;
            if (paths.length > 0) {
                return paths[0];
            }
        }
        return null;
    }

    private <T extends Annotation> boolean hasAnnotation(Method method, Class<T> val) {
        return method.getDeclaredAnnotation(val) != null;
    }

}
