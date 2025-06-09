package com.erzip.util;

import com.erzip.record.ToastConfig;
import com.erzip.record.TimeRange;

import java.util.Map;

public class ScriptBuilder {

    // 背景色处理逻辑
    private static String processBackgroundColor(String background, double opacity) {
        if (opacity < 1.0 && background.startsWith("#")) {
            try {
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
        return background;
    }

    public static String buildStyleVariables(ToastConfig config) {
        if ("notice".equals(config.useFixedGreeting())) {
            return null;
        }

        String background = processBackgroundColor(config.background(), config.opacity());

        return String.format(
            "const desktopStyleConfig = {\n" +
                "  position: '%s',\n" +
                "  top: '%s',\n" +
                "  left: '%s',\n" +
                "  translateX: '%s',\n" +
                "  background: '%s',\n" +
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
            background,
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
        if ("notice".equals(config.useFixedGreeting())) {
            // 处理公告模式的背景色
            String noticeBackground = processBackgroundColor(config.background(), config.opacity());

            return String.format(
                "<script>\n" +
                    "  document.addEventListener('DOMContentLoaded', function() {\n" +
                    "    if (['/', '/index.html'].includes(window.location.pathname)) {\n" +
                    "      let greeting = '%s';\n" +
                    "      \n" +
                    "      // 创建气泡容器\n" +
                    "      const toast = document.createElement('div');\n" +
                    "      toast.id = 'greeting-toast';\n" +
                    "      toast.innerHTML = `\n" +
                    "        <div class=\"content-wrapper\">\n" +
                    "          <span class=\"greeting-text\">${greeting}</span>\n" +
                    "          <button id=\"close-toast\">×</button>\n" +
                    "        </div>\n" +
                    "      `;\n" +
                    "      \n" +
                    "      // 创建样式配置对象\n" +
                    "      const desktopStyle = {\n" +
                    "        position: 'fixed',\n" +
                    "        top: '%s',\n" +
                    "        left: '%s',\n" +
                    "        transform: 'translateX(%s)',\n" +
                    "        background: '%s',\n" +
                    "        color: '%s',\n" +
                    "        padding: '%s',\n" +
                    "        borderRadius: '%s',\n" +
                    "        fontSize: '%s',\n" +
                    "        zIndex: '%s',\n" +
                    "        maxWidth: '%s'\n" +
                    "      };\n" +
                    "      \n" +
                    "      const mobileStyle = {\n" +
                    "        top: '%s',\n" +
                    "        maxWidth: '%s',\n" +
                    "        fontSize: '%s',\n" +
                    "        borderRadius: '%s',\n" +
                    "        padding: '%s'\n" +
                    "      };\n" +
                    "      \n" +
                    "      // 检测移动端\n" +
                    "      const isMobile = window.matchMedia('(max-width: 768px)').matches;\n" +
                    "      const currentStyle = {\n" +
                    "        ...desktopStyle,\n" +
                    "        ...(isMobile ? mobileStyle : {})\n" +
                    "      };\n" +
                    "      \n" +
                    "      // 应用合并后的样式\n" +
                    "      toast.style.cssText = `\n" +
                    "        position: ${currentStyle.position};\n" +
                    "        top: ${currentStyle.top};\n" +
                    "        left: ${currentStyle.left};\n" +
                    "        transform: ${currentStyle.transform};\n" +
                    "        background: ${currentStyle.background};\n" +
                    "        color: ${currentStyle.color};\n" +
                    "        padding: ${currentStyle.padding};\n" +
                    "        border-radius: ${currentStyle.borderRadius};\n" +
                    "        font-size: ${currentStyle.fontSize};\n" +
                    "        z-index: ${currentStyle.zIndex};\n" +
                    "        max-width: ${currentStyle.maxWidth};\n" +
                    "        box-shadow: 0 4px 12px rgba(0,0,0,0.15);\n" +
                    "        overflow: hidden;\n" +
                    "        display: inline-flex;\n" +
                    "        min-width: auto;\n" +
                    "        animation: fadeIn 0.5s ease forwards;\n" +
                    "        white-space: pre-wrap;\n" +
                    "        overflow-wrap: break-word;\n" +
                    "      `;\n" +
                    "      \n" +
                    "      document.body.appendChild(toast);\n" +
                    "      \n" +
                    "      // 添加关闭功能\n" +
                    "      const closeBtn = document.getElementById('close-toast');\n" +
                    "      closeBtn.addEventListener('click', function() {\n" +
                    "        toast.style.animation = 'fadeOut 0.3s forwards';\n" +
                    "        setTimeout(() => toast.remove(), 300);\n" +
                    "      });\n" +
                    "      \n" +
                    "      // 添加悬停效果\n" +
                    "      closeBtn.addEventListener('mouseenter', function() {\n" +
                    "        this.style.color = '#ff6b6b';\n" +
                    "        this.style.transform = 'scale(1.2)';\n" +
                    "      });\n" +
                    "      \n" +
                    "      closeBtn.addEventListener('mouseleave', function() {\n" +
                    "        this.style.color = 'rgba(255,255,255,0.7)';\n" +
                    "        this.style.transform = 'scale(1)';\n" +
                    "      });\n" +
                    "    }\n" +
                    "  });\n" +
                    "</script>\n" +
                    "<style>\n" +
                    "  @keyframes fadeIn {\n" +
                    "    0%% { opacity: 0; transform: translateX(-50%%) translateY(-15px); }\n" +
                    "    100%% { opacity: 1; transform: translateX(-50%%) translateY(0); }\n" +
                    "  }\n" +
                    "  \n" +
                    "  @keyframes fadeOut {\n" +
                    "    0%% { opacity: 1; transform: translateX(-50%%) translateY(0); }\n" +
                    "    100%% { opacity: 0; transform: translateX(-50%%) translateY(-15px); }\n" +
                    "  }\n" +
                    "  \n" +
                    "  #greeting-toast .content-wrapper {\n" +
                    "    display: flex;\n" +
                    "    align-items: center;\n" +
                    "    padding: 10px 20px;\n" +
                    "    position: relative;\n" +
                    "  }\n" +
                    "  \n" +
                    "  .greeting-text {\n" +
                    "    position: relative;\n" +
                    "    left: 0;\n" +
                    "    transform: none;\n" +
                    "    white-space: pre-wrap;\n" +
                    "    overflow-wrap: break-word;\n" +
                    "    padding: 0 20px;\n" +
                    "    flex-grow: 1;\n" +
                    "    text-align: center;\n" +
                    "  }\n" +
                    "  \n" +
                    "  #close-toast {\n" +
                    "    background: transparent;\n" +
                    "    color: rgba(255,255,255,0.7);\n" +
                    "    border: none;\n" +
                    "    font-size: 16px;\n" +
                    "    cursor: pointer;\n" +
                    "    transition: all 0.2s ease;\n" +
                    "    padding: 0;\n" +
                    "    width: 20px;\n" +
                    "    height: 20px;\n" +
                    "    line-height: 1;\n" +
                    "    font-weight: bold;\n" +
                    "    position: relative;\n" +
                    "    z-index: 2;\n" +
                    "    flex-shrink: 0;\n" +
                    "    margin-left: auto;\n" +
                    "  }\n" +
                    "</style>",
                config.fixedGreeting().replace("'", "\\'"),
                // 桌面端样式参数
                config.top(),
                config.left(),
                config.translateX(),
                noticeBackground,
                config.color(),
                config.padding(),
                config.borderRadius(),
                config.fontSize(),
                config.zIndex(),
                config.maxWidth(),
                // 移动端样式参数
                config.mobileTop(),
                config.mobileMaxWidth(),
                config.mobileFontSize(),
                config.mobileBorderRadius(),
                config.mobilePadding()
            );
        }

        // 问候模式
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
                "        background: ${currentStyle.background};\n" +
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