#!/bin/bash

file_name=`cat file_name.txt`
upgrade_file_name=`cat upgrade_file_name.txt`
ipAddress=$1
remotePath=/home/
sshPassword=rJ1#sDn
isDeploy=$2

if [[ ! -z ${file_name} ]];then
    ## 执行本地脚本并调用
    sshpass -p 123456 scp ./${file_name} root@172.31.140.153:/home/file/install
    sshpass -p ${sshPassword} scp ./${file_name} root@${ipAddress}:${remotePath}
    sshpass -p ${sshPassword} scp ./remote_deploy.sh root@${ipAddress}:${remotePath}
    rm -rf ./${file_name}
    echo "" > file_name.txt
    sshpass -p ${sshPassword} ssh root@${ipAddress} ${remotePath}remote_deploy.sh ${file_name}
fi