# oracle-jdbc-batch-insert-auto-commit-failure

Batch insert using Oracle JDBC Driver with autoCommit=true making one statement fail on purpose in order to determine 
the behavior of the driver in such circumstance.

Used Oracle XE Docker image (18.4.0) from: https://github.com/oracle/docker-images/tree/main/OracleDatabase/SingleInstance/dockerfiles

Running

``
./buildContainerImage.sh -x -v 18.4.0
``

## Scenario 1: Using Spring JdbcTemplate (autoCommit = true) ##

- Inserting 20 rows into a table:
  ```
    create table batch_insert
    (
        id int not null,
        text varchar2(50),
        primary key (id)
    );
  ```
- Insert in chunks of 5 items.
- Make the 8th item having TEXT longer than 50 chars.
- Check what gets inserted in DB.


![alt Sceneario 1](https://github.com/gldiazcardenas/oracle-jdbc-batch-insert-auto-commit-failure/blob/main/scenario_1.png?raw=true)

### Conclusion ###

- Inserting chunks of 5 elements at a time, batchSize=5, 4 iterations in total
- Making the 8th element fail having text longer than 50 characters
- The 2nd iteration gets aborted as soon as the 8th element is processed.
- 17 rows in total get inserted, the 6th and 7th element get inserted in the table.

```
2022-07-28 13:08:34.428  INFO 23020 --- [           main] c.g.o.Application                        : Starting Application using Java 17.0.3 on gdiaz-win with PID 23020 (C:\Users\gdiaz\oracle-jdbc-batch-insert-auto-commit-failure\build\classes\java\main started by gdiaz in C:\Users\gdiaz\oracle-jdbc-batch-insert-auto-commit-failure)
2022-07-28 13:08:34.431  INFO 23020 --- [           main] c.g.o.Application                        : No active profile set, falling back to 1 default profile: "default"
2022-07-28 13:08:35.939  INFO 23020 --- [           main] c.g.o.Application                        : Started Application in 2.091 seconds (JVM running for 2.724)
Running Scenario 1
2022-07-28 13:08:35.964  INFO 23020 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2022-07-28 13:08:36.553  INFO 23020 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
Inserted correctly: 5
Failed: PreparedStatementCallback; uncategorized SQLException for SQL [INSERT INTO BATCH_INSERT (ID,TEXT) VALUES (?,?)]; SQL state [72000]; error code [12899]; ORA-12899: value too large for column "SYS"."BATCH_INSERT"."TEXT" (actual: 52, maximum: 50)
; nested exception is java.sql.BatchUpdateException: ORA-12899: value too large for column "SYS"."BATCH_INSERT"."TEXT" (actual: 52, maximum: 50)
, results: null
Inserted correctly: 5
Inserted correctly: 5
Total rows inserted: 17
```

![alt Result](https://github.com/gldiazcardenas/oracle-jdbc-batch-insert-auto-commit-failure/blob/main/scenario_1_table_result.png?raw=true)


## Scenario 2: Using Spring JdbcTemplate (autoCommit = false) ##

- Same conditions than scenario 1

### Conclusion ###

- Same situation than scenario 1
- 17 rows in total get inserted.
- The JdbcTemplate uses a connection inside that is different from the one is obtained at the beginning of the block.
```
2022-07-28 13:09:30.032  INFO 49804 --- [           main] c.g.o.Application                        : Starting Application using Java 17.0.3 on gdiaz-win with PID 49804 (C:\Users\gdiaz\oracle-jdbc-batch-insert-auto-commit-failure\build\classes\java\main started by gdiaz in C:\Users\gdiaz\oracle-jdbc-batch-insert-auto-commit-failure)
2022-07-28 13:09:30.035  INFO 49804 --- [           main] c.g.o.Application                        : No active profile set, falling back to 1 default profile: "default"
2022-07-28 13:09:31.451  INFO 49804 --- [           main] c.g.o.Application                        : Started Application in 1.893 seconds (JVM running for 2.311)
Running Scenario 2
2022-07-28 13:09:31.456  INFO 49804 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2022-07-28 13:09:32.100  INFO 49804 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
Inserted correctly: 5
Failed: PreparedStatementCallback; uncategorized SQLException for SQL [INSERT INTO BATCH_INSERT (ID,TEXT) VALUES (?,?)]; SQL state [72000]; error code [12899]; ORA-12899: value too large for column "SYS"."BATCH_INSERT"."TEXT" (actual: 52, maximum: 50)
; nested exception is java.sql.BatchUpdateException: ORA-12899: value too large for column "SYS"."BATCH_INSERT"."TEXT" (actual: 52, maximum: 50)
, results: null
Inserted correctly: 5
Inserted correctly: 5
Disconnected from the target VM, address: 'localhost:58445', transport: 'socket'
Connected to the target VM, address: '127.0.0.1:58429', transport: 'socket'
Total rows inserted: 17
```

## Scenario 3: Using pure JDBC (autoCommit = false) ##

- Using Connection + PreparedStatement from javax.sql.
- Inserting 20 rows in batches of 5 elements.
- Making the 8th element fail due to text longer than 50 characters.

### Conclusion ###

- 15 rows total inserted.
- The 2nd chunk gets totally reverted, generating the expected ACID result.

```
2022-07-28 13:12:53.536  INFO 52124 --- [           main] c.g.o.Application                        : Starting Application using Java 17.0.3 on gdiaz-win with PID 52124 (C:\Users\gdiaz\oracle-jdbc-batch-insert-auto-commit-failure\build\classes\java\main started by gdiaz in C:\Users\gdiaz\oracle-jdbc-batch-insert-auto-commit-failure)
2022-07-28 13:12:53.541  INFO 52124 --- [           main] c.g.o.Application                        : No active profile set, falling back to 1 default profile: "default"
2022-07-28 13:12:55.137  INFO 52124 --- [           main] c.g.o.Application                        : Started Application in 2.15 seconds (JVM running for 2.637)
Running Scenario 3
2022-07-28 13:12:59.342  INFO 52124 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2022-07-28 13:13:09.273  INFO 52124 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
Inserted correctly: 5
Failed: ORA-12899: value too large for column "SYS"."BATCH_INSERT"."TEXT" (actual: 52, maximum: 50)
, results: null
Inserted correctly: 5
Inserted correctly: 5
Total rows inserted: 15
```

![alt Result](https://github.com/gldiazcardenas/oracle-jdbc-batch-insert-auto-commit-failure/blob/main/scenario_3_table_result.png?raw=true)

