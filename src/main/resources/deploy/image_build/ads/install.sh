#!/bin/bash
echo "开始编译ads镜像"
ADS_SOURCE_PATH=/home/source/ads

echo "进入到ads目录，目录：${ADS_SOURCE_PATH}"
cd ${ADS_SOURCE_PATH}
echo "maven编译、打包ads代码"
mvn -Dmaven.test.skip=true package
find ./ -name '*.jar'|xargs -i cp -v {} ..
mvn clean
cd ..
docker build -t ads -f ${ADS_SOURCE_PATH}/deploy/image_build/ads/Dockerfile ./
rm -rf ads.jar


