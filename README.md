# Halo-Plugin-Greeting
一款轻量灵活的网站交互增强插件，专为Halo建站系统设计，支持通过可定制的气泡提示框为访客提供动态公告或个性化问候。
通过简洁的后台管理界面，用户无需代码即可快速配置，实时预览效果。

## 特性
- 双模式智能切换：自由选择公告模式（展示网站重要通知、活动推广）或问候语模式（为新访客/回头客提供欢迎语），精准传递信息。
- 内容高度自定义：支持编辑公告内容、问候语内容，并可设置动画时长（单位秒）。
- 气泡样式随心配：提供气泡框颜色、字体颜色、圆角、字体大小、位置（侧边/顶部浮动）等样式配置，适配不同网站主题风格。
- 响应式适配：提供PC、移动端设备不同样式设置，确保提示框美观且交互友好。

## 使用方法
1. 下载jar包  
   进入[releases](https://github.com/springai/halo-plugin-greeting/releases)页面下载对应版本的jar包，
2. 安装jar包  
   下载jar包后，进入Halo后台插件页面点击右上角的安装，选择本地上传即可

## 预览
![halo-plugin-greeting](https://erzip.com/upload/022.avif)

## 开发环境

插件开发的详细文档请查阅：<https://docs.halo.run/developer-guide/plugin/introduction>

所需环境：

1. Java 17
2. Node 20
3. pnpm 9
4. Docker (可选)

克隆项目：

```bash
git clone git@github.com:halo-sigs/plugin-starter.git

# 或者当你 fork 之后

git clone git@github.com:{your_github_id}/plugin-starter.git
```

```bash
cd path/to/plugin-starter
```

### 运行方式 1（推荐）

> 此方式需要本地安装 Docker

```bash
# macOS / Linux
./gradlew pnpmInstall

# Windows
./gradlew.bat pnpmInstall
```

```bash
# macOS / Linux
./gradlew haloServer

# Windows
./gradlew.bat haloServer
```

执行此命令后，会自动创建一个 Halo 的 Docker 容器并加载当前的插件，更多文档可查阅：<https://docs.halo.run/developer-guide/plugin/basics/devtools>

### 运行方式 2

> 此方式需要使用源码运行 Halo

编译插件：

```bash
# macOS / Linux
./gradlew build

# Windows
./gradlew.bat build
```

修改 Halo 配置文件：

```yaml
halo:
  plugin:
    runtime-mode: development
    fixedPluginPath:
      - "/path/to/plugin-starter"
```

最后重启 Halo 项目即可。
