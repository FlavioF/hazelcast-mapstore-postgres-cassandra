hazelcast-store-postgres-cassandra
====================================
This project contains Examples of persisting Hazelcast data in PostgreSQL and Cassandra asynchronously.

# Pre-requisites

* JDK 8
* Maven 3.1.0 or newer
* PostgreSQL 9.3
* Cassandra 2.1.2

# Build

## Clone
```
git clone https://github.com/FlavioF/hazelcast-store-postgres-cassandra.git
cd hazelcast-store-postgres-cassandra
```

## Configuration

Edit persistence.xml file located in src/test/resources/META-INF/persistence.xml  with your PostgreSQL configuration
```
  <property name="javax.persistence.jdbc.databasename" value="yourpostgresdb" />      
  <property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/yourpostgresdb" />      
  <property name="javax.persistence.jdbc.user" value="yourpostgresuser" />
  <property name="javax.persistence.jdbc.password" value="yourpassword" />
```


## Run

```
mvn clean install -Dcassandra.ip=<your cassandra ip address>
```