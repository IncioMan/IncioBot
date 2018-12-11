#!/bin/sh
if [ -z ${BACKEND_PORT+x} ]
then 
	echo "Link alias 'backend' was not set!"
	exit
else
	export INCIOBOT_BACKEND_ADDRESS=http://backend:$BACKEND_PORT_8080_TCP_PORT/inciobot.bot_backend/
	echo "QUIIIIIIIIIIIII ---->" $INCIOBOT_BACKEND_ADDRESS
	exec "$@"
fi
