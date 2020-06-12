FROM mysql:5.7



ENV MYSQL_DATABASE archives
ENV MYSQL_ROOT_PASSWORD password
ENV MYSQL_USER mysql
ENV MYSQL_PASSWORD password
ENV DATABASE_HOST db



COPY createInsert.sql /docker-entrypoint-initdb.d/
