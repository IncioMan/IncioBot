FROM tomcat:8.0.41-jre8
MAINTAINER incio

COPY ./target/*.war /usr/local/tomcat/webapps/
COPY euei_gui_startup.sh /

ENTRYPOINT ["/bin/sh","/euei_gui_startup.sh"]

RUN mkdir /jrebel
ADD jrebel.jar /jrebel/

ENV JPDA_ADDRESS="8000"
ENV JPDA_TRANSPORT="dt_socket"
ENV CATALINA_OPTS="-javaagent:/jrebel/jrebel.jar -Drebel.remoting_plugin=true"

EXPOSE 8080 8000
CMD ["catalina.sh", "jpda", "run"]