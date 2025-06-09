package com.erzip.record;

public record NoticeStyleSetting(Integer top, Integer left, Integer maxWidth, Integer radius,
                                 String padding, Integer fontSize,
                                 String background_color, String font_color, Float opacity) {
    public static final String GROUP = "style_setting_notice";
}
