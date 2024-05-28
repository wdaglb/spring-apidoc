package cn.kecms.library.kapidoc.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestEnum {
    /**
     * 枚举1
     */
    OPT1("opt1"),
    /**
     * 枚举2
     */
    OPT2("opt2"),
    ;

    @JsonValue
    private final String name;
}
