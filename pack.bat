@echo off

@REM set jdk17 path
set jdkPath=C:\Users\hanjie\.version-fox\cache\java\v-17.0.8\java-17.0.8
echo set JAVA_HOME %jdkPath%
set JAVA_HOME=%jdkPath%

echo build simple-transfer-ui
cd simple-transfer-ui
call npm run build

cd ..
echo delete service template files
del simple-transfer-service\src\main\resources\templates\index.html
del /Q simple-transfer-service\src\main\resources\static\*
rmdir /S /Q simple-transfer-service\src\main\resources\static\static

echo move simple-transfer-ui dist files to simple-transfer-service template
move simple-transfer-ui\dist\index.html simple-transfer-service\src\main\resources\templates
move simple-transfer-ui\dist\favicon.ico simple-transfer-service\src\main\resources\static
move simple-transfer-ui\dist\static simple-transfer-service\src\main\resources\static

rmdir simple-transfer-ui\dist

echo build simple-transfer-service
cd simple-transfer-service
call mvn -DskipTests=true clean package

pause