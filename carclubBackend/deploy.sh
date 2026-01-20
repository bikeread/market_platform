#!/bin/bash

# Carclub 后端 Docker 部署脚本

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
print_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 显示帮助
show_help() {
    echo "用法: ./deploy.sh [命令]"
    echo ""
    echo "命令:"
    echo "  build       仅构建后端镜像"
    echo "  up          启动所有服务 (MySQL + Redis + 后端)"
    echo "  up-backend  仅启动后端 (使用外部数据库)"
    echo "  down        停止所有服务"
    echo "  logs        查看后端日志"
    echo "  restart     重启后端服务"
    echo "  clean       清理所有容器和镜像"
    echo ""
}

# 构建镜像
build() {
    print_info "开始构建后端镜像..."
    docker build -t carclub-backend:latest .
    print_info "构建完成!"
}

# 启动所有服务
up() {
    print_info "启动所有服务..."
    docker-compose up -d
    print_info "等待服务启动..."
    sleep 10
    docker-compose ps
    print_info "服务已启动! 访问 http://localhost:8080"
}

# 仅启动后端
up_backend() {
    print_info "仅启动后端服务..."
    docker-compose up -d carclub-backend
    print_info "后端已启动!"
}

# 停止服务
down() {
    print_info "停止所有服务..."
    docker-compose down
    print_info "服务已停止"
}

# 查看日志
logs() {
    docker-compose logs -f carclub-backend
}

# 重启后端
restart() {
    print_info "重启后端服务..."
    docker-compose restart carclub-backend
    print_info "重启完成"
}

# 清理
clean() {
    print_warn "这将删除所有容器、镜像和数据卷，确认? (y/n)"
    read -r confirm
    if [ "$confirm" = "y" ]; then
        docker-compose down -v --rmi all
        print_info "清理完成"
    else
        print_info "取消操作"
    fi
}

# 主逻辑
case "${1:-help}" in
    build)      build ;;
    up)         up ;;
    up-backend) up_backend ;;
    down)       down ;;
    logs)       logs ;;
    restart)    restart ;;
    clean)      clean ;;
    *)          show_help ;;
esac
