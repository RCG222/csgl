/*
SQLyog Ultimate v8.32 
MySQL - 5.5.27 : Database - csgl
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`csgl` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `csgl`;

/*Table structure for table `producttable` */

DROP TABLE IF EXISTS `producttable`;

CREATE TABLE `producttable` (
  `product_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `product_name` varchar(50) DEFAULT NULL,
  `Product_specifications` varchar(10) DEFAULT NULL,
  `product_barcode` varchar(20) DEFAULT NULL,
  `product_shortName` varchar(10) DEFAULT NULL,
  `measure_unit` varchar(10) DEFAULT NULL,
  `retail_price` double DEFAULT NULL,
  `bargin_price` double DEFAULT NULL,
  `product_status` int(11) DEFAULT NULL,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `producttable` */

insert  into `producttable`(`product_id`,`product_name`,`Product_specifications`,`product_barcode`,`product_shortName`,`measure_unit`,`retail_price`,`bargin_price`,`product_status`) values (1,'可口可乐','350ml','23422','KKKL','罐',3.5,3,1);

/* Procedure structure for procedure `dele` */

/*!50003 DROP PROCEDURE IF EXISTS  `dele` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `dele`(IN p_id INT)
BEGIN
DELETE  FROM  producttable WHERE product_id=p_id;
END */$$
DELIMITER ;

/* Procedure structure for procedure `product_modify` */

/*!50003 DROP PROCEDURE IF EXISTS  `product_modify` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `product_modify`(IN id INT,IN p_name VARCHAR(50),IN p_sp VARCHAR(10),IN p_bar VARCHAR(20),IN p_sh VARCHAR(10),IN p_uni VARCHAR(10),IN p_p DOUBLE,IN p_pr DOUBLE,IN STATUS INT )
BEGIN 
IF id<0 THEN
SELECT * FROM producttable;
ELSEIF id=0 THEN
INSERT INTO producttable(product_name,Product_specifications,product_barcode,product_shortName,measure_unit,retail_price,bargin_price,product_status)VALUES (p_name,p_sp,p_bar,p_sh,p_uni,p_p,p_pr,statu);
ELSE UPDATE producttable SET product_name=p_name,Product_specifications=p_sp,product_barcode=p_bar,product_shortName=p_sh,measure_unit=p_uni,retail_price=p_p,bargin_price=p_pr,product_status=statu WHERE product_id=id;
END IF;
END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
