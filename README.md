# Spring Boot 后端项目
启动 Redis 和建表说明

1. 启动 Redis

在 Windows 上启动 Redis，打开命令提示符（CMD）或直接双击：

E:\A_claude\redis\redis-server.exe

或者在 CMD 中运行：
cd E:\A_claude\redis
redis-server.exe

启动成功后会看到 Redis 的启动日志，显示 Ready to accept connections。

注意：保持这个窗口打开，关闭窗口 Redis 就会停止。

  ---
2. 数据库建表

是的，需要建表！ Redis 不需要建表（它是键值存储），但 MySQL 需要执行建表语句。

请在 MySQL 中执行以下步骤：

1. 创建数据库（如果还没有）：
   CREATE DATABASE figture_db DEFAULT CHARACTER SET utf8mb4;

2. 执行 schema.sql：
   在 C:\Users\ASUS\Desktop\backend\src\main\resources\schema.sql 中有完整的建表语句。

用 MySQL 客户端（如 Navicat、MySQL Workbench 或命令行）连接数据库后，执行该文件中的 SQL：

USE figture_db;

-- 然后执行 schema.sql 的全部内容
-- 包括6张表：sys_user, theme, user_settings, pomodoro_session, work_log, daily_stats

  ---
启动顺序

1. 先启动 MySQL（确保运行中）
2. 执行 schema.sql 建表
3. 启动 Redis（双击 redis-server.exe）
4. 启动 Spring Boot 后端（运行 BackendApplication）
5. 启动 前端（HBuilderX 运行项目）
## 项目说明
这是一个基于 Spring Boot 3.2 + MyBatis Plus 的标准后端项目框架，严格遵循阿里巴巴Java开发规范。

## 技术栈
- **JDK**: 17
- **Spring Boot**: 3.2.1
- **MyBatis Plus**: 3.5.5
- **MySQL**: 8.0
- **Redis**: 最新版
- **Knife4j**: 4.4.0（API文档）
- **JWT**: 0.12.3（认证）
- **Hutool**: 5.8.24（工具类）
- **Lombok**: 最新版（简化代码）

## 项目结构
```
backend/
├── src/main/java/com/company/project/
│   ├── BackendApplication.java          # 启动类
│   ├── common/                           # 通用类
│   │   ├── Result.java                   # 统一返回结果（code、msg、data）
│   │   └── ResultCode.java               # 响应码枚举
│   ├── config/                           # 配置类
│   │   ├── CorsConfig.java               # 跨域配置
│   │   ├── MyBatisPlusConfig.java        # MyBatis Plus配置
│   │   └── MetaObjectHandlerConfig.java  # 字段自动填充配置
│   ├── controller/                       # 控制层（接口规范整齐）
│   │   └── UserController.java           # 用户接口示例
│   ├── service/                          # 服务接口层
│   │   ├── UserService.java              # 用户服务接口
│   │   └── impl/                         # 服务实现层（复杂条件在这里写）
│   │       └── UserServiceImpl.java      # 用户服务实现
│   ├── mapper/                           # 数据访问层
│   │   └── UserMapper.java               # 用户Mapper
│   ├── entity/                           # 实体类
│   │   ├── BaseEntity.java               # 基础实体（雪花ID、公共字段）
│   │   └── User.java                     # 用户实体示例
│   └── exception/                        # 异常处理
│       ├── BusinessException.java        # 业务异常
│       └── GlobalExceptionHandler.java   # 全局异常处理器
├── src/main/resources/
│   ├── mapper/                           # MyBatis XML文件
│   │   └── UserMapper.xml                # 用户Mapper XML
│   ├── sql/                              # 数据库脚本
│   │   └── schema.sql                    # 建表脚本
│   └── application.yml                   # 配置文件
├── pom.xml                               # Maven依赖
└── README.md                             # 项目说明
```

## 快速开始

### 1. 配置数据库
打开 `src/main/resources/application.yml`，修改数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: root
    password: 
```

### 2. 初始化数据库
在 MySQL Workbench 中执行 `src/main/resources/sql/schema.sql` 脚本，创建数据库和表。

### 3. 启动项目
在 IDEA 中运行 `BackendApplication.java`

### 4. 访问API文档
启动成功后，访问：
- Knife4j文档：http://localhost:8080/api/doc.html
- 接口地址：http://localhost:8080/api/user/list

## 开发规范

### 1. 命名规范
- **类名**：大驼峰（UserController、UserService）
- **方法名**：小驼峰（getUserById、saveUser）
- **变量名**：小驼峰（userId、userName）
- **常量名**：全大写下划线分隔（MAX_COUNT、DEFAULT_SIZE）

### 2. 分层规范
- **Controller层**：只做参数校验和调用Service，不写业务逻辑
- **Service层**：编写业务逻辑，接口保持简洁
- **ServiceImpl层**：实现Service接口，**复杂条件查询只在这里写**
- **Mapper层**：数据访问，继承BaseMapper

### 3. 统一返回
所有接口统一返回 `Result<T>` 对象，包含：
- `code`: 响应码
- `msg`: 响应消息
- `data`: 响应数据

示例：
```java
return Result.success(data);           // 成功
return Result.fail("错误信息");         // 失败
```

### 4. 主键策略
所有实体类继承 `BaseEntity`，自动获得：
- `id`: 雪花ID（`@TableId(type = IdType.ASSIGN_ID)`）
- `createTime`: 创建时间（自动填充）
- `updateTime`: 更新时间（自动填充）
- `createBy`: 创建人（自动填充）
- `updateBy`: 更新人（自动填充）
- `deleted`: 逻辑删除标识（自动填充）

### 5. 注释规范
- 所有类、方法必须添加注释
- 注释要说明功能、参数、返回值
- 保持代码整洁，遵循阿里巴巴规范

## 待完成功能
- [ ] JWT认证拦截器
- [ ] Redis缓存配置
- [ ] 文件上传下载
- [ ] 日志配置
- [ ] 接口限流
- [ ] 数据权限

## 注意事项
1. 数据库密码请修改为实际密码
2. Redis如不使用可暂时注释依赖
3. 创建新实体类时继承 `BaseEntity`
4. 复杂查询条件只在 `ServiceImpl` 层编写
5. 接口保持规范整齐，统一返回 `Result`

---
**作者**: 陈宣而
**日期**: 2026-01-31

  ---
🔑 核心功能实现
┌──────────────┬───────────────────────────────────┐
│     功能     │             实现方式              │
├──────────────┼───────────────────────────────────┤
│ 密码加密     │ Hutool BCrypt                     │
├──────────────┼───────────────────────────────────┤
│ JWT认证      │ JJWT 0.12.3 + 拦截器              │
├──────────────┼───────────────────────────────────┤
│ 限制一局     │ Redis锁 + current_session_id 字段 │
├──────────────┼───────────────────────────────────┤
│ 欢迎语       │ 查询昨日 daily_stats + 计算排名   │
├──────────────┼───────────────────────────────────┤
│ 工作过久提醒 │ 对比今日时长与用户设置阈值        │
└──────────────┴───────────────────────────────────┘
  ---
📁 新增文件清单

src/main/java/com/company/project/
├── controller/
│   ├── AuthController.java          # 新增
│   ├── PomodoroController.java      # 新增
│   ├── SettingsController.java      # 新增
│   └── StatsController.java         # 新增
├── dto/
│   ├── LoginRequest.java            # 新增
│   ├── LoginResponse.java           # 新增
│   ├── RegisterRequest.java         # 新增
│   ├── SettingsUpdateRequest.java   # 新增
│   ├── StatsDTO.java                # 新增
│   └── WelcomeDTO.java              # 新增
├── entity/
│   ├── User.java                    # 修改（加字段）
│   ├── Theme.java                   # 新增
│   ├── UserSettings.java            # 新增
│   ├── PomodoroSession.java         # 新增
│   ├── WorkLog.java                 # 新增
│   └── DailyStats.java              # 新增
├── mapper/
│   ├── ThemeMapper.java             # 新增
│   ├── UserSettingsMapper.java      # 新增
│   ├── PomodoroSessionMapper.java   # 新增
│   ├── WorkLogMapper.java           # 新增
│   └── DailyStatsMapper.java        # 新增
├── service/
│   ├── AuthService.java             # 新增
│   ├── PomodoroService.java         # 新增
│   ├── UserSettingsService.java     # 新增
│   ├── StatsService.java            # 新增
│   └── impl/                        # 全部新增
├── interceptor/
│   └── JwtInterceptor.java          # 新增
├── config/
│   └── WebConfig.java               # 新增
└── util/
├── JwtUtil.java                 # 新增
└── TimeFormatUtil.java          # 新增

src/main/resources/
├── sql/schema.sql                   # 重写（完整6张表）
└── application.yml                  # 添加JWT配置

  ---
🚀 启动步骤

1. 初始化数据库：执行 src/main/resources/sql/schema.sql
2. 启动 Redis：确保本地 Redis 运行在 6379 端口
3. 启动项目：mvn spring-boot:run
4. 访问API文档：http://localhost:8080/api/doc.html

测试账号：admin / 123456
