-- May have to change .csv paths to absolute paths
COPY MENU
FROM 'data\menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM 'data\users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM 'data\orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 87257;

COPY ITEMSTATUS
FROM 'data\itemStatus.csv'
WITH DELIMITER ';';