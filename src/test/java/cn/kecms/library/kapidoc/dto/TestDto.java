package cn.kecms.library.kapidoc.dto;

import cn.kecms.library.kapidoc.enums.*;
import lombok.Data;

import java.util.Date;

@Data
public class TestDto {
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 简介
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 枚举例子1
     */
    private TestEnum test;

    /**
     * 枚举例子2
     */
    private Test2Enum test2;

    /**
     * 枚举例子3
     */
    private Test3Enum test3;

    /**
     * 枚举例子4
     */
    private Test4Enum test4;

    /**
     * 枚举例子5
     */
    private Test5Enum test5;
}
