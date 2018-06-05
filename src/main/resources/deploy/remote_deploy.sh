#!/bin/bash

file_path=/home
config_path=/user/rgonc/apps/applib/
fileName=$1

tar -zxf ${file_path}/${fileName} -C ${file_path}/

/home/ads/install.sh

cp -r /home/ads/ADS_Config.sh ${config_path}

##安装完后删除压缩包
if [ -d /home/ads ];then
    rm -rf /home/ads
fi
rm -rf ${file_path}/${fileName}
rm -rf ${file_path}/*.sh
