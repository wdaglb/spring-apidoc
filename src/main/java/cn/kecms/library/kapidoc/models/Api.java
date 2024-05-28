package cn.kecms.library.kapidoc.models;

import lombok.Data;

import java.util.List;

@Data
public class Api {
    /**
     * 所属目录
     */
    private String dir;

    /**
     * 标题
     */
    private String title;

    /**
     * 路径
     */
    private String path;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 介绍
     */
    private String description;

    /**
     * 参数列表
     */
    private List<ApiParam> query;

    /**
     * 请求体
     */
    private ApiRequestBody body;

    /**
     * 响应
     */
    private ApiResponse response;
}
