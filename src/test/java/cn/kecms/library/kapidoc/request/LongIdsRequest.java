package cn.kecms.library.kapidoc.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
public class LongIdsRequest implements Serializable {
    @NotEmpty
    private List<Long> ids;
}
