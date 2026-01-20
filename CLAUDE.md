# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Carclub 汽车会员营销系统 - 开源的多平台实体店铺会员管理和营销解决方案。采用前后端分离架构，包含 Java 后端、多个 Vue.js/Uniapp 前端及 Electron 桌面应用。

## 仓库结构

```
market/
├── carclubBackend/      # Spring Boot 后端 (后台+小程序+收银接口)
├── carclubAdmin/        # 管理后台前端 (Vue + Element-UI)
├── carclubUniapp/       # 会员端小程序/H5 (Uniapp)
├── carclubCashier/      # 收银系统桌面版 (Electron + Vue)
├── carclubCashierWeb/   # 收银系统网页版 (Vue + Element-UI)
├── 安装配置文档/       # 安装配置说明
└── 需求文档/          # 需求文档
```

## 常用命令

### 后端 (carclubBackend)

```bash
# 编译
mvn clean install

# 打包 JAR
mvn clean package -pl fuint-application -am

# 运行 (端口 8080)
java -jar fuint-application/target/carclub-car-1.0.0.jar

# 生产环境脚本
./sbin/start.sh fuint-application/target/carclub-car-1.0.0.jar
./sbin/stop.sh
./sbin/restart.sh

# 数据库初始化
mysql -u root -p carclub-car < db/carclub-car.sql
mysql -u root -p carclub-car < db/update_for_*.sql  # 按日期顺序执行
```

### 管理后台 (carclubAdmin)

```bash
npm install  # 或 npm install --registry=https://registry.npmmirror.com
NODE_OPTIONS=--openssl-legacy-provider npm run dev  # 开发模式 (端口 1024)
NODE_OPTIONS=--openssl-legacy-provider npm run build:prod  # 生产构建
npm run lint  # ESLint 检查
```

> **注意**: Node.js 17+ 需要添加 `NODE_OPTIONS=--openssl-legacy-provider` 解决 OpenSSL 兼容性问题

### 收银网页版 (carclubCashierWeb)

```bash
npm install
npm run dev  # 开发模式 (端口 8081)
npm run build:prod
```

### 收银桌面版 (carclubCashier)

```bash
yarn install
yarn dev
yarn build  # 打包 exe
yarn build:win32  # 32位 Windows
yarn build:win64  # 64位 Windows
```

### 会员端 (carclubUniapp)

使用 HBuilderX IDE 导入并编译，需先修改 `manifest.json` 中的 appid。

## 架构概览

### 后端模块依赖

```
fuint-application (主应用)
    └── fuint-framework (Web框架/分页/异常)
            └── fuint-repository (MyBatis Mapper/实体类)
                    └── fuint-utils (工具类)
```

### 后端 API 层

| 目录 | 说明 | 路径前缀 |
|------|------|----------|
| `clientApi/` | 小程序端 API | `/clientApi/*` |
| `merchantApi/` | 商家/门店端 API | `/merchantApi/*` |
| `backendApi/` | 管理后台 API | `/backendApi/*` |

### 技术栈

**后端**: Spring Boot 2.5.12, MyBatis Plus 3.1.0, Spring Security, JWT, Redis, Ehcache, Jetty
**前端**: Vue 2.6, Element-UI, Axios, Vuex
**小程序**: Uniapp, uView-UI
**桌面端**: Electron, electron-builder

## 开发环境配置

### 必需服务

- MySQL 8.0+ (配置: `configure/dev/application.properties`)
- Redis (默认: localhost:6379, 密码: carclub123)

### 开发账号

后台登录: `fuint` / `123456`

### 前端环境变量

```
VUE_APP_BASE_API=/dev-api
VUE_APP_SERVER_URL=http://localhost:8080/
```

## 关键配置文件

| 项目 | 配置文件 |
|------|----------|
| 后端开发环境 | `carclubBackend/configure/dev/application.properties` |
| 后端生产环境 | `carclubBackend/configure/prod/application.properties` |
| 管理后台 | `carclubAdmin/.env.development`, `.env.production` |
| 小程序 | `carclubUniapp/manifest.json` (appid 必须修改) |

## 代码规范

### 后端

- 实体类使用 Lombok `@Data`
- 统一返回 `ResponseObject`
- 分页使用 `PaginationRequest` / `PaginationResponse`
- 操作日志注解: `@OperationServiceLog`
- 时间格式: `yyyy-MM-dd HH:mm:ss`

### 前端 (ESLint)

- 2 空格缩进
- 单引号
- 无分号
- 组件名 PascalCase

## 注意事项

- carclubUniapp 的 `manifest.json` appid 必须修改，否则影响登录和支付
- 后端配置需修改: 数据库、Redis、微信 appId/appSecret
- 数据库升级脚本需按日期顺序执行
- 无自动化测试套件，需手动测试
