package com.erzip;


import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;



@Component
public class GreetingPlugin extends BasePlugin {
    public GreetingPlugin(PluginContext pluginContext) {
        super(pluginContext);
    }

    @Override
    public void start() {
        System.out.println("插件启动成功！");
    }

    @Override
    public void stop() {
        System.out.println("插件停止！");
    }

    @Override
    public void delete() {
        System.out.println("插件卸载！");
    }
}
