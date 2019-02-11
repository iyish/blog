#部署文档

##本地部署
1. 创建数据库springadmin，数据库编码为UTF-8
2. 执行sql/db_init.sql和sql/quartz_mysql_innodb.sql文件，分别初始化表和定时任务表数据
3. 修改conf/db.properties文件，更新为自己的MySQL账号和密码
4. 进入项目所在目录通过命令行执行【mvn tomcat7:run】命令，即可运行项目；也可使用MyEclipse或Idea配置tomcat Server, 选择8080端口，点击Debug或Run按钮即可运行项目
5. 默认登录用户名/密码=admin/admin

##Redis共享部署
1. 安装redis，并根据redis安装信息配置db.properties的redis信息
2. 配置【shiro.cache.type=1】，表示开启redis缓存并把shiro session存到redis，默认保存到Echache
