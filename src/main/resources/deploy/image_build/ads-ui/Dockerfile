FROM nginx
MAINTAINER WangZhixing
LABEL app=ads-ui
COPY ./run/ /usr/libexec/ads
RUN chmod -R 777 /usr/libexec/ads
COPY ./dist /opt/app
USER 0
EXPOSE 3000
ENTRYPOINT []
CMD ["/usr/libexec/ads/run"]
ENV LANG C.UTF-8
