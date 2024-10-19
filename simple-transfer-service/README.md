# 简单传输

> SimpleTransfer

## Thymeleaf使用

1. 打包前端代码 `npm run build`
2. 前端dist文件夹中的index.html放入`src/main/resources/templates`文件夹
3. 前端dist文件夹中除index.html文件，其他文件或文件夹都放入`src/main/resources/static`文件夹

## 生成项目JRE

项目使用 JDK 版本：17
jlink.txt：存储执行 jlink 需要的参数

```shell
./jlink.exe @jlink.txt
```

## 打包EXE

使用 exe4j 软件可以将 jar 打包成 exe，SimpleTransfer.exe4j 文件是打包的配置文件