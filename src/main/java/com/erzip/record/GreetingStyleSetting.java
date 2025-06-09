package com.erzip.record;

public record GreetingStyleSetting(Integer top, Integer left, Integer maxWidth, Integer radius,
                                   String padding, Integer fontSize, Float displaySeconds,
                                   String background_color, String font_color, Float opacity) {
    public static final String GROUP = "style_setting_greeting";
}
