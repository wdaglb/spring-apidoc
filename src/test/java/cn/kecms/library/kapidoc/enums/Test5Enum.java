package cn.kecms.library.kapidoc.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Test5Enum {
    OPT1(2, "无注释1"),
    OPT2(1, "无注释2"),
    ;

    @JsonValue
    private final Integer name;

    private final String text;
}
