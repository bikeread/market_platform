# Fuint → Carclub 品牌重命名迁移方案

> 创建时间: 2026-01-20
> 状态: 待执行
> 预估影响文件: 约953个

---

## 目录

1. [替换映射表](#1-替换映射表)
2. [阶段一：准备工作](#阶段一准备工作)
3. [阶段二：目录重命名](#阶段二目录重命名)
4. [阶段三：文件重命名](#阶段三文件重命名)
5. [阶段四：Java包名重构](#阶段四java包名重构)
6. [阶段五：文件内容批量替换](#阶段五文件内容批量替换)
7. [阶段六：数据库处理](#阶段六数据库处理)
8. [阶段七：文档更新](#阶段七文档更新)
9. [阶段八：验证与测试](#阶段八验证与测试)
10. [阶段九：提交变更](#阶段九提交变更)
11. [手动处理项目](#手动处理项目)
12. [执行进度跟踪](#执行进度跟踪)

---

## 1. 替换映射表

| 原值 | 新值 | 说明 |
|-----|-----|------|
| `fuint` | `carclub` | 小写形式 |
| `Fuint` | `Carclub` | 首字母大写 |
| `FUINT` | `CARCLUB` | 全大写 |
| `fuint-car` | `carclub-car` | 数据库/构件名 |
| `$fuint-theme` | `$carclub-theme` | SCSS变量 |
| `com.fuint` | `com.carclub` | Java包名 |
| `cn.fuint` | `cn.carclub` | App ID |
| `www.fuint.cn` | `(你的新域名)` | 网站域名 |
| `fuint123` | `carclub123` | Redis密码(建议更换更安全的) |

---

## 阶段一：准备工作

### 1.1 创建备份和新分支

```bash
# 进入项目目录
cd /home/bikeread/projects/market_platform

# 暂存当前修改（如有）
git stash

# 创建新分支
git checkout -b refactor/rename-to-carclub

# 确保工作目录状态
git status
```

### 1.2 检查点
- [ ] 已创建新分支
- [ ] 工作目录已准备好

---

## 阶段二：目录重命名

### 2.1 后端子模块重命名（先子目录后父目录）

```bash
# 重命名后端子模块
mv fuintBackend/fuint-utils fuintBackend/carclub-utils
mv fuintBackend/fuint-repository fuintBackend/carclub-repository
mv fuintBackend/fuint-framework fuintBackend/carclub-framework
mv fuintBackend/fuint-application fuintBackend/carclub-application
```

### 2.2 主目录重命名

```bash
# 重命名主项目目录
mv fuintBackend carclubBackend
mv fuintAdmin carclubAdmin
mv fuintUniapp carclubUniapp
mv fuintCashier carclubCashier
mv fuintCashierWeb carclubCashierWeb
```

### 2.3 检查点
- [ ] fuintBackend → carclubBackend
- [ ] fuintAdmin → carclubAdmin
- [ ] fuintUniapp → carclubUniapp
- [ ] fuintCashier → carclubCashier
- [ ] fuintCashierWeb → carclubCashierWeb
- [ ] fuint-utils → carclub-utils
- [ ] fuint-repository → carclub-repository
- [ ] fuint-framework → carclub-framework
- [ ] fuint-application → carclub-application

---

## 阶段三：文件重命名

### 3.1 Java主类文件

```bash
mv carclubBackend/carclub-application/src/main/java/com/fuint/fuintApplication.java \
   carclubBackend/carclub-application/src/main/java/com/fuint/carclubApplication.java
```

### 3.2 SCSS文件

```bash
mv carclubAdmin/src/assets/styles/fuint.scss \
   carclubAdmin/src/assets/styles/carclub.scss

mv carclubCashierWeb/src/assets/styles/fuint.scss \
   carclubCashierWeb/src/assets/styles/carclub.scss

mv carclubCashier/src/renderer/styles/fuint.scss \
   carclubCashier/src/renderer/styles/carclub.scss
```

### 3.3 JS工具文件

```bash
mv carclubAdmin/src/utils/fuint.js \
   carclubAdmin/src/utils/carclub.js

mv carclubCashierWeb/src/utils/fuint.js \
   carclubCashierWeb/src/utils/carclub.js

mv carclubCashier/src/renderer/utils/fuint.js \
   carclubCashier/src/renderer/utils/carclub.js
```

### 3.4 Vue组件目录

```bash
mv carclubAdmin/src/components/Fuint \
   carclubAdmin/src/components/Carclub
```

### 3.5 SQL和其他文件

```bash
mv carclubBackend/db/fuint-car.sql \
   carclubBackend/db/carclub-car.sql

mv 安装配置文档/fuint开发必读.txt \
   安装配置文档/carclub开发必读.txt

mv carclubBackend/sbin/yanhe.fuint \
   carclubBackend/sbin/yanhe.carclub
```

### 3.6 检查点
- [ ] Java主类已重命名
- [ ] 3个SCSS文件已重命名
- [ ] 3个JS工具文件已重命名
- [ ] Vue组件目录已重命名
- [ ] SQL文件已重命名
- [ ] 文档文件已重命名

---

## 阶段四：Java包名重构

### 4.1 移动Java包目录结构

```bash
cd carclubBackend

# 各模块的包目录移动
for module in carclub-utils carclub-repository carclub-framework carclub-application; do
  if [ -d "$module/src/main/java/com/fuint" ]; then
    mkdir -p "$module/src/main/java/com/carclub"
    cp -r "$module/src/main/java/com/fuint"/* "$module/src/main/java/com/carclub/"
    rm -rf "$module/src/main/java/com/fuint"
  fi
done

cd ..
```

### 4.2 检查点
- [ ] carclub-utils 包目录已移动
- [ ] carclub-repository 包目录已移动
- [ ] carclub-framework 包目录已移动
- [ ] carclub-application 包目录已移动

---

## 阶段五：文件内容批量替换

### 5.1 Java包名替换

```bash
# 替换所有Java文件中的包名
find carclubBackend -name "*.java" -exec sed -i 's/com\.fuint/com.carclub/g' {} \;

# 替换MyBatis XML映射文件
find carclubBackend -name "*.xml" -exec sed -i 's/com\.fuint/com.carclub/g' {} \;
```

### 5.2 Maven配置替换 (pom.xml)

```bash
find carclubBackend -name "pom.xml" -exec sed -i \
  -e 's/<groupId>com\.fuint<\/groupId>/<groupId>com.carclub<\/groupId>/g' \
  -e 's/<artifactId>fuint<\/artifactId>/<artifactId>carclub<\/artifactId>/g' \
  -e 's/<artifactId>fuint-/<artifactId>carclub-/g' \
  -e 's/fuint project root/carclub project root/g' {} \;
```

### 5.3 开发环境配置替换 (application.properties)

```bash
sed -i \
  -e 's/fuint-car/carclub-car/g' \
  -e 's/spring\.session\.redis\.namespace=fuint/spring.session.redis.namespace=carclub/g' \
  -e 's/spring\.redis\.password=fuint123/spring.redis.password=carclub123/g' \
  -e 's/system\.name = fuint/system.name = carclub/g' \
  -e 's/fuint汽车会员系统/carclub汽车会员系统/g' \
  -e 's/www\.fuint\.cn/your-domain.com/g' \
  -e 's/fuint-application/carclub-application/g' \
  -e 's/\/usr\/local\/fuint\//\/usr\/local\/carclub\//g' \
  -e 's/logging\.level\.com\.fuint/logging.level.com.carclub/g' \
  -e 's/fuintBackend/carclubBackend/g' \
  carclubBackend/configure/dev/application.properties
```

### 5.4 生产环境配置替换

```bash
sed -i \
  -e 's/fuint-car/carclub-car/g' \
  -e 's/spring\.session\.redis\.namespace=fuint/spring.session.redis.namespace=carclub/g' \
  -e 's/system\.name = fuint/system.name = carclub/g' \
  -e 's/fuint汽车会员系统/carclub汽车会员系统/g' \
  -e 's/www\.fuint\.cn/your-domain.com/g' \
  -e 's/fuint-application/carclub-application/g' \
  -e 's/\/usr\/local\/fuint\//\/usr\/local\/carclub\//g' \
  carclubBackend/configure/prod/application.properties
```

### 5.5 主配置文件替换

```bash
sed -i \
  -e 's/fuintBackend/carclubBackend/g' \
  -e 's/com\.fuint/com.carclub/g' \
  carclubBackend/carclub-application/src/main/resources/application.properties
```

### 5.6 日志配置替换

```bash
sed -i 's/\/data\/log\/fuint\//\/data\/log\/carclub\//g' \
  carclubBackend/carclub-application/src/main/resources/logback-spring.xml
```

### 5.7 前端 package.json 替换

**carclubAdmin:**
```bash
sed -i \
  -e 's/"name": "fuintAdmin"/"name": "carclubAdmin"/g' \
  -e 's/fuint汽车会员系统后台管理端/carclub汽车会员系统后台管理端/g' \
  -e 's/gitee\.com\/fuint\/fuint-uniapp\.git/github.com\/your-org\/carclub-uniapp.git/g' \
  carclubAdmin/package.json
```

**carclubCashierWeb:**
```bash
sed -i \
  -e 's/"name": "fuintCashier"/"name": "carclubCashier"/g' \
  -e 's/fuint收银系统/carclub收银系统/g' \
  -e 's/gitee\.com\/fuint\/fuint-uniapp\.git/github.com\/your-org\/carclub-uniapp.git/g' \
  carclubCashierWeb/package.json
```

**carclubCashier (Electron):**
```bash
sed -i \
  -e 's/"name": "fuintCashier"/"name": "carclubCashier"/g' \
  -e 's/"productName": "fuint收银系统"/"productName": "carclub收银系统"/g' \
  -e 's/"appId": "cn\.fuint\.cashier"/"appId": "cn.carclub.cashier"/g' \
  carclubCashier/package.json
```

**carclubUniapp manifest.json:**
```bash
sed -i \
  -e 's/fuint汽车会员系统/carclub汽车会员系统/g' \
  -e 's/fuint汽车会员营销系统/carclub汽车会员营销系统/g' \
  -e 's/www\.fuint\.cn/your-domain.com/g' \
  carclubUniapp/manifest.json
```

### 5.8 vue.config.js 替换

```bash
sed -i \
  -e 's/fuint会员营销管理系统/carclub会员营销管理系统/g' \
  -e 's/www\.fuint\.cn/your-domain.com/g' \
  carclubAdmin/vue.config.js

sed -i \
  -e 's/fuint会员营销管理系统/carclub会员营销管理系统/g' \
  -e 's/www\.fuint\.cn/your-domain.com/g' \
  carclubCashierWeb/vue.config.js
```

### 5.9 SCSS变量替换

```bash
# 替换变量定义
sed -i 's/\$fuint-theme/\$carclub-theme/g' carclubUniapp/uni.scss

# 替换所有Vue文件中的变量引用
find carclubUniapp -name "*.vue" -exec sed -i 's/\$fuint-theme/\$carclub-theme/g' {} \;
find carclubUniapp -name "*.scss" -exec sed -i 's/\$fuint-theme/\$carclub-theme/g' {} \;
```

### 5.10 JS导入路径替换

```bash
# carclubAdmin
find carclubAdmin -name "*.js" -exec sed -i \
  -e "s/@\/utils\/fuint/@\/utils\/carclub/g" \
  -e "s/@\/assets\/styles\/fuint\.scss/@\/assets\/styles\/carclub.scss/g" {} \;

find carclubAdmin -name "*.vue" -exec sed -i \
  -e "s/@\/utils\/fuint/@\/utils\/carclub/g" {} \;

# carclubCashierWeb
find carclubCashierWeb -name "*.js" -exec sed -i \
  -e "s/@\/utils\/fuint/@\/utils\/carclub/g" \
  -e "s/@\/assets\/styles\/fuint\.scss/@\/assets\/styles\/carclub.scss/g" {} \;

find carclubCashierWeb -name "*.vue" -exec sed -i \
  -e "s/@\/utils\/fuint/@\/utils\/carclub/g" {} \;

# carclubCashier
find carclubCashier -name "*.js" -exec sed -i \
  -e "s/@\/utils\/fuint/@\/utils\/carclub/g" \
  -e "s/@\/styles\/fuint\.scss/@\/styles\/carclub.scss/g" {} \;

find carclubCashier -name "*.vue" -exec sed -i \
  -e "s/@\/utils\/fuint/@\/utils\/carclub/g" {} \;
```

### 5.11 Vue组件名替换

```bash
# 替换组件引用
find carclubAdmin -name "*.vue" -exec sed -i \
  -e 's/FuintDoc/CarclubDoc/g' \
  -e 's/FuintQrCode/CarclubQrCode/g' \
  -e 's/FuintCashier/CarclubCashier/g' \
  -e 's/FuintGit/CarclubGit/g' \
  -e 's/fuint-doc/carclub-doc/g' \
  -e "s/@\/components\/Fuint/@\/components\/Carclub/g" {} \;

# 更新组件内部name属性
sed -i "s/name: 'FuintDoc'/name: 'CarclubDoc'/g" \
  carclubAdmin/src/components/Carclub/Doc/index.vue

sed -i "s/name: 'FuintQrCode'/name: 'CarclubQrCode'/g" \
  carclubAdmin/src/components/Carclub/QrCode/index.vue

sed -i "s/name: 'FuintCashier'/name: 'CarclubCashier'/g" \
  carclubAdmin/src/components/Carclub/Cashier/index.vue

sed -i "s/name: 'FuintGit'/name: 'CarclubGit'/g" \
  carclubAdmin/src/components/Carclub/Git/index.vue
```

### 5.12 版权声明替换

```bash
find . -type f \( -name "*.js" -o -name "*.java" -o -name "*.scss" -o -name "*.vue" \) \
  -exec sed -i \
  -e 's/Copyright (c) 2022 fuint/Copyright (c) 2022 carclub/g' \
  -e 's/Copyright (c) 2023 https:\/\/www\.fuint\.cn/Copyright (c) 2023 your-domain.com/g' \
  -e 's/CopyRight https:\/\/www\.fuint\.cn/CopyRight your-domain.com/g' {} \;
```

### 5.13 登录默认账号替换

```bash
sed -i 's/username: "fuint"/username: "admin"/g' carclubAdmin/src/views/login.vue
```

### 5.14 URL链接替换

```bash
# 替换Git仓库链接
find . -type f \( -name "*.vue" -o -name "*.js" -o -name "*.json" \) \
  -exec sed -i \
  -e 's/gitee\.com\/fuint\/fuint/github.com\/your-org\/carclub/g' \
  -e 's/gitee\.com\/fuint/github.com\/your-org/g' {} \;

# 替换文档和下载链接
find . -type f \( -name "*.vue" -o -name "*.js" \) \
  -exec sed -i \
  -e 's/www\.fuint\.cn\/docs/your-domain.com\/docs/g' \
  -e 's/www\.fuint\.cn\/download/your-domain.com\/download/g' {} \;
```

### 5.15 收银路径替换

```bash
# Admin中的收银路径
find carclubAdmin -name "*.vue" -exec sed -i \
  -e 's/\/fuintCarCashier\//\/carclubCarCashier\//g' {} \;
```

### 5.16 Electron菜单配置替换

```bash
sed -i 's/fuint收银系统/carclub收银系统/g' \
  carclubCashier/src/main/config/menu.js
```

### 5.17 检查点
- [ ] Java包名已替换
- [ ] Maven配置已替换
- [ ] 开发环境配置已替换
- [ ] 生产环境配置已替换
- [ ] 日志配置已替换
- [ ] 前端package.json已替换
- [ ] vue.config.js已替换
- [ ] SCSS变量已替换
- [ ] JS导入路径已替换
- [ ] Vue组件名已替换
- [ ] 版权声明已替换
- [ ] 登录账号已替换
- [ ] URL链接已替换

---

## 阶段六：数据库处理

### 6.1 SQL文件内容替换

```bash
# 替换SQL文件中的操作人字段
sed -i "s/'fuint'/'carclub'/g" carclubBackend/db/carclub-car.sql
```

### 6.2 数据库重命名（生产环境）

```sql
-- 方式一：创建新库并迁移数据
CREATE DATABASE `carclub-car` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 导出旧库
-- mysqldump -u root -p fuint-car > fuint-car-backup.sql

-- 替换SQL文件中的数据库名
-- sed -i 's/fuint-car/carclub-car/g' fuint-car-backup.sql

-- 导入新库
-- mysql -u root -p carclub-car < fuint-car-backup.sql

-- 确认无误后删除旧库（谨慎操作）
-- DROP DATABASE `fuint-car`;
```

### 6.3 检查点
- [ ] SQL文件内容已替换
- [ ] 数据库已迁移（如需要）

---

## 阶段七：文档更新

### 7.1 更新CLAUDE.md

```bash
sed -i \
  -e 's/fuintBackend/carclubBackend/g' \
  -e 's/fuintAdmin/carclubAdmin/g' \
  -e 's/fuintUniapp/carclubUniapp/g' \
  -e 's/fuintCashier/carclubCashier/g' \
  -e 's/fuintCashierWeb/carclubCashierWeb/g' \
  -e 's/fuint-car/carclub-car/g' \
  -e 's/fuint123/carclub123/g' \
  -e 's/fuint汽车会员/carclub汽车会员/g' \
  -e 's/Fuint/Carclub/g' \
  -e 's/fuint/carclub/g' \
  CLAUDE.md
```

### 7.2 更新需求文档

```bash
find 需求文档 -name "*.md" -exec sed -i 's/fuint/carclub/g' {} \;
```

### 7.3 更新安装配置文档

```bash
sed -i \
  -e 's/fuintBackend/carclubBackend/g' \
  -e 's/fuintUniapp/carclubUniapp/g' \
  -e 's/fuintAdmin/carclubAdmin/g' \
  -e 's/fuintCashier/carclubCashier/g' \
  -e 's/fuintCashierWeb/carclubCashierWeb/g' \
  -e 's/fuint-application/carclub-application/g' \
  -e 's/fuint-framework/carclub-framework/g' \
  -e 's/fuint-repository/carclub-repository/g' \
  -e 's/fuint-utils/carclub-utils/g' \
  安装配置文档/carclub开发必读.txt
```

### 7.4 检查点
- [ ] CLAUDE.md已更新
- [ ] 需求文档已更新
- [ ] 安装配置文档已更新

---

## 阶段八：验证与测试

### 8.1 检查残留

```bash
# 检查是否还有残留的fuint（忽略本迁移文档）
grep -ri "fuint" \
  --include="*.java" \
  --include="*.xml" \
  --include="*.properties" \
  --include="*.json" \
  --include="*.js" \
  --include="*.vue" \
  --include="*.scss" \
  --exclude-dir=node_modules \
  --exclude-dir=target \
  --exclude-dir=dist \
  --exclude="fuint-to-carclub-migration.md" \
  .
```

### 8.2 后端编译测试

```bash
cd carclubBackend
mvn clean compile
```

### 8.3 后端打包测试

```bash
mvn clean package -pl carclub-application -am -DskipTests
```

### 8.4 前端依赖安装测试

```bash
# Admin
cd ../carclubAdmin
rm -rf node_modules package-lock.json
npm install

# CashierWeb
cd ../carclubCashierWeb
rm -rf node_modules package-lock.json
npm install

# Cashier (Electron)
cd ../carclubCashier
rm -rf node_modules yarn.lock
yarn install
```

### 8.5 前端启动测试

```bash
# Admin
cd carclubAdmin
NODE_OPTIONS=--openssl-legacy-provider npm run dev

# CashierWeb
cd carclubCashierWeb
npm run dev
```

### 8.6 Uniapp测试

使用HBuilderX导入carclubUniapp项目，检查是否能正常编译。

### 8.7 检查点
- [ ] 无残留fuint字符串
- [ ] 后端编译成功
- [ ] 后端打包成功
- [ ] Admin前端安装成功
- [ ] Admin前端启动成功
- [ ] CashierWeb安装成功
- [ ] CashierWeb启动成功
- [ ] Cashier Electron安装成功
- [ ] Uniapp编译成功

---

## 阶段九：提交变更

### 9.1 添加所有更改

```bash
git add -A
```

### 9.2 查看变更状态

```bash
git status
```

### 9.3 提交

```bash
git commit -m "$(cat <<'EOF'
refactor: rename fuint to carclub

Brand Renaming Migration:
- Renamed all directories from fuint* to carclub*
- Updated Java package from com.fuint to com.carclub
- Updated Maven groupId and artifactId
- Updated all configuration files
- Updated frontend package names and imports
- Updated Vue component names (Fuint* -> Carclub*)
- Updated SCSS variable ($fuint-theme -> $carclub-theme)
- Updated all URL references
- Updated documentation files
- Updated SQL initialization data

Breaking Changes:
- Database name changed from fuint-car to carclub-car
- Redis namespace changed from fuint to carclub
- All API endpoints remain unchanged

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
EOF
)"
```

### 9.4 检查点
- [ ] 所有更改已提交

---

## 手动处理项目

以下项目需要根据实际情况手动配置：

| 项目 | 说明 | 当前值 | 建议操作 |
|-----|------|-------|---------|
| **域名配置** | 替换所有 www.fuint.cn | `www.fuint.cn` | 替换为你的实际域名 |
| **OSS配置** | 阿里云OSS bucket | `fuint-application` | 新建bucket并更新配置 |
| **支付域名** | 微信/支付宝回调 | `www.fuint.cn/fuint-application` | 更新为你的支付回调域名 |
| **小程序AppID** | manifest.json | 原有appid | 替换为你自己的小程序appid |
| **Git仓库** | 代码仓库链接 | `gitee.com/fuint` | 替换为你的仓库地址 |
| **Redis密码** | 连接密码 | `carclub123` | 建议使用更安全的密码 |
| **登录账号** | 后台默认账号 | `admin` | 根据需要修改 |
| **数据库** | 生产环境迁移 | `fuint-car` | 按6.2步骤迁移 |

---

## 执行进度跟踪

| 阶段 | 状态 | 完成时间 | 备注 |
|-----|------|---------|------|
| 阶段一：准备工作 | ⬜ 待开始 | | |
| 阶段二：目录重命名 | ⬜ 待开始 | | |
| 阶段三：文件重命名 | ⬜ 待开始 | | |
| 阶段四：Java包名重构 | ⬜ 待开始 | | |
| 阶段五：文件内容替换 | ⬜ 待开始 | | |
| 阶段六：数据库处理 | ⬜ 待开始 | | |
| 阶段七：文档更新 | ⬜ 待开始 | | |
| 阶段八：验证测试 | ⬜ 待开始 | | |
| 阶段九：提交变更 | ⬜ 待开始 | | |

状态说明：⬜ 待开始 | 🔄 进行中 | ✅ 已完成 | ❌ 失败需修复

---

## 回滚方案

如果迁移过程中出现问题，可以回滚：

```bash
# 放弃所有更改，回到原始状态
git checkout main
git branch -D refactor/rename-to-carclub

# 如果已经stash了更改
git stash pop
```

---

## 附录：受影响文件统计

| 类型 | 预估数量 |
|-----|---------|
| 目录重命名 | 9 |
| 文件重命名 | 15+ |
| Java文件 | 400+ |
| XML配置文件 | 50+ |
| Properties文件 | 3 |
| JS/Vue文件 | 500+ |
| SCSS文件 | 80+ |
| JSON配置文件 | 10+ |
| 文档文件 | 5+ |
| **总计** | **约953个文件** |
