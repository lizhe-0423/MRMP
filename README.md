# MRMP
SEEKCY新员工课题-微服务发布管理平台-基于SpringCloud+RockctMQ+ElasticSearch微服务项目

## 技术栈
- SpringBoot
- SpringCloud
- Nacos
- MybatisPlus
- ElasticSearch
- RoctetMQ
- Knife4j
- Swagger
- Redis
- Mysql
- Docker
- Gateway
- OSS
- Dubbo

## 组织架构图V1.0
![img.png](img.png)

## 项目结构
**api-user** 用户模块 用户管理 也是主模块（用户管理、系统管理） 通过用户模块去调用远程RPC服务

**api-gateway** 网关 前端->网关->转发给用户模块

**api-storage** 文档存储服务 提供RPC服务

**api-document** 文档管理服务 (数据库文档管理、接口文档管理、版本文档管理) 提供RPC服务

**api-publish** 发布服务 提供RPC服务