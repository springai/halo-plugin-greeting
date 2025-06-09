package com.erzip.record;

public record MobileSetting(
    // 新增移动端配置方法
    Integer mobileTop,
    Integer mobileMaxWidth,
    Integer mobileFontSize,
    Integer mobileBorderRadius,
    String mobileNoticePadding,
    String mobilePadding
) {
    public static final String GROUP = "mobile_setting";
}
