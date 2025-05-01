package com.erzip.record;

public record ToastConfig(
    String useFixedGreeting,
    String fixedGreeting,
    String finalGreeting,
    String position,
    String top,
    String left,
    String translateX,
    String background,
    String color,
    String padding,
    String borderRadius,
    String fontSize,
    String zIndex,
    String animationName,
    int displaySeconds,
    String animationTiming
) {}