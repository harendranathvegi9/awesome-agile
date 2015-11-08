# docker-machine

Docker is used as part of the integration environment by this project. Specifically, it is used to bring up a local instance of the PostgreSQL. Docker is a required dependency for the integration tests to run. Instructions on how to configure Docker under different operating systems follow below.

## Mac OS X
1. Download and install [Docker Toolbox](https://www.docker.com/docker-toolbox) for Mac OS X.
2. Run the following command in the Terminal from the project's directory:
```
source machine.sh
```
This will create a new Docker virtual machine named `awesome-agile` that the test will be able to connect to and expose Docker environment variables required for the tests to run.
3. Run tests with maven as one normally would, i.e.
```
mvn  test -Dtest=DatabaseIntegrationTest -DfailIfNoTests=false
```

## Windows
1. Download and install [Docker Toolbox](https://www.docker.com/docker-toolbox) for Windows.
2. Make sure you have Bash installed (as part of either Cygwin or MinGW).
3. Run the following command in Bash terminal from the project's directory:
```
source machine.sh
```
This will create a new Docker virtual machine named `awesome-agile` that the test will be able to connect to and expose Docker environment variables required for the tests to run.
4. Run tests with maven as one normally would, i.e.
```
mvn  test -Dtest=DatabaseIntegrationTest -DfailIfNoTests=false
```

## Travis-CI
The integration tests have been configured and checked to pass under Travis-CI.
