package com.erzip.record;

public record ToastConfig(
    String useFixedGreeting,
    String fixedGreeting,
    String finalGreeting,
    String position,
    String top,
    String left,
    String maxWidth,
    String translateX,
    String background,
    String color,
    String padding,
    String borderRadius,
    String fontSize,
    String zIndex,
    String animationName,
    Float displaySeconds,
    String animationTiming,
    // 新增移动端配置方法
    String mobileTop,
    String mobileMaxWidth,
    String mobileFontSize,
    String mobileBorderRadius,
    String mobilePadding
) {}