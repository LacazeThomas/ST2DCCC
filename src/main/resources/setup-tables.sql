create table IF NOT EXISTS movies
(
    id
    integer
    identity
    primary
    key,
    rank
    integer,
    rating
    double,
    title
    varchar
(
    100
), releaseDate date);
create table IF NOT EXISTS actors
(
    id
    integer
    identity
    primary
    key,
    firstName
    varchar
(
    100
),lastName varchar
(
    100
), birthDate date);