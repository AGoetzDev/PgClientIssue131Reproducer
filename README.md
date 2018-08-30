# PgClientIssue131Reproducer

Reproducer project for https://github.com/reactiverse/reactive-pg-client/issues/131

Tries to insert entries into the database with wrong type to trigger class cast exception in order to reproduce the issue.
The first two inserts are done by obtaining a connection but not closing it. In this case the exception handler of the connection object is triggered as expected each time and the ClassCastException is thrown. 
Unexpected in this case is that the close handler of the connection object is called, even though connection.close() is not called.

The next three inserts are done by obtaining a connection object and calling connection.close() in the handler to return the connection to the connection pool as described in https://reactiverse.io/reactive-pg-client/guide/java/ under "Using connections".
The first insert in this case triggers the exception handler as expected with a ClassCastException, subsequent inserts however fail with "Connection not open CLOSED" without triggering the exception handler.

# Setup

1. Create a PostgreSQL database "reproducerdb" then execute reproducerdb.sql to create the database table schema.
2. Fill in the database connection details in DataBaseConfig.json
3. Build the project
4. Run it with java -jar PgClientIssue131Reproducer-1.0.jar DataBaseConfig.json

# Output

The console output should look something like this:

Aug 30, 2018 12:29:22 AM PostgreSQLClientWrapper
INFORMATION: QUERY WITHOUT CLOSING: 1
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
INFORMATION: QueryWithoutClosing 1: Got Connection
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
SCHWERWIEGEND: QueryWithoutClosing 1 Exception: java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
Aug 30, 2018 12:29:23 AM ReproducerVerticle
SCHWERWIEGEND: First insert without closing failed: java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
INFORMATION: QUERY WITHOUT CLOSING: 2
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
INFORMATION: QueryWithoutClosing 1: Closing
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
INFORMATION: QueryWithoutClosing 2: Got Connection
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
SCHWERWIEGEND: QueryWithoutClosing 2 Exception: java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
Aug 30, 2018 12:29:23 AM ReproducerVerticle
SCHWERWIEGEND: Second insert without closing failed: java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
INFORMATION: QUERY WITH CONNECTION: 1
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
INFORMATION: QueryWithoutClosing 2: Closing
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
INFORMATION: QueryWithConnection 1: Got Connection
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
SCHWERWIEGEND: QueryWithConnection 1 Exception: java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
Aug 30, 2018 12:29:23 AM ReproducerVerticle
SCHWERWIEGEND: First insert with connection failed: java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
INFORMATION: QUERY WITH CONNECTION: 2
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
INFORMATION: QueryWithConnection 2: Got Connection
Aug 30, 2018 12:29:23 AM ReproducerVerticle
SCHWERWIEGEND: Second insert with connection failed: io.vertx.core.VertxException: Connection not open CLOSED
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
INFORMATION: QUERY WITH CONNECTION: 3
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
INFORMATION: QueryWithConnection 3: Got Connection
Aug 30, 2018 12:29:23 AM ReproducerVerticle
SCHWERWIEGEND: Third insert with connection failed: io.vertx.core.VertxException: Connection not open CLOSED
Aug 30, 2018 12:29:23 AM PostgreSQLClientWrapper
INFORMATION: QueryWithConnection 1: Closing
Aug 30, 2018 12:29:23 AM Main
INFORMATION: Startup completed