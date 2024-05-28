package cn.kecms.library.kapidoc.parser;

import cn.hutool.core.util.ReUtil;
import cn.kecms.library.kapidoc.utils.CommentUtil;
import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

public class TypeParser {
    private final Class<?> clazz;

    @Getter
    private String path;

    @Getter
    private String title;

    public TypeParser(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Collection<Method> parse() {
        ClassJavadoc javadoc = RuntimeJavadoc.getJavadoc(this.clazz);
        this.title = CommentUtil.getTitle(javadoc.getComment());
        RequestMapping[] annotationsByType = this.clazz.getDeclaredAnnotationsByType(RequestMapping.class);

        if (annotationsByType.length > 0) {
            RequestMapping mapping = annotationsByType[0];
            if (mapping.path().length > 0) {
                this.path = mapping.path()[0];
            } else {
                this.path = mapping.value()[0];
            }
        }
        return this.getMethods();
    }

    /**
     * 获取控制器方法
     */
    public Collection<Method> getMethods() {
        Collection<Method> methods = new ArrayList<>();
        Method[] declaredMethods = this.clazz.getDeclaredMethods();

        for (Method method : declaredMethods) {
            if (this.isRoute(method.getDeclaredAnnotations())) {
                methods.add(method);
            }
        }
        return methods;
    }

    /**
     * 是否为路由
     */
    private boolean isRoute(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            String name = annotation.annotationType().getName();
            if (ReUtil.isMatch("org.springframework.web.bind.annotation.\\w+Mapping", name)) {
                return true;
            }
        }
        return false;
    }
}
