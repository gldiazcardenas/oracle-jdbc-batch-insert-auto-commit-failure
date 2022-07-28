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

![alt Result](https://github.com/gldiazcardenas/oracle-jdbc-batch-insert-auto-commit-failure/blob/main/scenario_1_table_result.png?raw=true)


## Scenario 2: Using Spring JdbcTemplate (autoCommit = true) ##

- Same conditions than scenario 1

### Conclusion ###

- Same situation than scenario 1
- 17 rows in total get inserted.
- The JdbcTemplate uses a connection inside that is different from the one is obtained at the beginning of the block.


