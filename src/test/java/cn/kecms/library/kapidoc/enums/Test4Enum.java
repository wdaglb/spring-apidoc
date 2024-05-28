package cn.kecms.library.kapidoc.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Test4Enum {
    /**
     * 枚举1
     */
    OPT1(2, "有注释1"),
    /**
     * 枚举2
     */
    OPT2(1, "有注释2"),
    ;

    @JsonValue
    private final Integer name;

    private final String text;
}
