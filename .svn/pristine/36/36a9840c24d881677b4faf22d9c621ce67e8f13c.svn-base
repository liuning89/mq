CREATE TABLE `tt_mq_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `clazz` varchar(255) NOT NULL COMMENT '接口类',
  `application` varchar(255) NOT NULL COMMENT '服务隶属的应用',
  `registry` varchar(255) NOT NULL COMMENT '注册中心地址',
  `registryGroup` varchar(255) NOT NULL COMMENT '注册中心分组',
  `topic` varchar(255) NOT NULL COMMENT '消息topic',
  `tag` varchar(255) NOT NULL COMMENT '消息tag',
  `group` varchar(100) NOT NULL COMMENT '远程服务分组',
  `version` varchar(15) DEFAULT NULL COMMENT '远程服务版本',
  `createTime` datetime NOT NULL,
  `updateTime` datetime NOT NULL,
  `remark` varchar(255) DEFAULT NULL COMMENT '消息订阅备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `application` (`application`,`topic`,`tag`,`group`,`version`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8

CREATE TABLE `tt_mq_consume_detail` (
  `id` bigint(20) NOT NULL,
  `message_dbrecord_id` bigint(20) DEFAULT NULL,
  `messageid` varchar(100) DEFAULT NULL COMMENT 'RocketMQ 消息ID',
  `status` int(11) DEFAULT NULL COMMENT '1:新收到，2：消费成功，3：消费失败',
  `group` varchar(255) DEFAULT NULL COMMENT '远程服务分组',
  `version` varchar(255) DEFAULT NULL COMMENT '远程服务版本',
  `createTime` datetime NOT NULL,
  `updateTime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `consumer` (`messageid`,`group`,`version`),
  KEY `status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tt_mq_messagedb_record` (
  `id` bigint(20) NOT NULL,
  `msg_id` varchar(100) DEFAULT NULL COMMENT 'RocketMQ消息标识',
  `consume_count` int(11) NOT NULL DEFAULT '0' COMMENT '消费次数',
  `status` int(11) DEFAULT NULL COMMENT '1：未发送，2：已发送，3：已消费',
  `topic` varchar(100) DEFAULT NULL COMMENT '消息topic',
  `tag` varchar(100) DEFAULT NULL COMMENT '消息tag',
  `key` varchar(100) DEFAULT NULL COMMENT '消息key',
  `body` varchar(10240) DEFAULT NULL COMMENT '消息体',
  `retrySendCount` int(11) DEFAULT '0' COMMENT '重新发送次数',
  `createTime` datetime NOT NULL,
  `updateTime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `msg_id` (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO ts_sequence(`tablename`,`current_value`,`increment`,`cache`,`version`)
VALUES('tt_mq_config',10000000,1,NULL,NULL),
('tt_mq_consume_detail',10000000,1,NULL,NULL),
('TT_MQ_MESSAGEDB_RECORD',10000000,1,NULL,NULL)


ALTER TABLE `ltn_db`.`tt_mq_consume_detail`   
  ADD COLUMN `callCount` INT DEFAULT 0  NULL  COMMENT '调用远程消费者的次数' AFTER `version`;
