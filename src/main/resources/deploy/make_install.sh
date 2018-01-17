#!/bin/bash

file_path=/home/install

## 版本号
svn_url=$1
version=$2
build=$3

cd image_build/ads
./install.sh
cd ../../
cd image_build/ads-ui
./install.sh
cd ../../
date=`date +%Y%m%d`
outFileName="RG_ONC_APPS_ADS_"${version}"_Build"${build}".tar.gz"
fileName="rgonc-ads.tar.gz"
SVN_URI=${svn_url}
SVN_USERNAME="panzibin"
SVN_PASSWORD="pzb.328942829"
svn co ${SVN_URI} --username ${SVN_USERNAME} --password ${SVN_PASSWORD} --no-auth-cache
cp -r ads/src/main/resources/script/* ads/deploy/install_package/data/script/
cp -r ads/src/main/resources/database/* ads/deploy/install_package/database/
mv  ads/deploy/install_package  ads/deploy/ads
docker save ads > ads/deploy/ads/image/ads.tar
docker save ads-ui > ads/deploy/ads/image/ads-ui.tar
docker save mariadb > ads/deploy/ads/image/mariadb.tar
## 由于系统中没有对应的percona ，所以直接在项目中放了percona.tar，直接copy 到对应的目录中
#cp ads/deploy/ads/packge/percona.tar ads/deploy/ads/image/

chmod 777 ads/deploy/ads/*.sh
chmod 777 ads/deploy/*.sh
chmod 777 ads/deploy/ads/data/script/dhcp/*.sh

cd ads/deploy

## 保存build号和版本号用于升级使用

sed -i "s/^build=.*$/build=${build}/g" ./ads/build
sed -i "s/^version=.*$/version=${version}/g" ./ads/build
#echo ${date} > ./ads/build

tar -zcvf ${fileName} ads
##获取文件的MD5,并写入到MD5
md5_value=`md5sum ${fileName}|cut -d ' ' -f1`
echo ${md5_value} > ./ads/.md5
mv ${fileName} ../

##移动目录到外层打包目录
mv ./ads/install.sh ../
mv ./ads/.md5 ../
mv ./ads/version.txt ../
mv ./ADS_Config.sh ../

##打包外层目录
cd ../
rm -rf deploy
rm -rf src
rm -rf ads.iml
rm -rf pom.xml
rm -rf .svn

cd ../
tar -zcvf ${outFileName} ads
mv ${outFileName} ${file_path}
rm -rf ads
docker rmi ads ads-ui
