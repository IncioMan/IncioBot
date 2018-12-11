#!/bin/sh

if [ -z ${DATABASE_PORT+x} ]
then 
	echo "Link alias 'database' was not set!"
	exit
else
	export DATABASE_URL=postgres://$DATABASE_ENV_POSTGRES_USER:$DATABASE_ENV_POSTGRES_PASSWORD@database:$DATABASE_PORT_5432_TCP_PORT/$DATABASE_ENV_POSTGRES_DB
	exec "$@"
fi


