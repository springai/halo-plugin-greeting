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
                settingFetcher.fetch(PatternSetting.GROUP, PatternSetting.class),
                settingFetcher.fetch(NoticeSetting.GROUP, NoticeSetting.class),
                settingFetcher.fetch(GreetingSetting.GROUP, GreetingSetting.class),
                settingFetcher.fetch(MobileSetting.GROUP,MobileSetting.class)
            )
            .flatMap(tuple -> {
                GreetingStyleSetting style = tuple.getT1();
                PatternSetting pattern = tuple.getT2();
                NoticeSetting notice = tuple.getT3();
                GreetingSetting greeting = tuple.getT4();
                MobileSetting mobileStyle = tuple.getT5();

                ToastConfig config = new ToastConfig(
                    pattern.pattern_setting(),
                    notice.notice_text(),
                    greeting.finalNotice_text(),
                    "fixed",
                    style.top() + "px",
                    style.left() + "%",
                    style.maxWidth() + "%",
                    "-50%",
                    style.background_color(),
                    style.font_color(),
                    style.padding(),
                    style.radius() + "px",
                    style.fontSize() + "px",
                    "9999",
                    "fadeInOut",
                    style.displaySeconds(),
                    "ease",
                    style.opacity(),
                    mobileStyle.mobileTop() + "px",
                    mobileStyle.mobileMaxWidth() + "%",
                    mobileStyle.mobileFontSize() + "px",
                    mobileStyle.mobileBorderRadius() + "px",
                    mobileStyle.mobilePadding()
                );

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
}