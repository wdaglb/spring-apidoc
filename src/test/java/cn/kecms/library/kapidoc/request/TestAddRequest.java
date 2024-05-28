package cn.kecms.library.kapidoc.request;

import cn.kecms.library.kapidoc.dto.TestDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TestAddRequest implements Serializable {
    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    private String name;

    private String description;

    /**
     * id
     */
    private List<Integer> ids;

    /**
     * id2
     */
    @NotEmpty
    private List<Integer> id2;

    /**
     * 列表
     */
    private List<TestDto> testList;

    /**
     * 时间
     */
    private Date time;
}
