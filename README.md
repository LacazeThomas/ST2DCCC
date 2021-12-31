---
title: 'Cloud Integration Docs'
author: 'Thomas LACAZE'
date: 29/12/2021
---

# Report Labs Cloud Integration - Thomas LACAZE DT M2 2022

Code source is available on **[Github](https://github.com/LacazeThomas/ST2DCCC)**

![Stack](images/stack.svg)

## Description

The application reads CSV files in order to transform them into objects and sends them to a database as well as JSON files.


## Demonstration
### Inputs

`Movie.csv`
```csv
Movie ID;Rank;Rating;Title;ReleaseDate;Actors ID
100;3;4.2;Spider-man : no way home;2021-12-15;[1,2]
101;2;2.6;Matrix Resurrections;2021-12-22;[3,1]
102;1;8.0;The King's Man : Première Mission;2021-12-29;[2,1]
```

`Actors.csv`
```
Actor ID;FirstName;LastName;BithDate;Movies ID
1;Gemma;Arterton;1986-02-02;[100,101,102]
2;Keanu;Reeves;1964-09-02;[100,102]
3;Tobey;Maguire;1975-06-27;[101]
```

<div style="page-break-after: always;"></div>

### Outputs

#### H2 Database output

We can see the result of the `int-jdbc:outbound-channel-adapter` using the H2 console

![Actors table](images/actors-db.png)

![Movie table](images/movies-db.png)

<div style="page-break-after: always;"></div>

#### JSON output
```json
[   
    {
        "id":1,
        "firstName":"GEMMA",
        "lastName":"ARTERTON",
        "birthDate":[1986,2,2],
        "moviesID":[100,101,102]
    },
    {
        "id":2,
        "firstName":"KEANU"
        "lastName":"REEVES",
        "birthDate":[1964,9,2],
        "moviesID":[100,102]
    },
    {
        "id":3,
        "firstName":"TOBEY",
        "lastName":"MAGUIRE",
        "birthDate":[1975,6,27],
        "moviesID":[101]
    }
]
```

<div style="page-break-after: always;"></div>

## Explanation of `moviesApplication.xml`

```xml
    <jdbc:embedded-database id="datasource" type="H2">
        <jdbc:script location="classpath:setup-tables.sql"/>
    </jdbc:embedded-database>
```

First we have `jdbc:embedded-database` which permits to create a H2 database and to initialize it with the `setup-tables.sql` script.

```xml
    <int-file:inbound-channel-adapter
            channel="CSVInput"
            directory="./dataIn"
            filename-pattern="*.csv">
        <int:poller fixed-delay="5000" receive-timeout="5000" task-executor="pollerExecu or">
        </int:poller>
    </int-file:inbound-channel-adapter>
```
Next to that, we have an `int-file:inbound-channel-adapter` that allows to read CSV files in the `dataIn` directory, in order to send the information in the `CSVInput` channel.
```xml
    <int-file:splitter apply-sequence="false" charset="UTF-8" first-line-as-header="true"
                       input-channel="CSVInput" output-channel="SplittedCSV" auto-startup="true"/>
```

Once the data is sent to the `CSVInput` channel. We have an `int-file:splitter` which allows us to split the CSV files into several lines and send them to the `SplittedCSV` channel.

```xml
    <int:transformer input-channel="SplittedCSV"
                     output-channel="SplittedCSVWithUpperCase"
                     expression="payload.toUpperCase()"/>
```

Each line is transformed: the payload is converted to uppercase only, the result is sent to the channel `SplittedCSVWithUpperCase`.

<div style="page-break-after: always;"></div>

```xml
    <int:header-value-router input-channel="SplittedCSVWithUpperCase" header-name="file_name"
                             resolution-required="false">
        <int:mapping value="movies.csv" channel="Movie"/>
        <int:mapping value="actors.csv" channel="Actor"/>
    </int:header-value-router>
```

Then we have an `int:header-value-router` that allows us to decide which stream to use based on the file name. If the file is `movies.csv` then the stream will be `Movie` or the file is `actors.csv` then the stream will be `Actor`.

```xml
    <int:transformer input-channel="Movie" output-channel="MergedChannel" ref="mapToObject" method="mapMovie"/>
    <int:transformer input-channel="Actor" output-channel="MergedChannel" ref="mapToObject" method="mapActor"/>
```

We have again an `int:transformer` which allows to transform CSV rows into objects thanks to the `mapMovie` and `mapActor` functions.

```xml
    <int:recipient-list-router id="customRouter" input-channel="MergedChannel">
        <int:recipient channel="MergedChannelToDB"/>
        <int:recipient channel="MergedChannelToJson"/>
    </int:recipient-list-router>
```

The result of the transformation is pushed into the `MergedChannel` channel which is then sent into the `MergedChannelToDB` and `MergedChannelToJson` channels: the stream is thus sent into both channels.

```xml
    <int:channel id="MergedChannelMoviesFiltered"/>
    <int:channel id="MergedChannelActorsFiltered"/>
    <int:header-value-router input-channel="MergedChannelToDB" header-name="file_name">
        <int:mapping value="movies.csv" channel="MergedChannelMoviesFiltered"/>
        <int:mapping value="actors.csv" channel="MergedChannelActorsFiltered"/>
    </int:header-value-router>
```
The channel `MergedChannelToDB` is filtered according to the name of the file in the header. In order to be able to insert in the database according to the `Movie` object or the `Actor` object. So the `Movies` are sent in the channel `MergedChannelMoviesFiltered` and the `Actors` in the channel `MergedChannelActorsFiltered`.

<div style="page-break-after: always;"></div>

```xml
    <int-jdbc:outbound-channel-adapter
            query="insert into movies (id, rank, rating, title, releaseDate) values (:payload.id, :payload.rank, :payload.rating, :payload.title, :payload.releaseDate)"
            data-source="datasource"
            channel="MergedChannelMoviesFiltered"/>

    <int-jdbc:outbound-channel-adapter
            query="insert into actors (id, firstName, lastName, birthDate) values (:payload.id, :payload.firstName, :payload.lastName, :payload.birthDate)"
            data-source="datasource"
            channel="MergedChannelActorsFiltered"/>
```
Once the `Movies` and `Actors` are separated, we have an `int-jdbc:outbound-channel-adapter` that allows us to insert the data into the database. Depending on the structure of the table and the object.

```xml
    <int:channel id="MergedChannelAggregator"/>
    <int:aggregator id="myAggregator"
                    input-channel="MergedChannelToJson"
                    output-channel="MergedChannelAggregator"
                    correlation-strategy-expression="headers.file_originalFile"
                    release-strategy-expression="size()==3">
    </int:aggregator>
```

We come back here after the separation of the `MergedChannel` into `MergedChannelToJson` and `MergedChannelToDB`. We have on the `MergedChannelToJson` side an `int:aggregator` which allows to group objects into an object list. This is very important when we split the CSV file. So we apply two conditions: 
- Group the objects according to the name of the original CSV file in the header.
- Release objects according to the size of the list.

We have 3 actors and 3 movies. So we want to release the whole thing once we have 3 objects in the list.

```xml
    <int:channel id="JsonOuput"/>
    <int:object-to-json-transformer input-channel="MergedChannelAggregator" output-channel="JsonOuput"/>
    <int-file:outbound-channel-adapter channel="JsonOuput" filename-generator="nameGenerator"
                                       directory="./dataOut" append-new-line="true"/>

```

Once the objects are grouped we can convert them to JSON and send them to the `JsonOuput` channel. So that `int-file:outbound-channel-adapter` can generate a JSON file from the objects. We use a `nameGenerator` method that generates a file name from the name of the original CSV file that is present in the stream header.

<div style="page-break-after: always;"></div>

## Problem encountered 

In order to observe the result to the database, it was necessary to be able to inspect the H2 database. 

It was necessary for that to start Spring in a classical way and to add a dependency to launch a tomcat server with annotation for XML ressource. 

Then, we had to create an 'application.properties' file in order to launch an H2 console accessible at [localhost:8080/h2-console](localhost:8080/h2-console)


`main.java`
```java
@SpringBootApplication
@ImportResource("classpath:moviesApplication.xml")
public class Main {
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}
```

`application.properties`
```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

server.port=8080
logging.level.root=DEBUG
```

## Installation Steps

Import the project in IntelliJ IDEA (File -> Open and select the project's folder). You can now run the program via `run` menu.

## License

**[MIT](https://github.com/LacazeThomas/ST2DCCC/blob/master/LICENSE)**