DROP INDEX IF EXISTS index1;
CREATE INDEX index1
ON orders
(timeStampRecieved);

DROP INDEX IF EXISTS index2;
CREATE INDEX index2
ON ItemStatus
( orderid );