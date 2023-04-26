DROP TABLE DELINQUENCY_MODEL;
SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `DELINQUENCY_MODEL` (
  `DELIQUENCY_MODEL_ID` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `ACTION` varchar(255) DEFAULT NULL,
  `CANBLACKLIST` int DEFAULT NULL,
  `CANDEFAULT` int DEFAULT NULL,
  `CANSUSPEND` int DEFAULT NULL,
  `DAYS_AFTER_DEFAULT` int DEFAULT NULL,
  `DAYS_TO_SUSPENSION` int DEFAULT NULL,
  `INTRASH` varchar(255) DEFAULT NULL,
  `PENALTY` int DEFAULT NULL,
  `PENALTY_TYPE` varchar(255) DEFAULT NULL,
  `PERIOD` int DEFAULT NULL,
  `PRODUCT_ID_FK` int DEFAULT NULL,
FOREIGN KEY (`PRODUCT_ID_FK`) REFERENCES `product` (`PRODUCT_ID`)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS=1;