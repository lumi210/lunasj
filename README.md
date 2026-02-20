# LunaTV Android App

LunaTV Android 应用 - 自动适配手机和电视设备

## 功能特性

- **自动设备识别**: 启动时自动检测设备类型（手机/平板/电视），加载对应的界面
- **手机模式**: 标准的移动端 WebView 应用体验
- **电视模式**: 专为 Android TV 优化，支持遥控器操作
- **全屏沉浸式体验**: 沉浸式播放体验
- **媒体控制**: 支持媒体按键控制播放

## 设备支持

### 手机和平板
- 标准 Android 手机和平板设备
- 触屏操作优化
- 支持横竖屏切换

### Android TV
- Android TV 设备自动识别
- Leanback 界面支持
- 遥控器按键支持：
  - 方向键导航
  - 确认/播放键
  - 快进/快退键
  - 菜单键

## 构建说明

### 环境要求
- Android SDK 34
- JDK 17+
- Gradle 8.4+

### 构建命令

```bash
# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本
./gradlew assembleRelease
```

### APK 输出位置
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## 默认配置

应用默认加载 LunaTV 网站：`https://lunatv.smone.us`

如需修改默认地址，编辑 `app/src/main/java/com/lunatv/app/utils/AppConstants.kt` 中的 `DEFAULT_URL` 常量。

## 技术栈

- **Kotlin** - 主要开发语言
- **AndroidX** - Android 扩展库
- **WebView** - 网页渲染
- **Leanback** - Android TV 支持
- **Material Components** - UI 组件

## 项目结构

```
app/src/main/
├── java/com/lunatv/app/
│   ├── LunaTVApplication.kt    # Application 类
│   ├── SplashActivity.kt       # 启动页（设备检测）
│   ├── ui/
│   │   ├── BaseActivity.kt     # 基础 Activity
│   │   ├── MainActivity.kt     # 手机/平板模式
│   │   └── TVMainActivity.kt   # 电视模式
│   ├── utils/
│   │   ├── AppConstants.kt     # 常量定义
│   │   └── DeviceUtils.kt      # 设备检测工具
│   └── webview/
│       └── LunaWebView.kt      # WebView 封装
├── res/
│   ├── layout/                  # 布局文件
│   ├── values/                  # 资源值
│   ├── values-zh-rCN/           # 中文资源
│   ├── drawable/                # 图形资源
│   └── mipmap-*/                # 应用图标
└── AndroidManifest.xml          # 清单文件
```

## 设备检测逻辑

应用通过以下特征判断设备类型：

1. **电视设备检测**
   - UI Mode 为 TELEVISION
   - 支持 Leanback 特性
   - 支持 Television 特性
   - 无触摸屏支持

2. **平板设备检测**
   - 大屏幕或超大屏幕
   - 非电视设备
   - 非手机设备

3. **手机设备检测**
   - UI Mode 为 PHONE
   - 支持电话功能

## 许可证

本项目基于 LunaTV 开发，遵循其原有许可证。

## 相关项目

- [LunaTV](https://github.com/lumi210/LunaTV) - 原始 Web 项目
