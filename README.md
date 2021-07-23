# jraftdb

start server1:
/tmp/jraftdb/server1 counter 127.0.0.1:8081 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 /src/main/resources/data1/repositories db1

start server2:
/tmp/jraftdb/server2 counter 127.0.0.1:8082 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 /src/main/resources/data2/repositories db2

start server3:
/tmp/jraftdb/server3 counter 127.0.0.1:8083 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 /src/main/resources/data3/repositories db3

start client:
counter 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083