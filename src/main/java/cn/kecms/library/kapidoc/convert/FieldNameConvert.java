package cn.kecms.library.kapidoc.convert;

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 字段名称转换器
 */
@Component
@ConditionalOnBean(FieldNameConvert.class)
public class FieldNameConvert {
    public String convert(String name) {
        return StrUtil.toUnderlineCase(name);
    }
}
