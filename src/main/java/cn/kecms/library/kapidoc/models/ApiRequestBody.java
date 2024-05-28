package cn.kecms.library.kapidoc.models;

import lombok.Data;

@Data
public class ApiRequestBody {
    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 模型
     */
    private String model;
}
