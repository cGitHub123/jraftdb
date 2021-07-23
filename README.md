# jraftdb

start server1:
/tmp/jraftdb/server1 counter 127.0.0.1:8081 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 /data1/repositories

start server2:
/tmp/jraftdb/server2 counter 127.0.0.1:8082 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 /data2/repositories 

start server3:
/tmp/jraftdb/server3 counter 127.0.0.1:8083 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 /data3/repositories

start client:
counter 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083