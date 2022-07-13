#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
psql -h localhost -p $PGPORT $USER"_DB" < $DIR/../sql/create_tables.sql
psql -h localhost -p $PGPORT $USER"_DB" < $DIR/../sql/create_indexes.sql
psql -h localhost -p $PGPORT $USER"_DB" < $DIR/../sql/load_data.sql