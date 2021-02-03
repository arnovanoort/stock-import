# Stock reader
This application will enable you to fetch stock prices from tiingo, store these in a database and emit an event that can be used by other applications.

## Getting started

- database setup
    - start postgresql database 
        - sudo docker run -d --name dev-postgress -e POSTGRES_PASSWORD=Pass2020! -v ${HOME}/data/docker/postgres/:/var/lib/postgresql/data -p 5432:5432 postgres
    - sudo apt-get install pgadmin
    - create databases named stocks(used by the application) and stocks-test(used for junit testing)

- You will need credentials to use the tiingo api:
    - go to https://api.tiingo.com/ and signup
    - after signing up yu will be provided a token
    - update the token in application.properties(tiingo.token)

- application.properties
    - an example application.properties exists. copy the example and store it as application.properties.
        Update this with the correct variables(the tiingo.supported-tickers-location will be explained soon)

- download stocks zip
    - Download zip from here: https://apimedia.tiingo.com/docs/tiingo/daily/supported_tickers.zip
    - unpack and store somewhere on your filesystem
    - update tiingo.supported-tickers-location in application.properties to point to this file.

        
Now fill the database with stocks
POST http://localhost:8080/stocks/importlocal        

And import the stockprices
First query the just created stockmarkets in the database and fetch the uuid of the market you want to import the prices and replace that value with the placeholder in the url below

POST http://localhost:8080/stockmarkets/<replace-with stock-market-uuid>/prices?from=2020-11-01&to=2020-11-10

Todo
- build frontend to display stocks
- introduce backpressure while importing stocks
- read and process stock information zit directly from http
- non happy flow unit/integration tests.


Fix
- recreate repository layer, ditch ReactiveCrudRepository
 
## Technical details

This is a fully non blocking application:
- frontend uses spring webflux
- business logic written using reactor
- database layer uses the non blocking r2dbc postgresql driver.

The integration tests use the postgresql [testcontainer](https://www.testcontainers.org/). This means you need the docker daemon running while executing the tests.