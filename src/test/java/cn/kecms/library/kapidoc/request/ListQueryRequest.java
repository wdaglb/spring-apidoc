package cn.kecms.library.kapidoc.request;

import lombok.Data;

import java.util.List;

@Data
public class ListQueryRequest {
    /**
     * 页码
     */
    private Integer current;

    /**
     * id
     */
    private List<Integer> ids;
}
