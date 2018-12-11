FROM tomcat:8.0.41-jre8
MAINTAINER incio
COPY ./target/*.war /usr/local/tomcat/webapps/
COPY context.xml /usr/local/tomcat/conf/
COPY be_startup.sh /

ENTRYPOINT ["/bin/sh","/be_startup.sh"]

RUN mkdir /jrebel
ADD jrebel.jar /jrebel/

ENV DATABASE_URL ciao
ENV JPDA_ADDRESS="8000"
ENV JPDA_TRANSPORT="dt_socket"
ENV CATALINA_OPTS="-javaagent:/jrebel/jrebel.jar -Drebel.remoting_plugin=true"

EXPOSE 8080 8000
CMD ["catalina.sh", "jpda", "run"]