# 多阶段构建 - 构建阶段
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /build

# 先复制 pom 文件，利用 Docker 缓存
COPY pom.xml .
COPY fuint-utils/pom.xml fuint-utils/
COPY fuint-repository/pom.xml fuint-repository/
COPY fuint-framework/pom.xml fuint-framework/
COPY fuint-application/pom.xml fuint-application/

# 复制源代码
COPY fuint-utils/src fuint-utils/src
COPY fuint-repository/src fuint-repository/src
COPY fuint-framework/src fuint-framework/src
COPY fuint-application/src fuint-application/src

# 构建项目
RUN mvn clean package -pl fuint-application -am -DskipTests -B

# 运行阶段
FROM eclipse-temurin:8-jre

LABEL maintainer="fuint"
LABEL description="Fuint 汽车会员营销系统后端"

WORKDIR /app

# 安装必要工具
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 从构建阶段复制 JAR
COPY --from=builder /build/fuint-application/target/fuint-car-1.0.0.jar app.jar

# 复制配置文件目录
COPY configure /app/configure

# 创建上传文件目录
RUN mkdir -p /app/upload

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# JVM 参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 环境配置
ENV ENV_PROFILE=prod
ENV ENV_PROPERTIES_PATH=/app/configure

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Denv.profile=$ENV_PROFILE -Denv.properties.path=$ENV_PROPERTIES_PATH -jar app.jar"]
