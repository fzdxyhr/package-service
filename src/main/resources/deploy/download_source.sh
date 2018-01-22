#!/bin/bash

SOURCE_PATH=/home/source

SVN_URI=$1
SVN_USERNAME=$2
SVN_PASSWORD=$3

MODULE_ADS=ads
MODULE_ADS_UI=ads-ui

ADS_SVN_PATH=${SOURCE_PATH}'/'${MODULE_ADS}
ADS_UI_SVN_PATH=${SOURCE_PATH}'/'${MODULE_ADS_UI}

if [[ ! -d ${SOURCE_PATH} ]];then
    echo "下载文件目录不存在，创建目录${SOURCE_PATH}"
    mkdir -p ${SOURCE_PATH}
fi

if [[ -d ${ADS_SVN_PATH} ]];then
    echo "开始更新svn ads代码，代码路径： ${ADS_SVN_PATH}"
    cd ${ADS_SVN_PATH}
    svn up
    echo "更新svn ads代码完成，代码路径： ${ADS_SVN_PATH}"
else
    echo "开始下载svn ads代码，代码路径： ${SVN_URI}"
    svn co ${SVN_URI}'/'${MODULE_ADS} --username ${SVN_USERNAME} --password ${SVN_PASSWORD} --no-auth-cache ${SOURCE_PATH}
    echo "svn ads代码下载完成，存放路径： ${ADS_SVN_PATH}"
fi

if [[ -d ${ADS_UI_SVN_PATH} ]];then
    echo "开始更新svn ads代码，代码路径： ${ADS_UI_SVN_PATH}"
    cd ${ADS_UI_SVN_PATH}'/'
    svn up
    echo "更新svn ads代码完成，代码路径： ${ADS_UI_SVN_PATH}"
else
    echo "开始下载svn ads-ui代码，代码路径： ${SVN_URI}"
    svn co ${SVN_URI}'/'${MODULE_ADS_UI} --username ${SVN_USERNAME} --password ${SVN_PASSWORD} --no-auth-cache ${SOURCE_PATH}
    echo "svn ads-ui代码下载完成，存放路径： ${ADS_UI_SVN_PATH}"
fi



