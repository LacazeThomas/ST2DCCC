---
title: 'Cloud Integration Docs'
author: 'Thomas LACAZE'
date: 29/12/2021
---

# Report Labs Cloud Integration - Thomas LACAZE DT M2 2022

Code source is available on **[Github](https://github.com/LacazeThomas/ST2DCCC)**

![Stack](images/stack.svg)

# Description

The application reads CSV files in order to transform them into objects and sends them to a database as well as JSON files.

# Explanation 



# Demonstration

## Inputs

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

## Outputs

#### H2 Database output

We can see the result of the `int-jdbc:outbound-channel-adapter` using the H2 console

![Actors table](images/actors-db.png)

![Movie table](images/movies-db.png)



#### JSON output
```JSON
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

## Problem encountered 

H2 console in Web

# License

**[MIT](https://github.com/LacazeThomas/ST2DCCC/blob/master/LICENSE)**