#!/bin/bash
DATABASE_NAME="euei_fifa"
DB_DUMP_LOCATION="/*.eueififa.backup"
USER="urlljusqdsjtib"

echo "*** CREATING DATABASE ***"
# import sql_dump
/etc/init.d/postgresql start
export PGUSER=postgres
psql <<- EOSQL
    CREATE USER $USER;
    GRANT ALL PRIVILEGES ON DATABASE $DATABASE_NAME TO $USER;
EOSQL
pg_restore -U postgres -d $DATABASE_NAME < $DB_DUMP_LOCATION
echo "*** DATABASE CREATED! ***"