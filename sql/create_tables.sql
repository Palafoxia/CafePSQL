DROP TABLE IF EXISTS Users CASCADE;
DROP TABLE IF EXISTS Orders CASCADE;
DROP TABLE IF EXISTS Menu CASCADE;
DROP TABLE IF EXISTS ItemStatus;

CREATE TABLE Users(
	login char(50) UNIQUE NOT NULL, 
	phoneNum char(16) UNIQUE, 
	password char(50) NOT NULL,
	favItems char(400),
	type char(8) NOT NULL,
	PRIMARY KEY(login));

CREATE TABLE Menu(
	itemName char(50) UNIQUE NOT NULL,
	type char(20) NOT NULL,
	price real NOT NULL,
	description char(400),
	imageURL char(256),
	PRIMARY KEY(itemName));

CREATE TABLE Orders(
	orderid serial UNIQUE NOT NULL,
	login char(50), 
	paid boolean,
	timeStampRecieved timestamp NOT NULL,
	total real NOT NULL,
	PRIMARY KEY(orderid));

CREATE TABLE ItemStatus(
	orderid integer,
	itemName char(50), 
	lastUpdated timestamp NOT NULL,
	status char(20), 
	comments char(130), 
	PRIMARY KEY(orderid,itemName),
	FOREIGN KEY(orderid) REFERENCES Orders(orderid) ON DELETE CASCADE,
	FOREIGN KEY(itemName) REFERENCES Menu(itemName) ON DELETE CASCADE);