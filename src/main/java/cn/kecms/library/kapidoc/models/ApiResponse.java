package cn.kecms.library.kapidoc.models;

import lombok.Data;

import java.util.List;

@Data
public class ApiResponse {
    /**
     * 描述
     */
    private String description;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 模型
     */
    private String model;
}
