package com.erzip.util;

import com.erzip.record.ToastConfig;
import com.erzip.record.TimeRange;

import java.util.Map;

public class ScriptBuilder {

    public static String buildStyleVariables(ToastConfig config) {
        // 处理背景透明度
        String background = config.background();
        double opacity = config.opacity();

        // 如果设置了透明度且不是1.0，则转换为RGBA格式
        if (opacity < 1.0 && background.startsWith("#")) {
            try {
                // 处理简写颜色代码如 #abc
                if (background.length() == 4) {
                    char r = background.charAt(1);
                    char g = background.charAt(2);
                    char b = background.charAt(3);
                    background = "#" + r + r + g + g + b + b;
                }

                if (background.length() == 7) {
                    int r = Integer.parseInt(background.substring(1, 3), 16);
                    int g = Integer.parseInt(background.substring(3, 5), 16);
                    int b = Integer.parseInt(background.substring(5, 7), 16);
                    background = String.format("rgba(%d, %d, %d, %.2f)", r, g, b, opacity);
                }
            } catch (Exception e) {
                // 转换失败时保持原背景色
            }
        }

        return String.format(
            "const desktopStyleConfig = {\n" +
                "  position: '%s',\n" +
                "  top: '%s',\n" +
                "  left: '%s',\n" +
                "  translateX: '%s',\n" +
                "  background: '%s',\n" +  // 使用处理后的背景色
                "  color: '%s',\n" +
                "  padding: '%s',\n" +
                "  borderRadius: '%s',\n" +
                "  fontSize: '%s',\n" +
                "  zIndex: '%s',\n" +
                "  maxWidth: '%s'\n" +
                "};\n" +
                "const mobileStyleConfig = {\n" +
                "  top: '%s',\n" +
                "  maxWidth: '%s',\n" +
                "  fontSize: '%s',\n" +
                "  borderRadius: '%s',\n" +
                "  padding: '%s'\n" +
                "};\n" +
                "const animationConfig = {\n" +
                "  name: '%s',\n" +
                "  duration: %s,\n" +
                "  timing: '%s'\n" +
                "};",
            config.position(),
            config.top(),
            config.left(),
            config.translateX(),
            background,  // 使用处理后的背景色
            config.color(),
            config.padding(),
            config.borderRadius(),
            config.fontSize(),
            config.zIndex(),
            config.maxWidth(),
            // 移动端配置
            config.mobileTop(),
            config.mobileMaxWidth(),
            config.mobileFontSize(),
            config.mobileBorderRadius(),
            config.mobilePadding(),
            // 动画配置
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
                "      const isMobile = window.matchMedia('(max-width: 768px)').matches;\n" +
                "      const currentStyle = {\n" +
                "        ...desktopStyleConfig,\n" +
                "        ...(isMobile ? mobileStyleConfig : {})\n" +
                "      };\n" +
                "      const toast = document.createElement('div');\n" +
                "      toast.textContent = greeting;\n" +
                "      toast.style = `\n" +
                "        position: ${currentStyle.position};\n" +
                "        top: ${currentStyle.top};\n" +
                "        left: ${currentStyle.left};\n" +
                "        transform: translateX(${currentStyle.translateX});\n" +
                "        background: ${currentStyle.background};\n" + // 支持RGBA
                "        color: ${currentStyle.color};\n" +
                "        padding: ${currentStyle.padding};\n" +
                "        border-radius: ${currentStyle.borderRadius};\n" +
                "        font-size: ${currentStyle.fontSize};\n" +
                "        z-index: ${currentStyle.zIndex};\n" +
                "        max-width: ${currentStyle.maxWidth};\n" +
                "        white-space: pre-wrap;\n" +
                "        overflow-wrap: break-word;\n" +
                "        animation: ${animationConfig.name} ${animationConfig.duration}ms ${animationConfig.timing};\n" +
                "        animation-fill-mode: forwards;\n" +
                "      `;\n" +
                "      document.body.appendChild(toast);\n" +
                "      toast.addEventListener('animationend', () => {\n" +
                "        toast.remove();\n" +
                "      });\n" +
                "    }\n" +
                "  });\n" +
                "</script>\n" +
                "<style>\n" +
                "  @keyframes %s {\n" +
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