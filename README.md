# Patient Portal with Vaadin 8

This repo contains a Vaadin 8 implementation of the https://github.com/vaadin/patient-portal-demo application.

## Requirements
- Java 8+
- Maven

## Building
Building this repo requires having `patient-portal-backend-0.0.1-SNAPSHOT.jar` in the local maven cache. It needs to be built first from the https://github.com/vaadin/patient-portal-demo-backend repo:
 - clone and build the data layer (`patient-portal-backend-0.0.1-SNAPSHOT.jar`) from the https://github.com/vaadin/patient-portal-demo-backend repo
 - `mvn package`

## Running locally
 - `mvn spring-boot:run`

The app would be running with its own in-memory database on http://localhost:8080.

It's also possible to run the app with an external database used by other Patient Portal implementations. See the comments in the `applicaiton.properties` file for details.

## Docker image
 - build the app: `mvn package`
 - build an image: `docker build -t vaadin-8 .`
 - run this image standalone: `docker run -it --rm -p 8080:8080 vaadin-8`
 - OR run this image with an external database:
   ```
   docker run -it --rm -p 8080:8080 \
         --env SPRING_DATASOURCE_URL=jdbc:mysql://database:3306/pp\?useSSL=false \
         --env SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.jdbc.Driver \
         --env SPRING_DATASOURCE_USERNAME=pp \
         --env SPRING_DATASOURCE_PASSWORD=pp \
         vaadin-8 /wait-for database:3306 -- /start.sh
   ```
