package cn.kecms.library.kapidoc.parser;

import cn.hutool.core.util.StrUtil;
import cn.kecms.library.kapidoc.models.Api;
import cn.kecms.library.kapidoc.models.RequestOption;
import cn.kecms.library.kapidoc.utils.CommentUtil;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MethodParser {
    private final Method method;

    private final MethodJavadoc javadoc;

    @Getter
    private String title;

    @Getter
    private String requestMethod;

    @Getter
    private String path;

    public MethodParser(Method method, MethodJavadoc javadoc) {
        this.method = method;
        this.javadoc = javadoc;
        this.parse();
    }

    private void parse() {
        RequestOption requestOption = new RequestOption(this.method);
        this.title = CommentUtil.getTitle(this.javadoc.getComment());
        this.requestMethod = requestOption.getMethod();
        this.path = requestOption.getPath();
    }

}
