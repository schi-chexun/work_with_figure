# Spring Boot 后端项目

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
    password: your_password
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
**作者**: Your Name
**日期**: 2026-01-31
