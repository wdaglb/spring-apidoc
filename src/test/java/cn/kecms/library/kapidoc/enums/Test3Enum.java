package cn.kecms.library.kapidoc.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Test3Enum {
    /**
     * 枚举1
     */
    OPT1(2),
    /**
     * 枚举2
     */
    OPT2(1),
    ;

    @JsonValue
    private final Integer vvv;
}
