FROM centos:6.9
MAINTAINER WangZhixing
LABEL app=ads
WORKDIR /opt
ENV base_path /home/source/ads/deploy/image_build/ads
ADD ${base_path}/server-jre-8u144-linux-x64.tar.gz /home
COPY ${base_path}/run/ /usr/libexec/ads
RUN mkdir -p /opt/app/config && mkdir -p /opt/app/data && yum install dhcp -y && chmod -R 777 /usr/sbin && chmod -R 777 /usr/libexec/ads && echo -e "export JAVA_HOME=/home/jdk1.8.0_144" >> /etc/profile && echo -e "export JRE_HOME=/home/jdk1.8.0_144/jre" >> /etc/profile && echo -e 'export export PATH=${JAVA_HOME}/bin:${PATH}' >> /etc/profile
COPY ${base_path}/run/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone
COPY /home/source/ads.jar /opt/app
USER 0
EXPOSE 67
EXPOSE 68
EXPOSE 69
EXPOSE 8096
ENTRYPOINT []
CMD ["/usr/libexec/ads/run"]
ENV LANG C.UTF-8
