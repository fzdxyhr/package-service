#!/bin/bash

ADS_UI_SOURCE_PATH=/home/source/ads-ui
echo "开始编译ads-ui镜像"
cd ${ADS_UI_SOURCE_PATH}
npm install
npm run build
cp -r dist ../
cd ../
docker build -t ads-ui -f ${ADS_UI_SOURCE_PATH}/deploy/image_build/ads-ui/Dockerfile ./
rm -rf dist/
