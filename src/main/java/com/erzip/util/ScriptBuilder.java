package com.erzip.util;

import com.erzip.record.ToastConfig;
import com.erzip.record.TimeRange;

import java.util.Map;

public class ScriptBuilder {

    public static String buildStyleVariables(ToastConfig config) {
        return String.format(
            "const styleConfig = {\n" +
                "  position: '%s',\n" +
                "  top: '%s',\n" +
                "  left: '%s',\n" +
                "  translateX: '%s',\n" +
                "  background: '%s',\n" +
                "  color: '%s',\n" +
                "  padding: '%s',\n" +
                "  borderRadius: '%s',\n" +
                "  fontSize: '%s',\n" +
                "  zIndex: '%s'\n" +
                "};\n" +
                "const animationConfig = {\n" +
                "  name: '%s',\n" +
                "  duration: %d,\n" +
                "  timing: '%s'\n" +
                "};",
            config.position(),
            config.top(),
            config.left(),
            config.translateX(),
            config.background(),
            config.color(),
            config.padding(),
            config.borderRadius(),
            config.fontSize(),
            config.zIndex(),
            config.animationName(),
            config.displaySeconds() * 1000,
            config.animationTiming()
        );
    }

    public static String buildGreetingLogic(ToastConfig config, Map<TimeRange, String> greetings) {
        if ("notice".equals(config.useFixedGreeting())) {
            return String.format("let greeting = '%s';\n",
                config.fixedGreeting().replace("'", "\\'"));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("const hour = new Date().getHours();\n")
            .append("let greeting;\n");

        boolean isFirst = true;
        for (Map.Entry<TimeRange, String> entry : greetings.entrySet()) {
            TimeRange range = entry.getKey();
            String condition = range.start() < range.end() ?
                String.format("hour >= %d && hour < %d", range.start(), range.end()) :
                String.format("hour >= %d || hour < %d", range.start(), range.end());

            String clause = isFirst ? "if" : "else if";
            sb.append(clause)
                .append(" (")
                .append(condition)
                .append(") {\n")
                .append("  greeting = '")
                .append(entry.getValue().replace("'", "\\'"))
                .append("';\n")
                .append("}\n");

            isFirst = false;
        }

        sb.append("else {\n")
            .append("  greeting = '")
            .append(config.finalGreeting().replace("'", "\\'"))
            .append("';\n")
            .append("}\n");

        return sb.toString();
    }

    public static String buildFullScript(ToastConfig config, String jsLogic, String styleVars) {
        return String.format(
            "<script>\n" +
                "  document.addEventListener('DOMContentLoaded', () => {\n" +
                "    if (['/', '/index.html'].includes(window.location.pathname)) {\n" +
                "      %s\n" +  // styleVars
                "      %s\n" +  // jsLogic
                "      const toast = document.createElement('div');\n" +
                "      toast.textContent = greeting;\n" +
                "      toast.style = `\n" +
                "        position: ${styleConfig.position};\n" +
                "        top: ${styleConfig.top};\n" +
                "        left: ${styleConfig.left};\n" +
                "        transform: translateX(${styleConfig.translateX});\n" +
                "        background: ${styleConfig.background};\n" +
                "        color: ${styleConfig.color};\n" +
                "        padding: ${styleConfig.padding};\n" +
                "        border-radius: ${styleConfig.borderRadius};\n" +
                "        font-size: ${styleConfig.fontSize};\n" +
                "        z-index: ${styleConfig.zIndex};\n" +
                "        animation: ${animationConfig.name} ${animationConfig.duration}ms ${animationConfig.timing};\n" +
                "      `;\n" +
                "      document.body.appendChild(toast);\n" +
                "      setTimeout(() => toast.remove(), animationConfig.duration);\n" +
                "    }\n" +
                "  });\n" +
                "</script>\n" +
                "<style>\n" +
                "  @keyframes %s {\n" +  // animationName
                "    0%% { opacity: 0; transform: translateX(-50%%) translateY(-10px); }\n" +
                "    20%% { opacity: 1; transform: translateX(-50%%) translateY(0); }\n" +
                "    80%% { opacity: 1; transform: translateX(-50%%) translateY(0); }\n" +
                "    100%% { opacity: 0; transform: translateX(-50%%) translateY(-10px); }\n" +
                "  }\n" +
                "</style>",
            styleVars,
            jsLogic,
            config.animationName()
        );
    }
}