-- drop database if exists example_docker_db;
-- create database example_docker_db;
GRANT CREATE USER ON *.* TO 'root'@'%';
use memeExchange;

--
-- Table structure for table `example_table`
--

DROP TABLE IF EXISTS Feeds;
DROP TABLE IF EXISTS Posts;
DROP TABLE IF EXISTS Relationships;
DROP TABLE IF EXISTS History;
DROP TABLE IF EXISTS Memes;
DROP TABLE IF EXISTS Templates;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
user_id VARCHAR(255) NOT NULL,
password VARCHAR(255) NOT NULL,
first_name VARCHAR(255),
last_name VARCHAR(255),
PRIMARY KEY (user_id)
)
