# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Carclub 汽车会员系统 - 基于 Spring Boot 2.5.12 的 B2C 会员营销系统，包含小程序端、商家端、管理后台三套 API。

## 常用命令

### 构建与运行

```bash
# 编译整个项目
mvn clean install

# 打包可执行 JAR (在 carclub-application 模块)
mvn clean package -pl carclub-application -am

# 运行应用 (端口 8080)
java -jar carclub-application/target/carclub-car-1.0.0.jar

# 使用启动脚本 (生产环境)
./sbin/start.sh carclub-application/target/carclub-car-1.0.0.jar
./sbin/stop.sh
./sbin/restart.sh
```

### 数据库初始化

```bash
# 初始化数据库 (MySQL 8.0+)
mysql -u root -p carclub-car < db/carclub-car.sql

# 执行升级脚本 (按日期顺序)
mysql -u root -p carclub-car < db/update_for_20250414.sql
# ... 依次执行其他 update_for_*.sql
```

### Docker 部署 (生产环境)

**生产服务器信息：**
- 服务器: prod-server (43.161.219.118)
- 后端端口: 8083
- API 地址: http://43.161.219.118:8083
- Swagger: http://43.161.219.118:8083/swagger-ui.html
- Docker 镜像: bikeread2008/carclub-backend:latest
- 数据库: mysql-prod (carclub-car)
- Redis: redis-prod

```bash
# 1. 本地构建镜像
docker build -t carclub-backend:latest -t bikeread2008/carclub-backend:latest .

# 2. 推送到 Docker Hub
docker push bikeread2008/carclub-backend:latest

# 3. 服务器拉取并重启
ssh prod-server "docker pull bikeread2008/carclub-backend:latest && docker stop carclub-backend-prod && docker rm carclub-backend-prod"

# 4. 启动新容器
ssh prod-server "docker run -d \
  --name carclub-backend-prod \
  --network business-network \
  --restart unless-stopped \
  -p 8083:9090 \
  -e TZ=Asia/Shanghai \
  -e JAVA_OPTS='-Xms256m -Xmx512m -XX:+UseG1GC' \
  -v carclub_upload:/app/upload \
  -v carclub_logs:/app/logs \
  bikeread2008/carclub-backend:latest"

# 快速更新 (一行命令)
docker build -t bikeread2008/carclub-backend:latest . && \
docker push bikeread2008/carclub-backend:latest && \
ssh prod-server "docker pull bikeread2008/carclub-backend:latest && docker restart carclub-backend-prod"

# 查看日志
ssh prod-server "docker logs -f carclub-backend-prod --tail 100"

# 进入容器
ssh prod-server "docker exec -it carclub-backend-prod bash"
```

**相关文件：**
- `Dockerfile` - 多阶段构建配置
- `docker-compose.prod.yml` - 生产环境编排
- `.dockerignore` - 构建忽略文件
- `deploy.sh` - 部署脚本

### 开发环境配置

修改 `configure/dev/application.properties`:
- MySQL: `spring.datasource.url`, `username`, `password`
- Redis: `spring.redis.host`, `port`, `password`
- 图片路径: `images.root`, `images.path`

## 项目架构

### 模块依赖关系

```
carclub-application (主应用, 可执行 JAR)
    └── carclub-framework (Web 框架, 分页, 异常处理)
            └── carclub-repository (MyBatis Mapper, 实体类)
                    └── carclub-utils (二维码, Excel, 加密工具)
```

### API 层划分 (carclub-application/src/main/java/com/carclub/module/)

| 目录 | 说明 | 路径前缀 |
|------|------|----------|
| `clientApi/` | 小程序端 API | `/clientApi/*` |
| `merchantApi/` | 商家/门店端 API | `/merchantApi/*` |
| `backendApi/` | 管理后台 API | `/backendApi/*` |
| `schedule/` | 定时任务 (6个 Job) | - |

### 核心业务服务 (carclub-application/src/main/java/com/carclub/common/service/)

52+ 个业务服务，按领域划分：
- 会员: `MemberService`, `UserGradeService`, `AccountService`
- 订单: `OrderService`, `CartService`, `RefundService`
- 卡券: `CouponService`, `GiveService`, `PointService`
- 商品: `GoodsService`, `StockService`
- 门店: `StoreService`, `PrinterService`

### 数据访问层 (carclub-repository/)

- `mapper/`: 67 个 MyBatis Mapper 接口
- `model/`: 64 个数据模型实体类
- XML Mapper: `src/main/resources/mapper/*.xml`

## 技术栈

- **框架**: Spring Boot 2.5.12 + Spring Security + Jetty
- **ORM**: MyBatis Plus 3.1.0 + PageHelper
- **缓存**: Ehcache (L1) + Redis (分布式会话)
- **支付**: IJPay (微信/支付宝)
- **短信**: 阿里云 SMS
- **存储**: 本地文件 / 阿里云 OSS
- **文档**: Swagger UI (`/swagger-ui.html`)

## 配置文件

| 文件 | 说明 |
|------|------|
| `carclub-application/src/main/resources/application.properties` | 基础配置 |
| `configure/dev/application.properties` | 开发环境配置 |
| `configure/prod/application.properties` | 生产环境配置 |
| `ehcache.xml` | 本地缓存配置 |
| `urlRewrite.xml` | URL 重写规则 |

环境切换通过 `env.profile=dev/prod` 和 `env.properties.path` 控制。

## 定时任务

位于 `carclub-application/src/main/java/com/carclub/module/schedule/`:

| Job | 功能 | 配置开关 |
|-----|------|----------|
| `MessageJob` | 消息发送 | `message.job.switch` |
| `CouponExpireJob` | 卡券到期处理 | `couponExpire.job.switch` |
| `OrderCancelJob` | 订单超时取消 | `orderCancel.job.switch` |
| `CommissionJob` | 佣金计算 | `commission.job.switch` |
| `UploadShippingInfoJob` | 小程序发货上传 | `uploadShippingInfoJob.job.switch` |

## 权限系统

RBAC 权限模型，相关表: `t_account`, `t_duty`, `t_duty_source`, `t_account_duty`

权限服务: `com.carclub.common.service.PermissionService`

## 外部服务依赖

| 服务 | 必需 | 配置前缀 |
|------|------|----------|
| MySQL 8.0+ | 是 | `spring.datasource.*` |
| Redis | 是 | `spring.redis.*` |
| 微信小程序/支付 | 否 | `wxpay.*` |
| 支付宝 | 否 | `alipay.*` |
| 阿里云短信 | 否 | `aliyun.sms.*` |
| 阿里云 OSS | 否 | `aliyun.oss.*` |

## 代码规范

- 实体类使用 Lombok `@Data`, `@Getter`, `@Setter`
- 控制器统一返回 `ResponseObject` (carclub-framework)
- 分页使用 `PaginationRequest` / `PaginationResponse`
- 操作日志注解: `@OperationServiceLog`
- 时间格式: `yyyy-MM-dd HH:mm:ss`
