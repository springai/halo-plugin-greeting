package com.erzip.record;

public record StyleSetting(Integer top,Integer left, Integer radius,
                           String padding, Integer fontSize, Integer displaySeconds,
                           String background_color, String font_color) {
    public static final String GROUP = "style_setting";
}
