FROM nginx
MAINTAINER WangZhixing
LABEL app=ads-ui
ENV base_path /home/source/ads/deploy/image_build/ads-ui
COPY ${base_path}/run/ /usr/libexec/ads
RUN chmod -R 777 /usr/libexec/ads
COPY /home/source/dist /opt/app
USER 0
EXPOSE 3000
ENTRYPOINT []
CMD ["/usr/libexec/ads/run"]
ENV LANG C.UTF-8
