package com.erzip.record;


import java.util.List;

public record GreetingSetting(List<GreetingRepeater> greeting_repeater, String finalNotice_text) {
    public static final String GROUP = "greeting_setting";
}
