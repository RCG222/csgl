#### MySQL数据库的一些信息

###### 触发器

1. 自动添加货品名称缩写

```mysql
CREATE
    /*[DEFINER = { user | CURRENT_USER }]*/
    TRIGGER `csgl`.`AutoInsertSc` BEFORE INSERT
    ON `csgl`.`producttable`
    FOR EACH ROW 
    BEGIN
    DECLARE sc VARCHAR(50);
    SELECT pysxcx(new.product_name) INTO sc;
    SET  new.product_shortName=sc;
    END $
    
```

2. 自动添加库存信息

```mysql
CREATE
    TRIGGER `csgl`.`AutoInsertKc` AFTER INSERT
    ON `csgl`.`producttable`
    FOR EACH ROW 
    BEGIN
    INSERT INTO kcxxb(product_id)VALUES(new.product_id);
    END $

```

3. 自动删除库存信息

```mysql

CREATE
    TRIGGER `csgl`.`AutoDeletetKc` BEFORE DELETE
    ON `csgl`.`producttable`
    FOR EACH ROW 
    BEGIN
    DELETE FROM kcxxb WHERE product_id=old.product_id;
    END $
```

4. 自动更改名称缩写

```mysql
CREATE
    TRIGGER `csgl`.`AutoUpdateMcsx` BEFORE UPDATE
    ON `csgl`.`producttable`
    FOR EACH ROW 
    BEGIN
    DECLARE sc VARCHAR(50);
    SELECT pysxcx(new.product_name) INTO sc;
    SET new.product_shortName=sc;
    END $
```

5. 删除收银记录，自动触发清楚消费记录（增加库存，扣除积分等）

```mysql
CREATE
   
    TRIGGER `Delete_syjlmx_update_kcxxb_hyxxb_after_delete` AFTER DELETE ON `syjlb` 
    FOR EACH ROW BEGIN
    DECLARE syid1 INT;
    DECLARE hyid1 INT;
    DECLARE ssje1 INT;
    CREATE TEMPORARY TABLE lsb(hpid INT ,xssl NUMERIC(9,3));
    SELECT old.syid,old.hyid,old.ssje INTO syid1,hyid1,ssje1;
    IF hyid1 >= 10000 THEN
UPDATE hyxxb SET knye=knye+ssje1 ,kyjf=kyjf-FLOOR(ssje1) WHERE hyid=hyid1;
END IF;
INSERT INTO lsb SELECT hpid, SUM(xssl) FROM syjlmxb WHERE syid=syid1 GROUP BY hpid;
UPDATE kcxxb,lsb SET kcsl=kcsl+lsb.xssl WHERE lsb.hpid=kcxxb.product_id;
DELETE FROM syjlmxb WHERE syid=syid1;
DROP TEMPORARY TABLE IF EXISTS lsb;

    END;
$$

DELIMITER ;
```

6. 确认入库后自动增加库存

```mysql
DELIMITER $$

CREATE
    /*[DEFINER = { user | CURRENT_USER }]*/
    TRIGGER `csgl`.`Auto_Update_Kcxxb_After_qrrk` AFTER UPDATE
    ON `csgl`.`gjjlb`
    FOR EACH ROW 
    BEGIN
    IF new.gjzt=1 THEN 
    UPDATE kcxxb,gjjlmxb SET kcsl=kcsl+gjjlmxb.gjsl  WHERE new.gjid=gjjlmxb.gjid AND kcxxb.product_id=gjjlmxb.hpid;
    END IF;
    END$$

DELIMITER ;
```



###### 添加一条方便面记录

```mysql 
INSERT INTO producttable(product_name,product_specifications,product_barcode,measure_unit,retail_price,bargin_price,product_status) VALUES ('方便面','120g','12312','袋',3.0,2.5,0);
```

###### 存储过程

1. 增改查货品信息的存储过程

```mysql
CREATE PROCEDURE product_modify(IN id INT,IN p_name VARCHAR(50),IN p_sp VARCHAR(10),IN p_bar VARCHAR(20),IN p_uni VARCHAR(10),IN p_p DOUBLE,IN p_pr DOUBLE,IN statu INT )
BEGIN 
IF id<0 THEN
SELECT * FROM producttable;
ELSEIF id=0 THEN
INSERT INTO producttable(product_name,Product_specifications,product_barcode,measure_unit,retail_price,bargin_price,product_status)VALUES (p_name,p_sp,p_bar,p_uni,p_p,p_pr,statu);
ELSE UPDATE producttable SET product_name=p_name,Product_specifications=p_sp,product_barcode=p_bar,measure_unit=p_uni,retail_price=p_p,bargin_price=p_pr,product_status=statu WHERE product_id=id;
END IF;
END $
```

2. 删除货品信息的存储过程

```mysql
CREATE PROCEDURE dele(IN p_id INT)
BEGIN
DELETE  FROM  producttable WHERE product_id=p_id;
END
```

3. 收银记录存盘

```mysql
DELIMITER $$

CREATE
    PROCEDURE `csgl`.`SYJLCP`(IN syyid1 INT ,IN hyid1 INT ,IN ysje1 NUMERIC(9,2),IN ssje1 NUMERIC(9,2),IN qtje1 NUMERIC(9,2),IN zffs1 INT, IN xsmx1 VARCHAR(8000))
    BEGIN
    DECLARE syid1 INT;
    -- 创建临时表slb来保存查询到的货品id和销售数量信息
    DROP TEMPORARY TABLE IF EXISTS slb;
    CREATE TEMPORARY TABLE slb(hpid INT ,xssl NUMERIC(9,3));
    -- 1.在收银记录表中插入数据
    INSERT INTO syjlb(syyid, hyid ,ysje,ssje,zffs) VALUES(syyid1,hyid1,ysje1,ssje1,zffs1);
    -- 使用系统定义的全局变量`@@identity`获取最近插入的信息的标识列
    SELECT @@identity INTO syid1;
    -- 2.将销售明细的字符串转换为临时表，临时表在函数中创建，创建完后在这个存储过程中直接调用
    -- 并在明细表中插入数据
    SELECT xsmx_to_lsb(xsmx1);
    INSERT syjlmxb(syid,hpid,xssl,lsj,cxj) SELECT syid1, lsb.hpid, lsb.xssl, producttable.retail_price, producttable.bargin_price FROM lsb,producttable WHERE lsb.hpid=producttable.product_id;
    -- 3.通过分组查询group by将临时表lsb中的数据按照hpid分组，统计每个货品消耗的总数量
    -- 并更新库存信息
    INSERT INTO slb SELECT hpid, SUM(xssl) FROM lsb GROUP BY hpid;
    UPDATE kcxxb,slb SET kcsl=kcsl-slb.xssl  WHERE kcxxb.product_id=slb.hpid; 
    -- 4.判断是否是会员，是会员的话更新会员表
    IF hyid1 > 10000 THEN
    UPDATE hyxxb SET knye=knye-ssje1 ,kyjf=FLOOR(ssje1) WHERE hyid=hyid1;
    END IF;
    END$$
DELIMITER ;
```

> 使用一个表中的数据去更新另一个表举例
>
> `UPDATE kcxxb,slb SET kcsl=kcsl-slb.xssl  WHERE kcxxb.product_id=slb.hpid;`
>
> 将一个表中的数据插入另一个表中举例
>
> `INSERT INTO slb SELECT hpid, SUM(xssl) FROM lsb GROUP BY hpid;`
>
> 【注】插入数据时，insert into 后面省略values，使用select语句获取源表；
>
> ---

4. 收银记录查询

```mysql
![屏幕截图(286)](C:\Users\neo\Pictures\Screenshots\屏幕截图(286).pngDELIMITER $$

CREATE
    PROCEDURE `csgl`.`Syjlcx`(IN cxlb INT,IN cxcs INT)
    BEGIN
	IF cxlb=0 THEN
	SELECT syid,syyid,sysj,hyid,ysje,ssje,qtje,zfmc FROM syjlb INNER JOIN zffsb ON zffs=zfid WHERE syyid=cxcs AND sysj >= DATE_SUB(CURDATE(),INTERVAL 1 DAY);
	ELSE IF cxlb=1 THEN
	SELECT * FROM v_syjlmxb WHERE syid=cxcs;
	END IF;
	END IF;
    END$$
DELIMITER ;
```

> 输入（0，收银员id）查询的是收银记录只可查询当天，输入（1，收银id）查询的是收银明细
>
> DATE_SUB()函数用于减少日期，第一个参数为日期，第二个参数为减少的日期长度
>
> CURDATE()函数返回当前日期
>
> ---

​	收银记录查询改进

```mysql
DELIMITER $$

USE `csgl`$$

DROP PROCEDURE IF EXISTS `Syjlcx_02`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `Syjlcx_02`(IN cxlb INT,IN cxcs INT)
BEGIN
	IF cxlb=0 THEN
	SELECT syid,syyid,sysj,hyid,ysje,ssje,qtje,zfmc thid FROM syjlb INNER JOIN zffsb ON zffs=zfid WHERE syyid=cxcs AND sysj > DATE_SUB(CURDATE(),INTERVAL 0 DAY);
	ELSE IF cxlb=1 THEN
	SELECT * FROM v_syjlmxb WHERE syid=cxcs;
	ELSE IF cxlb=2 THEN
	SELECT syid,syyid,sysj,hyid,ysje,ssje,qtje,zfmc,thid FROM syjlb INNER JOIN zffsb ON zffs=zfid WHERE sysj > DATE_SUB(CURDATE(),INTERVAL 6 DAY);
	END IF;
	END IF;
	END IF;
    END$$

DELIMITER ;
```

> 新增查询类别2，输入`Syjlcx_02`(2,任意值)用来查询7天内所有收银员的所有信息 
>
> 新增退货id的查询`thid`
>
> ----



5. 退货信息存盘

```mysql 
DELIMITER $$

CREATE
    /*[DEFINER = { user | CURRENT_USER }]*/
    PROCEDURE `csgl`.`ThjlCp`(IN syid1 INT)
    /*LANGUAGE SQL
    | [NOT] DETERMINISTIC
    | { CONTAINS SQL | NO SQL | READS SQL DATA | MODIFIES SQL DATA }
    | SQL SECURITY { DEFINER | INVOKER }
    | COMMENT 'string'*/
    BEGIN
	DECLARE hyid1 INT;
	DECLARE ssje1 NUMERIC(9,2);
	CREATE TEMPORARY TABLE lsb(hpid INT ,xssl NUMERIC(9,3));
	SELECT hyid,ssje INTO hyid1,ssje1 FROM syjlb WHERE syid=syid1;
	IF hyid1 >= 10000 THEN
	UPDATE hyxxb SET knye=knye+ssje1 ,kyjf=kyjf-FLOOR(ssje1) WHERE hyid=hyid1;
	END IF;
	INSERT INTO lsb SELECT hpid, SUM(xssl) FROM syjlmxb WHERE syid=syid1 GROUP BY hpid;
	UPDATE kcxxb,lsb SET kcsl=kcsl+lsb.xssl WHERE lsb.hpid=kcxxb.product_id;
	DELETE FROM syjlmxb WHERE syid=syid1;
	DELETE FROM syjlb WHERE syid=syid1;
	DROP TEMPORARY TABLE IF EXISTS lsb;
    END$$

DELIMITER ;
```

6. 查看会员信息表，库存信息表，收银记录表，收银记录明细表

```mysql
DELIMITER $$

CREATE
    /*[DEFINER = { user | CURRENT_USER }]*/
    PROCEDURE `csgl`.`checkAll`()
    /*LANGUAGE SQL
    | [NOT] DETERMINISTIC
    | { CONTAINS SQL | NO SQL | READS SQL DATA | MODIFIES SQL DATA }
    | SQL SECURITY { DEFINER | INVOKER }
    | COMMENT 'string'*/
    BEGIN
	SELECT * FROM hyxxb;
	SELECT * FROM kcxxb;
	SELECT * FROM syjlb;
	SELECT * FROM syjlmxb;
    END$$

DELIMITER ;
```

7. 退货记录存盘新

```mysql
DELIMITER $$

CREATE
    /*[DEFINER = { user | CURRENT_USER }]*/
    PROCEDURE `csgl`.`ThjlCp_new`(IN syid1 INT)
    
    BEGIN
    DELETE FROM syjlb WHERE syid=syid1; 
    END$$

DELIMITER ;
```

8. 7天内退货记录存盘

```mysql
DELIMITER $$

CREATE
    /*[DEFINER = { user | CURRENT_USER }]*/
    PROCEDURE `csgl`.`ThjlCp_new_new`(IN syid1 INT)  
    BEGIN
    DECLARE syid_new INT;
    DECLARE hyid1 INT;
    DECLARE ssje1 NUMERIC(9,2);
    CREATE TEMPORARY TABLE lsb(hpid INT ,xssl NUMERIC(9,3));
    -- 判断收银id,为负则是负销售
    IF syid1>0 THEN
    DELETE FROM syjlb WHERE syid=syid1;
    ELSE 
    -- 插入负销售记录
      INSERT INTO syjlb(syyid, hyid ,ysje,ssje,qtje,zffs,thid) SELECT syyid, hyid ,-ysje,-ssje,-qtje,zffs,-1 FROM syjlb WHERE syid=-syid1 ;
    UPDATE syjlb SET thid=-1 WHERE syid=-syid1;
    SET syid_new=@@identity;
    -- 为明细表插入新的负销售记录
    INSERT syjlmxb(syid,hpid,xssl,lsj,cxj) SELECT syid_new,hpid,-xssl,lsj,cxj FROM syjlmxb WHERE syid=-syid1;
    -- 增加库存
    INSERT INTO lsb SELECT hpid, SUM(xssl) FROM syjlmxb WHERE syid=-syid1 GROUP BY hpid;
    UPDATE kcxxb,lsb SET kcsl=kcsl+lsb.xssl  WHERE kcxxb.product_id=lsb.hpid; 
    SELECT hyid,ssje INTO hyid1,ssje1 FROM syjlb WHERE syid=-syid1;
    -- 判断是否时会员
    IF hyid1>10000 THEN 
    UPDATE hyxxb SET knye=knye+ssje1 ,kyjf=kyjf-FLOOR(ssje1) WHERE hyid=hyid1;
    END IF; 
    END IF; 
    DROP TEMPORARY TABLE IF EXISTS lsb;
    END$$

DELIMITER ;
```

9. 供应商信息维护

```mysql
DELIMITER $
CREATE PROCEDURE gysxiwh(IN id INT,IN gysmc1 VARCHAR(50),IN lxren1 VARCHAR(50),lxfs1 VARCHAR(50))
BEGIN 
DECLARE mcsx1 VARCHAR(50);
SELECT PysxCx(gysmc1) INTO mcsx1;
IF id=0 THEN
INSERT INTO gysxxb(gysmc,mcsx,lxren,lxfs)VALUES (gysmc1, mcsx1, lxren1, lxfs1);
ELSE UPDATE gysxxb SET gysmc=gysmc1,mcsx=mcsx1,lxren=lxren1,lxfs=lxfs1 WHERE gysid=id;
END IF;
END $
```

>`CALL gysxiwh(0,'康师傅','张三',1001001001);`
>-- 添加一个供应商康师傅，联系人是张三
>
>---

10. 购进记录存盘

```mysql
DELIMITER $$

CREATE
    PROCEDURE `csgl`.`gjjlcp`(IN ywy1 VARCHAR(50),IN gysid1 INT,IN gjmx1 VARCHAR(8000))
    BEGIN
    DECLARE gjje1 NUMERIC(9,2);
    DECLARE gjid1 INT;
    SELECT gjmx_to_lsb(gjmx1);
    SELECT SUM(gjsl*bcdj) FROM gjlsb INTO gjje1;
    INSERT INTO gjjlb(ywy,gysid,gjje) VALUES (ywy1,gysid1,gjje1);
    SET gjid1=@@identity;
    INSERT INTO gjjlmxb(gjid,hpid,gjsl,bcdj) SELECT gjid1,hpid,gjsl,bcdj FROM gjlsb;
    DROP TEMPORARY TABLE IF EXISTS gjlsb;
    END$$

DELIMITER ;
```

> `CALL gjjlcp('李四',1,'2,10,7,1,5,1.5');`
> -- 向康师傅（id为1）进十瓶老干妈（id：2），5瓶矿泉水（id：1）
>
> ----

11. 确认入库

```mysql
DELIMITER $$

CREATE
    PROCEDURE `csgl`.`qrrk`(IN gjid1 INT)
    BEGIN
    UPDATE gjjlb SET gjzt=1 WHERE gjid=gjid1;
    END$$
DELIMITER ;
```

12. 查看购进信息相关的表

```mysql
DELIMITER $$

CREATE
    PROCEDURE `csgl`.`checkgj`()
    BEGIN
    SELECT * FROM gjjlb;
    SELECT * FROM gjjlmxb;
    SELECT * FROM jsjlb;
    END$$
DELIMITER ;
```

13. 结算记录存盘

```mysql
DELIMITER $$
CREATE
    PROCEDURE `csgl`.`jsjlcp`(IN gjid1 INT,IN jsren1 VARCHAR(8000))
    BEGIN
    INSERT INTO jsjlb(gjid,jsje,jsren) SELECT gjid1,gjje,jsren1 FROM gjjlb WHERE gjid1=gjid;
    UPDATE gjjlb SET gjzt=2 WHERE gjid1=gjid;
    END$$
DELIMITER ;
```





###### 函数

> 出现错误：ERROR 1418 (HY000): This function has none of DETERMINISTIC, NO SQL, or READS SQL DATA in its declaration and binary logging is enabled (you *might* want to use the less safe log_bin_trust_function_creators variable)

> 订正方法：
>
> - SET GLOBAL log_bin_trust_function_creators = 1;
>
> - 在BEGIN前声明DETERMINISTIC或NO SQL与READS SQL DATA中的一个；

1. 获取货品信息拼音缩写的函数

```mysql
DELIMITER $
CREATE FUNCTION PysxCx(hz VARCHAR(50)) RETURNS VARCHAR(50)

BEGIN
	#len表示获取字符串的长度
	DECLARE len INT;
	#i记录循环次数
	DECLARE i INT DEFAULT 1;
	#str表示字符串缩写
	DECLARE str VARCHAR(50) DEFAULT '';
	#st表示被截取单个字符
	DECLARE st VARCHAR(50);
	#s表示被截取单个字符的拼音首字母
	DECLARE s VARCHAR(50);
    SELECT CHAR_LENGTH(hz) INTO len;	
	WHILE i <= len DO
		SELECT SUBSTRING(hz,i,1) INTO st;
		SELECT hzpyb.jp INTO s FROM hzpyb WHERE hzpyb.hz=st;
		SELECT CONCAT(str,s) INTO str;
		
		SET i=i+1;
	END WHILE;
	SELECT UPPER(str) INTO str;
	RETURN str;
END $
DELIMITER ;

```

> 需要改进的地方
>
> 1. 查找拼音缩写的时候判断文字是否有拼音
> 2. 更新时要判断是否更新了货品名称，如果没有更新名称就不改变拼音缩写
>
> ---



2. 将收银记录明细的字符串转换为临时表

```mysql
DELIMITER $$

CREATE
    /*[DEFINER = { user | CURRENT_USER }]*/
    FUNCTION `csgl`.`xsmx_to_lsb`(str VARCHAR(8000)) RETURNS INT
    BEGIN
    DECLARE k INT;
    DECLARE hpid INT;
    DECLARE xssl NUMERIC(9,3);
    DECLARE charindex INT DEFAULT 1;
    -- 如果临时表lsb存在就执行删除，不存在就创建
    DROP TEMPORARY TABLE IF EXISTS lsb;
    CREATE TEMPORARY TABLE lsb(hpid INT ,xssl NUMERIC(9,3));
    -- 循环往临时表中插入数据
    WHILE charindex>0 DO
    SET k = (SELECT INSTR(str,','))-1;
    SET hpid = (SELECT LEFT(str,k));
    SET str = (SELECT SUBSTRING(str,k+2));
    SET k = (SELECT INSTR(str,','))-1;
    IF k>0 THEN
    SET xssl = (SELECT LEFT(str,k));
    SET str = (SELECT SUBSTRING(str,k+2));
    ELSE
    SET xssl = str;  
    END IF;  
    INSERT INTO lsb VALUES(hpid,xssl);
    SET charindex = k;
    END WHILE;
    RETURN 0;
    END$$
DELIMITER ;
```

> 临时表的删除和创建语法举例
>
>  ` DROP TEMPORARY TABLE IF EXISTS lsb; `
>  `CREATE TEMPORARY TABLE lsb(hpid INT ,xssl NUMERIC(9,3));`
>
> 【注】1. 临时表只在当前会话有效，即一个connection内有效
>
> 聚合函数`INSTR`
>
> ​	参数一 ：字符串
>
> ​	参数二 ：需要查询的字串
>
> 返回值：字串第一次出现的位置
>
> ---

3. 购进明细转化为临时表

```mysql
DELIMITER $$

CREATE
    FUNCTION `csgl`.`gjmx_to_lsb`(str VARCHAR(8000)) RETURNS INT
    BEGIN
    DECLARE k INT DEFAULT 1;
    DECLARE hpid INT;
    DECLARE gjsl NUMERIC(9,3);
    DECLARE bcdj NUMERIC(9,3);
    CREATE TEMPORARY TABLE gjlsb(hpid INT ,gjsl NUMERIC(9,3),bcdj NUMERIC(9,2));
    WHILE k>0 DO
    SET k = INSTR(str,',')-1;
    SET hpid = LEFT(str,k);
    SET str = SUBSTRING(str,k+2);
    SET k = INSTR(str,',')-1;
    SET gjsl = LEFT(str,k);
    SET str = SUBSTRING(str,k+2);
    SET k = INSTR(str,',')-1;
    IF k>0 THEN
    SET bcdj = LEFT(str,k);
    SET str = SUBSTRING(str,k+2);
    ELSE
    SET bcdj = str;  
    END IF;  
    INSERT INTO gjlsb VALUES(hpid,gjsl,bcdj);
    END WHILE;
    RETURN 0;
    END$$
DELIMITER ;
```



###### 初始化表

```mysql
TRUNCATE TABLE
```



更改表

```mysql
ALTER TABLE hyxxb MODIFY COLUMN scxfsj DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;  //设置时间自动更改当更新时on update后跟新值
```

```mysql
CREATE TABLE Hyxxb(
	hyid INT PRIMARY KEY AUTO_INCREMENT NOT NULL
	)AUTO_INCREMENT=10000
```

> 设置自增种子的方法s
>
> ---



会员信息表

```mysql
DELIMITER $$

CREATE
    PROCEDURE `csgl`.`add_hy`(IN nname VARCHAR(50),IN nxb CHAR(2),IN nye DECIMAL(9,2),IN njf INT )
    BEGIN
    INSERT INTO hyxxb(hyxm,hyxb,knye,kyjf) VALUES(nname,nxb,nye,njf);
    END$$
DELIMITER ;
```

```mysql 
DELIMITER $$

CREATE
    PROCEDURE `csgl`.`del_hy`(IN id INT)
    BEGIN
    DELETE FROM hyxxb WHERE hyid=id;
    END$$
DELIMITER ;
```

```mysql
CREATE FUNCTION get_discount(id int,se double) RETURNS double
NO SQL
BEGIN
DECLARE discount double DEFAULT 0;
if id>0 then
SET discount=se*0.8;
else
set discount=se;
end if;
return discount;
end$
//设置会员折扣
```

#####  销售记录存盘的改进

- 最后添加删除语句，让`lsb` `slb` 使用后即时清除
- 更新了@@identity的使用，将select语句替换成set
- 更新了会员表的更新方式，如果支付方式是1的话表示刷会员卡支付，否则只更新积分不更新余额

```mysql 
DELIMITER $$

CREATE
    PROCEDURE `csgl`.`SYJLCP7`(IN syyid1 INT ,IN hyid1 INT ,IN ysje1 NUMERIC(9,2),IN ssje1 NUMERIC(9,2),IN qtje1 NUMERIC(9,2),IN zffs1 INT, IN xsmx1 VARCHAR(8000))
    BEGIN
    DECLARE syid1 INT;
    -- 创建临时表slb来保存查询到的货品id和销售数量信息
    CREATE TEMPORARY TABLE slb(hpid INT ,xssl NUMERIC(9,3));
    -- 1.在收银记录表中插入数据
    INSERT INTO syjlb(syyid, hyid ,ysje,ssje,zffs,qtje) VALUES(syyid1,hyid1,ysje1,ssje1,zffs1,qtje1);
    -- 使用系统定义的全局变量`@@identity`获取最近插入的信息的标识列
    SET syid1=@@identity;
    -- 2.将销售明细的字符串转换为临时表，临时表在函数中创建，创建完后在这个存储过程中直接调用
    -- 并在明细表中插入数据
    SELECT xsmx_to_lsb3(xsmx1);
    INSERT syjlmxb(syid,hpid,xssl,lsj,cxj) SELECT syid1, lsb.hpid, lsb.xssl, producttable.retail_price, producttable.bargin_price FROM lsb,producttable WHERE lsb.hpid=producttable.product_id;
    -- 3.通过分组查询group by将临时表lsb中的数据按照hpid分组，统计每个货品消耗的总数量
    -- 并更新库存信息
    INSERT INTO slb SELECT hpid, SUM(xssl) FROM lsb GROUP BY hpid;
    UPDATE kcxxb,slb SET kcsl=kcsl-slb.xssl  WHERE kcxxb.product_id=slb.hpid; 
    -- 4.判断是否是会员，是会员的话更新会员表
    IF hyid1 > 10000 AND zffs1 = 1 THEN  -- 判断是否是刷会员卡支付，不是的话只更新积分
    UPDATE hyxxb SET knye=knye-ssje1 ,kyjf=FLOOR(ssje1)++kyjf  WHERE hyid=hyid1;
    ELSE IF hyid1 > 10000 THEN
    UPDATE hyxxb SET kyjf=FLOOR(ssje1)+kyjf  WHERE hyid=hyid1;	
    END IF;
    END IF;
    -- 删除创建的临时表
    DROP TEMPORARY TABLE IF EXISTS lsb;
    DROP TEMPORARY TABLE IF EXISTS slb;
    END$$
DELIMITER ;


DELIMITER $$

CREATE
    /*[DEFINER = { user | CURRENT_USER }]*/
    FUNCTION `csgl`.`xsmx_to_lsb2`(str VARCHAR(8000)) RETURNS INT
    BEGIN
    DECLARE k INT;
    DECLARE hpid INT;
    DECLARE xssl NUMERIC(9,3);
    DECLARE charindex INT DEFAULT 1;
    -- 如果临时表lsb存在就执行删除，不存在就创建
    CREATE TEMPORARY TABLE lsb(hpid INT ,xssl NUMERIC(9,3));
    -- 循环往临时表中插入数据
    WHILE charindex>0 DO
    SET k = (SELECT INSTR(str,','))-1;
    SET hpid = (SELECT LEFT(str,k));
    SET str = (SELECT SUBSTRING(str,k+2));
    SET k = (SELECT INSTR(str,','))-1;
    IF k>0 THEN
    SET xssl = (SELECT LEFT(str,k));
    SET str = (SELECT SUBSTRING(str,k+2));
    ELSE
    SET xssl = str;  
    END IF;  
    INSERT INTO lsb VALUES(hpid,xssl);
    SET charindex = k;
    END WHILE;
    RETURN 0;
    END$$
DELIMITER ;
```

```mysql
DELIMITER $$

CREATE
    /*[DEFINER = { user | CURRENT_USER }]*/
    FUNCTION `csgl`.`xsmx_to_lsb3`(str VARCHAR(8000)) RETURNS INT
    BEGIN
    DECLARE k INT DEFAULT 1;
    DECLARE hpid INT;
    DECLARE xssl NUMERIC(9,3);
    -- 如果临时表lsb存在就执行删除，不存在就创建
    CREATE TEMPORARY TABLE lsb(hpid INT ,xssl NUMERIC(9,3));
    -- 以下内容发生改动，使语法更规范
    WHILE k>0 DO
    SET k = INSTR(str,',')-1;
    SET hpid = LEFT(str,k);
    SET str = SUBSTRING(str,k+2);
    SET k = INSTR(str,',')-1;
    IF k>0 THEN
    SET xssl = LEFT(str,k);
    SET str = SUBSTRING(str,k+2);
    ELSE
    SET xssl = str;  
    END IF;  
    INSERT INTO lsb VALUES(hpid,xssl);
    END WHILE;
    RETURN 0;
    END$$
DELIMITER ;
```

> 循环直接用k判断，去掉下标
>
> ---

######  王五买两瓶矿泉水，两瓶老干妈

```mysql
 call SYJLCP6(1,10001,26,26,0,1,'1,1,2,2,1,1');
```

######  收银记录明细表查询视图

```mysql

CREATE
    VIEW `csgl`.`V_Syjlmxb` 
    AS
	SELECT p1.product_id, p1.product_name, p1.measure_unit, s1.syid,s1.xssl,s1.lsj,s1.cxj  FROM syjlmxb s1 INNER JOIN producttable p1 ON s1.hpid=p1.product_id;

```

> as后面写select语句
>
> ---

  

![屏幕截图(C:\GYKU\学习笔记\图片\屏幕截图(286).png)](C:\GYKU\学习笔记\图片\屏幕截图(286).png)

![屏幕截图(C:\GYKU\学习笔记\图片\屏幕截图(278).png)](C:\GYKU\学习笔记\图片\屏幕截图(278).png)

![屏幕截图(C:\GYKU\学习笔记\图片\屏幕截图(279).png)](C:\GYKU\学习笔记\图片\屏幕截图(279).png)

![屏幕截图(C:\GYKU\学习笔记\图片\屏幕截图(281).png)](C:\GYKU\学习笔记\图片\屏幕截图(281).png)

![屏幕截图(C:\GYKU\学习笔记\图片\屏幕截图(282).png)](C:\GYKU\学习笔记\图片\屏幕截图(282).png)

![屏幕截图(C:\GYKU\学习笔记\图片\屏幕截图(283).png)](C:\GYKU\学习笔记\图片\屏幕截图(283).png)

![屏幕截图(C:\GYKU\学习笔记\图片\屏幕截图(284).png)](C:\GYKU\学习笔记\图片\屏幕截图(284).png)