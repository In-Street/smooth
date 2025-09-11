package com.smooth.feature.enums;

import lombok.Getter;

/**
 *
 * @author Cheng Yufei
 * @create 2025-09-05 22:06
 **/
@Getter
public enum DayEnum {

    MONDAY(1, "星期一"),
    TUESDAY(2, "星期二"),
    WEDNESDAY(3, "星期三"),
    THURSDAY(4, "星期四"),
    FRIDAY(5, "星期五"),
    SATURDAY(6,"星期六"),
    SUNDAY(7,"星期日"),

    DEFAULT(999,"");


    private Integer code;
    private String name;

    DayEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DayEnum get(Integer code) {
        DayEnum[] values = DayEnum.values();
        for (DayEnum value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return DEFAULT;
    }
    }