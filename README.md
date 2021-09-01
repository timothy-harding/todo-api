# Todo Service
This asset provides feature to create, update and query todo items
The asset also provides a batch utility to mark stale records, hourly and can be customised
The service is extensible for any new requirements related to todo management.

### Assumptions
* Todo Items which are already past due (status/date) cannot be added
* No restriction to update description along with the status update
* Used field Status date instead of DONE date, to be reused for all status
* Date-time format is `yyyy-MM-dd'T'HH:mm:ss'Z'` as per ISO 8601 standard
* Status date is updated whenever status of todo item changes
* Created date and status date is defaulted with time of creation (only while adding a todo, not during an update)
* Scheduler is set to run every hour
* Default properties are defined in the application.properties file (app port : `9080`)

### Service Endpoints
[API Collections](postman/Todo API.postman_collection.json)

### Technical information
This is a SpringBoot application that uses the following frameworks/libraries:

* Java 11
* Open API tools (API/Models generator & swagger UI)
* H2 in-memory database / JPA
* Lombok (Code generation and logging)
* Mapstruct (Mapper for dto to entity conversion)
* Junit
* Cucumber automation tests
* Docker

### Dev Info

This asset uses a Openapi / swagger and Mapstruct Maven plugin to generate code, you can run the generation by using:
```
mvn compile
```

This asset uses Maven, to build it locally use
```
mvn clean install
```

This asset uses Maven, to run it locally use
```
mvn spring-boot:run
```

To run the tests use
```
mvn test
```
This asset is dockerized, to build the docker file use
```
mvn install dockerfile:build
```

This asset is dockerized, to run the docker image as container use
```
docker run -p 9080:9080 -t interview/todo-api
```

### TODO / Improvements
* Increase test coverage
* Increase Javadoc/ comments
* Enable caching
* Enable pagination
