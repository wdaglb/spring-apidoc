package cn.kecms.library.kapidoc.utils;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.kecms.library.kapidoc.models.ApiParam;
import com.github.therapi.runtimejavadoc.Comment;
import com.github.therapi.runtimejavadoc.FieldJavadoc;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class CommentUtil {
    /**
     * 获取注释的标题
     */
    public static String getTitle(Comment comment) {
        String val = comment.toString();
        if (val.isEmpty()) {
            return "";
        }
        String[] temps = val.split("\n");
        return temps[0].trim();
    }

    /**
     * 获取注释的简介
     */
    public static String getDescription(Comment comment) {
        String val = comment.toString();
        if (val.isEmpty()) {
            return "";
        }
        String[] temps = val.split("\n");
        if (temps.length < 2) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < temps.length; i++) {
            sb.append(temps[i].trim());
        }
        return sb.toString();
    }

    /**
     * 获取控制器名称
     */
    public static String getControllerName(String path) {
        return ReUtil.get(".(\\w+)Controller$", path, 1);
    }

    /**
     * 获取字段标题
     */
    public static String getFieldText(Field field) {
        FieldJavadoc javadoc = RuntimeJavadoc.getJavadoc(field);
        return getTitle(javadoc.getComment());
    }

}
