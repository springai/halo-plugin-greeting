package com.erzip;

import com.erzip.record.*;
import com.erzip.util.ScriptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;
import run.halo.app.theme.dialect.TemplateHeadProcessor;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GreetingHeadProcessor implements TemplateHeadProcessor {
    private final ReactiveSettingFetcher settingFetcher;

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model,
        IElementModelStructureHandler structureHandler) {

        return Mono.zip(
                settingFetcher.fetch(GreetingStyleSetting.GROUP, GreetingStyleSetting.class),
                settingFetcher.fetch(NoticeStyleSetting.GROUP, NoticeStyleSetting.class),
                settingFetcher.fetch(PatternSetting.GROUP, PatternSetting.class),
                settingFetcher.fetch(NoticeSetting.GROUP, NoticeSetting.class),
                settingFetcher.fetch(GreetingSetting.GROUP, GreetingSetting.class),
                settingFetcher.fetch(MobileSetting.GROUP, MobileSetting.class)
            )
            .flatMap(tuple -> {
                GreetingStyleSetting style = tuple.getT1();
                NoticeStyleSetting noticeStyle = tuple.getT2();
                PatternSetting pattern = tuple.getT3();
                NoticeSetting notice = tuple.getT4();
                GreetingSetting greeting = tuple.getT5();
                MobileSetting mobileStyle = tuple.getT6();

                ToastConfig config;
                config = InitConfig(style, noticeStyle, pattern, notice, greeting, mobileStyle);
                Map<TimeRange, String> greetings = new LinkedHashMap<>();
                greeting.greeting_repeater().forEach(e ->
                    greetings.put(new TimeRange(e.start(), e.end()), e.content())
                );

                String styleVars = ScriptBuilder.buildStyleVariables(config);
                String jsLogic = ScriptBuilder.buildGreetingLogic(config, greetings);
                String fullScript = ScriptBuilder.buildFullScript(config, jsLogic, styleVars);

                model.add(context.getModelFactory().createText(fullScript));
                return Mono.empty();
            })
            .then();
    }


    private ToastConfig InitConfig(
        GreetingStyleSetting style,
        NoticeStyleSetting noticeStyle,
        PatternSetting pattern,
        NoticeSetting notice,
        GreetingSetting greeting,
        MobileSetting mobileStyle
    ) {
        Boolean type = "notice".equals(pattern.pattern_setting());
        return new ToastConfig(
            pattern.pattern_setting(),
            notice.notice_text(),
            greeting.finalNotice_text(),
            "fixed",
            type? noticeStyle.top() + "px" : style.top() + "px",
            type? noticeStyle.left() + "%" : style.left() + "%",
            type? noticeStyle.maxWidth() + "%" : style.maxWidth() + "%",
            "-50%",
            type? noticeStyle.background_color() : style.background_color(),
            type? noticeStyle.font_color() : style.font_color(),
            type? noticeStyle.padding() : style.padding(),
            type? noticeStyle.radius() + "px" : style.radius() + "px",
            type? noticeStyle.fontSize() + "px" : style.fontSize() + "px",
            "9999",
            "fadeInOut",
            type? 0f : style.displaySeconds(),
            "ease",
            type? noticeStyle.opacity() : style.opacity(),
            mobileStyle.mobileTop() + "px",
            mobileStyle.mobileMaxWidth() + "%",
            mobileStyle.mobileFontSize() + "px",
            mobileStyle.mobileBorderRadius() + "px",
            type?mobileStyle.mobileNoticePadding(): mobileStyle.mobilePadding()
        );
    }
}