# Video library database

## Why this learning project?

- Practice in Java 17.
- Work with the database through JDBC and connection pooling.
- Design a database and write queries in SQL.
- Practice writing tests.

## Task Statement

In the task it is necessary to perform the following actions on Java using JDBC:

- organize the database connection into a separate class whose method returns the connection;
- create a database, bring tables to one of the normal forms;
- create a class for executing queries to retrieve information from the database using compiled queries.

### Database Requirements

The database stores information about the home video library: movies, actors, directors.

For movies, it is necessary to store:

- title;
- actors names;
- release date;
- the country where the film was released.

For actors and directors you need to store:

- full name;
- date of birth.

### Query requirements

Queries need to be implemented:

- Find all movies that were released in the last N years.
- Output information about actors who acted in the given movie.
- Output the information about actors who acted in at least N movies.
- Output the information about the actors that were directors of at least one of the movies.
- Delete all movies that were released more than the specified number of years ago.

## Solution

### Stack

- Java 17.
- Junit 4.
- Maven.
- PostgreSQL.
- HikariCP.

### Explanation

- A script to create `chema.sql` tables.
- A script for filling tables with `data.sql` values.
- A script to destroy tables `destroy.sql`.
- The queries are described in `QueriesToDataBaseFromTaskDescription.java` and implemented in `VideoLibraryService.java`
  .
- Demo in `App.java`.
- Database connection test in `DataBaseConnectionCreatorTest.java`.
- Query test in `VideoLibraryServiceTest.java`.

### Run

- Set the parameters for connecting to the database in `application.properties` or create a database with the specified
  parameters.
- Connect to the database using `psql`.
- Initialize the tables from the file:

```postgresql
\i ./schema.sql
```

- Fill the tables with values:

```postgresql
\i ./data.sql
```

- Run the `App.java`.