package cn.kecms.library.kapidoc.models;

import lombok.Data;

import java.util.List;

@Data
public class ApiParam {
    /**
     * 参数名
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 子类型
     */
    private List<String> subType;

    /**
     * 中文名
     */
    private String text;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否必填
     */
    private boolean required;
}
