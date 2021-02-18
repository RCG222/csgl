# csgl
超市进销存系统基于java swing,mysql

其中mysql使用到了存储过程，函数，触发器等

其中数据库脚本在CSGL的一些信息.md中，并有详细注释

MySQL实现了大部分自动化工作

例如
1. 添加一种商品时会自动在库存表中添加库存为0的商品，并依据商品名称自动插入商品拼音缩写
2. 购买时数据库进行应收金额，库存减少等操作


![image](imgs/1.png)
![image](imgs/2.png)
![image](imgs/3.png)
![image](imgs/4.png)
![image](imgs/5.png)
![image](imgs/6.png)
![image](imgs/7.png)
