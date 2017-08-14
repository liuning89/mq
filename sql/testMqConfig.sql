insert into `tt_mq_config` (`id`, `clazz`, `application`, `registry`, `registryGroup`, `topic`, `tag`, `group`, `version`, `createTime`, `updateTime`, `remark`) values('1','com.wy.mq.consumer.IMqConsumerService','mq-example','zookeeper://192.168.18.130:2181','mq-service','USER','NEW','user','new','2016-10-31 18:20:04','2016-10-31 18:20:07','新用户事件测试');
insert into `tt_mq_config` (`id`, `clazz`, `application`, `registry`, `registryGroup`, `topic`, `tag`, `group`, `version`, `createTime`, `updateTime`, `remark`) values('2','com.wy.mq.consumer.IMqConsumerService','mq-example','zookeeper://192.168.18.130:2181','mq-service','USER','LOGIN','user','login','2016-10-31 18:20:49','2016-10-31 18:20:52','用户登录事件测试');
insert into `tt_mq_config` (`id`, `clazz`, `application`, `registry`, `registryGroup`, `topic`, `tag`, `group`, `version`, `createTime`, `updateTime`, `remark`) values('3','com.wy.mq.consumer.IMqConsumerService','mq-example','zookeeper://192.168.18.130:2181','mq-service','USER','NEW','user','shortMsg','2016-10-31 18:20:49','2016-10-31 18:20:52','用户登录事件测试');


INSERT INTO `tt_mq_config` (`clazz`, `application`, `registry`, `registryGroup`, `topic`, `tag`, `group`, `version`, `createTime`, `updateTime`, `remark`) VALUES('com.wy.mq.consumer.IMqConsumerService','mq-service','zookeeper://localhost:2181','mq-service','USER','NEW','default','default','2016-10-31 18:20:49','2016-10-31 18:20:52','无消息者时的默认配置');