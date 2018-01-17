#!/bin/bash
echo "开始编译ads-ui镜像"
SVN_URI="http://svn.ruijie.net/svn/soft-code/CP/CustomMade/ONC-ADS/branches/ADS_1.10/ads-ui"
SVN_USERNAME="panzibin"
SVN_PASSWORD="pzb.328942829"
echo "开始下载svn代码，代码路径： ${SVN_URI}"
export SRC_DIR=`basename $SVN_URI`
mkdir src
cd src
svn co ${SVN_URI} --username ${SVN_USERNAME} --password ${SVN_PASSWORD} --no-auth-cache
echo "完成svn代码下载"
echo "开始编译代码${SRC_DIR}"
cd ${SRC_DIR}
npm install
npm run build
cp -r dist ../../
cd ../../
docker build -t ads-ui .
#docker tag ads-ui 172.30.167.66:5000/openshift/ads-ui
#docker push 172.30.167.66:5000/openshift/ads-ui
rm -rf dist/
rm -rf src/
