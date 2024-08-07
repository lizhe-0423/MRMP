#
FROM 172.16.4.252:20000/tiji/openjdk8-alpine:latest

RUN mkdir -p /tiji/dbexport
# 工作空间自行修改为项目对应空间
WORKDIR /tiji/dbexport
# 这里替换为项目编译 jar 路径
ADD ./target/database-export-jar-with-dependencies.jar ./dbexport.jar
# 暴露的端口号，对应应用端口号
EXPOSE 18111
# tini 解决 pid 1 不能使用 arthas
ENTRYPOINT ["tini"]
CMD ["/bin/sh", "-c", "java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -Djava.security.egd=file:/dev/./urandom -jar dbexport.jar"]