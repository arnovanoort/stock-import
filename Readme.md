
- database setup
    - start postgresql database 
        - sudo docker run -d --name dev-postgress -e POSTGRES_PASSWORD=Pass2020! -v ${HOME}/data/docker/postgres/:/var/lib/postgresql/data -p 5432:5432 postgres
    - sudo apt-get install pgadmin
    - create databases named stocks(used for prod) and stocks-test(used for testing)

- tiingo client
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
- introduce logging and remove System.out.println
- introduce backpressure while importing stocks
- read and process stock information zit directly from http


Fix
- recreate repository layer, ditch ReactiveCrudRepository
- remove onErrorResume
- Do not import if already present

- 