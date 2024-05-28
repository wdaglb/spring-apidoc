package cn.kecms.library.kapidoc.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ApiDoc {
    /**
     * 类型列表
     */
    private Map<String, List<ApiParam>> typeList;

    /**
     * 接口列表
     */
    private List<Api> apiList;
}
