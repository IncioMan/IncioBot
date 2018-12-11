FROM postgres:9.3
MAINTAINER incio
COPY *.eueififa.backup /
COPY postgres_startup.sh /docker-entrypoint-initdb.d/
ENV POSTGRES_USER=postgres \ 
	POSTGRES_DB=euei_fifa \ 
	POSTGRES_PASSWORD=postgresql
