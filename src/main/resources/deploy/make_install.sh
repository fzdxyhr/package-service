#!/bin/bash

file_path=/home/file/install
ADS_SOURCE_PATH=/home/source/ads
PACKAGE_PATH=/home/source/deploy/install/ads

## svn 基础地址
svn_url=$1
## 版本号
version=$2
## 构建号
build=$3

current_path="$(cd `dirname $0`;pwd)"
date=`date +%Y%m%d`
outFileName="RG_ONC_APPS_ADS_"${version}"_Build"${build}".tar.gz"
fileName="rgonc-ads.tar.gz"
SVN_URI=${svn_url}
SVN_USERNAME="panzibin"
SVN_PASSWORD="pzb.328942829"

if [[ ! -d ${PACKAGE_PATH} ]];then
    echo "打包目录不存在，创建目录${PACKAGE_PATH}"
    mkdir -p ${PACKAGE_PATH}
fi

echo "开始下载应用源代码"
${current_path}/download_source.sh ${SVN_URI} ${SVN_USERNAME} ${SVN_PASSWORD}
echo "下载应用源代码完成"

echo "开始构建 ads和ads-ui镜像"
${ADS_SOURCE_PATH}/deploy/image_build/ads/install.sh
${ADS_SOURCE_PATH}/deploy/image_build/ads-ui/install.sh
echo "构建 ads和ads-ui镜像完成"

echo "拷贝文件到打包目录"
cp -r ${ADS_SOURCE_PATH}/deploy/install_package ${ADS_SOURCE_PATH}/deploy/ADS_Config.sh  ${PACKAGE_PATH}/
cp -r ${ADS_SOURCE_PATH}/src/main/resources/script/* ${PACKAGE_PATH}/install_package/data/script/
cp -r ${ADS_SOURCE_PATH}/src/main/resources/database/* ${PACKAGE_PATH}/install_package/database/
mv  ${PACKAGE_PATH}/install_package  ${PACKAGE_PATH}/ads
docker save ads > ${PACKAGE_PATH}/ads/image/ads.tar
docker save ads-ui > ${PACKAGE_PATH}/ads/image/ads-ui.tar
docker save mariadb > ${PACKAGE_PATH}/ads/image/mariadb.tar
echo "脚本赋予权限"
chmod 777 ${PACKAGE_PATH}/ads/*.sh
chmod 777 ${PACKAGE_PATH}/*.sh
chmod 777 ${PACKAGE_PATH}/ads/data/script/dhcp/*.sh

cd ${PACKAGE_PATH}
## 保存build号和版本号用于升级使用
sed -i "s/^build=.*$/build=${build}/g" ./ads/build
sed -i "s/^version=.*$/version=${version}/g" ./ads/build

tar -zcvf ${fileName} ads
##获取文件的MD5,并写入到MD5
md5_value=`md5sum ${fileName}|cut -d ' ' -f1`
echo ${md5_value} > ./ads/.md5

##移动目录到外层打包目录
mv ./ads/install.sh ./
mv ./ads/.md5 ./
mv ./ads/version.txt ./

##打包外层目录
rm -rf ads

cd ../
tar -zcvf ${outFileName} ads
mv ${outFileName} ${file_path}
rm -rf ads
docker rmi ads ads-ui
