# Ticketing System API

Simple application based on SpringBoot and Java 17 which exposes a REST API.
The application handle events and creates and processes violation in an asynchronous way


## Getting Started

Checkout the git repository

### Prerequisites

-- JAVA 17
-- MAVEN


### Installing

After checking out the project, you may need to install the dependencies

    mvn clean install

That should be all. After that we may start the application via our IDE or using cmd:

    mvn spring-boot:run


## Running the app and tests

You may test the rest functionalities via [swagger ui](http://localhost:8080/swagger-ui)



Unit and Integrations tests are located under src/test/java/com/traffic/eventmanager


## License

This project is licensed under the [CC0 1.0 Universal](LICENSE.md)
Creative Commons License - see the [LICENSE.md](LICENSE.md) file for
details
