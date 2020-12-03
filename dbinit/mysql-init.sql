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
);	

CREATE TABLE Relationships (
FromUserId VARCHAR(255) NOT NULL,
ToUserId VARCHAR(255) NOT NULL,
CreatedDateTime DATETIME NOT NULL default current_timestamp on update current_timestamp,
CONSTRAINT F1 FOREIGN KEY (FromUserId) REFERENCES Users(user_id),
CONSTRAINT F2 FOREIGN KEY (ToUserId) REFERENCES Users(user_id),
PRIMARY KEY (FromUserId, ToUserId)
);

CREATE TABLE Templates (
template_id VARCHAR(255) NOT NULL,
image_url VARCHAR(512) NOT NULL,
CreatedDateTime DATETIME NOT NULL default current_timestamp on update current_timestamp,
PRIMARY KEY (template_id)
);

CREATE TABLE Memes (
id INT NOT NULL AUTO_INCREMENT,
user_id VARCHAR(255) NOT NULL,
template_id VARCHAR(255) NOT NULL,
category VARCHAR(255) NOT NULL,
caption VARCHAR(255) NOT NULL,
image_url VARCHAR(512) NOT NULL,
CreatedDateTime DATETIME NOT NULL default current_timestamp on update current_timestamp,
PRIMARY KEY (id),
FOREIGN KEY (user_id) REFERENCES Users(user_id),
FOREIGN KEY (template_id) REFERENCES Templates(template_id)
);

CREATE TABLE Feeds (
id INT NOT NULL AUTO_INCREMENT,
subscriber_id VARCHAR(255) NOT NULL,
meme_id INT NOT NULL,
CreatedDateTime DATETIME NOT NULL default current_timestamp on update current_timestamp,
PRIMARY KEY (id),
FOREIGN KEY (subscriber_id) REFERENCES Users(user_id),
FOREIGN KEY (meme_id) REFERENCES Memes(id)
);

CREATE TABLE History (
id INT NOT NULL AUTO_INCREMENT,
user_id VARCHAR(255) NOT NULL,
meme_id INT NOT NULL,
CreatedDateTime DATETIME NOT NULL default current_timestamp on update current_timestamp,
PRIMARY KEY (id),
FOREIGN KEY (user_id) REFERENCES Users(user_id),
FOREIGN KEY (meme_id) REFERENCES Memes(id)
);

