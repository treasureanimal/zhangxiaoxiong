/*
Navicat MySQL Data Transfer

Source Server : wsk
Source Server Version : 50640
Source Host : localhost:3306
Source Database : c2c

Target Server Type : MYSQL
Target Server Version : 50640
File Encoding : 65001

Date: 2018-10-17 09:41:51
*/
DROP DATABASE IF EXISTS `c2c`;
CREATE DATABASE `c2c`;

USE `c2c`;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for admininformation
-- ----------------------------
DROP TABLE IF EXISTS `admininformation`;
CREATE TABLE `admininformation` (
`id` int(11) NOT NULL,
`ano` char(12) NOT NULL,
`password` char(24) NOT NULL,
`modified` datetime DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of admininformation
-- ----------------------------

-- ----------------------------
-- Table structure for adminoperation
-- ----------------------------
DROP TABLE IF EXISTS `adminoperation`;
CREATE TABLE `adminoperation` (
`id` int(11) NOT NULL,
`aid` int(11) NOT NULL,
`modified` datetime DEFAULT NULL,
`operation` varchar(255) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of adminoperation
-- ----------------------------

-- ----------------------------
-- Table structure for allkinds
-- ----------------------------
DROP TABLE IF EXISTS `allkinds`;
CREATE TABLE `allkinds` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(50) NOT NULL,
`modified` datetime DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of allkinds
-- ----------------------------
INSERT INTO `allkinds` VALUES ('1', '空调器具', '2019-05-14 13:28:20');
INSERT INTO `allkinds` VALUES ('2', '制冷器具', '2019-05-14 13:28:23');
INSERT INTO `allkinds` VALUES ('3', '清洁器具', '2019-05-14 13:28:26');
INSERT INTO `allkinds` VALUES ('4', '取暖器具', '2019-05-14 13:28:28');
INSERT INTO `allkinds` VALUES ('5', '家用电子器具', '2019-05-14 13:28:31');
INSERT INTO `allkinds` VALUES ('6', '其他', '2019-05-14 13:28:41');

-- ----------------------------
-- Table structure for boughtshop
-- ----------------------------
DROP TABLE IF EXISTS `boughtshop`;
CREATE TABLE `boughtshop` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`state` int(11) NOT NULL,
`uid` int(11) NOT NULL,
`sid` int(11) NOT NULL,
`quantity` int(11) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of boughtshop
-- ----------------------------

-- ----------------------------
-- Table structure for classification
-- ----------------------------
DROP TABLE IF EXISTS `classification`;
CREATE TABLE `classification` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(50) NOT NULL,
`modified` datetime DEFAULT NULL,
`aid` int(11) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of classification
-- ----------------------------
INSERT INTO `classification` VALUES ('1', '电风扇', null, '1');
INSERT INTO `classification` VALUES ('2', '空调', null, '1');
INSERT INTO `classification` VALUES ('3', '空气净化器', null, '1');
INSERT INTO `classification` VALUES ('4', '电冰箱', null, '2');
INSERT INTO `classification` VALUES ('5', '制冷机', null, '2');
INSERT INTO `classification` VALUES ('6', '洗衣机', null, '3');
INSERT INTO `classification` VALUES ('7', '吸尘器', null, '3');

INSERT INTO `classification` VALUES ('18', '电暖气', null, '4');
INSERT INTO `classification` VALUES ('9', '电热毯', null, '4');

INSERT INTO `classification` VALUES ('10', '电脑', null, '5');
INSERT INTO `classification` VALUES ('11', '电视', null, '5');
INSERT INTO `classification` VALUES ('12', '手机', null, '5');
INSERT INTO `classification` VALUES ('13', '其他', null, '6');
-- ----------------------------
-- Table structure for goodscar
-- ----------------------------
DROP TABLE IF EXISTS `goodscar`;
CREATE TABLE `goodscar` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`sid` int(11) NOT NULL,
`uid` int(11) NOT NULL,
`quantity` int(11) NOT NULL,
`display` int(11) NOT NULL DEFAULT '1',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of goodscar
-- ----------------------------

-- ----------------------------
-- Table structure for goodsoforderform
-- ----------------------------
DROP TABLE IF EXISTS `goodsoforderform`;
CREATE TABLE `goodsoforderform` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`ofid` int(11) NOT NULL,
`sid` int(11) NOT NULL,
`modified` datetime DEFAULT NULL,
`quantity` int(11) NOT NULL,
`display` int(11) NOT NULL DEFAULT '1',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of goodsoforderform
-- ----------------------------

-- ----------------------------
-- Table structure for orderform
-- ----------------------------
DROP TABLE IF EXISTS `orderform`;
CREATE TABLE `orderform` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`display` int(11) NOT NULL DEFAULT '1',
`uid` int(11) NOT NULL,
`context` varchar(255) DEFAULT NULL,
`price` decimal(11,2) not null,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of orderform
-- ----------------------------

-- ----------------------------
-- Table structure for shopcar
-- ----------------------------
DROP TABLE IF EXISTS `shopcar`;
CREATE TABLE `shopcar` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`display` int(11) NOT NULL DEFAULT '1',
`uid` int(11) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shopcar
-- ----------------------------

-- ----------------------------
-- Table structure for shopcontext
-- ----------------------------
DROP TABLE IF EXISTS `shopcontext`;
CREATE TABLE `shopcontext` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`sid` int(11) NOT NULL,
`context` varchar(255) NOT NULL,
`display` int(11) NOT NULL DEFAULT '1',
`uid` int(11) NOT NULL DEFAULT '1',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shopcontext
-- ----------------------------

-- ----------------------------
-- Table structure for shopinformation
-- ----------------------------
DROP TABLE IF EXISTS `shopinformation`;
CREATE TABLE `shopinformation` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`name` varchar(50) NOT NULL,
`level` int(11) NOT NULL,
`remark` varchar(255) NOT NULL,
`price` decimal(10,2) NOT NULL,
`sort` int(11) NOT NULL,
`display` int(11) NOT NULL DEFAULT '1',
`quantity` int(11) NOT NULL,
`transaction` int(11) NOT NULL DEFAULT '1',
`sales` int(11) DEFAULT '0',
`uid` int(11) NOT NULL,
`image` varchar(255) DEFAULT NULL,
`thumbnails` varchar(255) DEFAULT NULL,
`status` char(2) DEFAULT '0',
PRIMARY KEY (`id`),
KEY `index_uid` (`uid`) -- USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1098 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shopinformation
-- ----------------------------

-- ----------------------------
-- Table structure for shoppicture
-- ----------------------------
DROP TABLE IF EXISTS `shoppicture`;
CREATE TABLE `shoppicture` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`sid` int(11) NOT NULL,
`picture` varchar(200) NOT NULL,
`display` int(11) NOT NULL DEFAULT '1',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shoppicture
-- ----------------------------


-- ----------------------------
-- Table structure for specifickinds
-- ----------------------------
DROP TABLE IF EXISTS `specifickinds`;
CREATE TABLE `specifickinds` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(50) NOT NULL,
`modified` datetime DEFAULT NULL,
`cid` int(11) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of specifickinds
-- ----------------------------
INSERT INTO `specifickinds` VALUES ('1', '美的', null, '1');
INSERT INTO `specifickinds` VALUES ('2', '格力', null, '1');
INSERT INTO `specifickinds` VALUES ('3', '海尔', null, '1');
INSERT INTO `specifickinds` VALUES ('4', '奥克斯', null, '1');
INSERT INTO `specifickinds` VALUES ('5', '松下', null, '1');
INSERT INTO `specifickinds` VALUES ('6', '康佳', null, '1');
INSERT INTO `specifickinds` VALUES ('7', '小米', null, '1');
INSERT INTO `specifickinds` VALUES ('8', '志高', null, '1');
INSERT INTO `specifickinds` VALUES ('9', '其他', null, '1');
INSERT INTO `specifickinds` VALUES ('10', '华为', null, '1');
INSERT INTO `specifickinds` VALUES ('11', '苹果', null, '1');
INSERT INTO `specifickinds` VALUES ('12', '三星', null, '1');
--



-- ----------------------------
-- Table structure for usercollection
-- ----------------------------
DROP TABLE IF EXISTS `usercollection`;
CREATE TABLE `usercollection` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`uid` int(11) NOT NULL,
`sid` int(11) NOT NULL,
`display` int(11) NOT NULL DEFAULT '1',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of usercollection
-- ----------------------------

-- ----------------------------
-- Table structure for userinformation
-- ----------------------------
DROP TABLE IF EXISTS `userinformation`;
CREATE TABLE `userinformation` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`username` varchar(50) NOT NULL,
`phone` char(11) NOT NULL,
`realname` varchar(50) DEFAULT NULL,
`clazz` varchar(50) DEFAULT NULL,
`sno` char(12) DEFAULT NULL,
`dormitory` varchar(50) DEFAULT NULL,
`gender` char(2) DEFAULT NULL,
`createtime` datetime DEFAULT NULL,
`avatar` varchar(200) DEFAULT NULL,
`role` char(2) DEFAULT '0',
PRIMARY KEY (`id`),
UNIQUE KEY `index_id` (`id`), -- USING BTREE,
KEY `selectByPhone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;


insert into userinformation(id,modified,username,phone,realname,clazz,sno,dormitory,gender, createtime, role) 
values(10000,now(),'admin_1', '17867677989', '李雷', '1', '232', '444', '男', now(), '1');

insert into userpassword(id,modified,`password`,uid) values(10000,now(),'4QrcOUm6Wau+VuBX8g+IPg==', 10000);

-- ----------------------------
-- Records of userinformation
-- ----------------------------

-- ----------------------------
-- Table structure for userpassword
-- ----------------------------
DROP TABLE IF EXISTS `userpassword`;
CREATE TABLE `userpassword` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`password` varchar(24) NOT NULL,
`uid` int(11) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of userpassword
-- ----------------------------

-- ----------------------------
-- Table structure for userrelease
-- ----------------------------
DROP TABLE IF EXISTS `userrelease`;
CREATE TABLE `userrelease` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`display` int(11) NOT NULL DEFAULT '1',
`uid` int(11) NOT NULL,
`sid` int(11) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1006 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of userrelease
-- ----------------------------


-- ----------------------------
-- Table structure for userstate
-- ----------------------------
DROP TABLE IF EXISTS `userstate`;
CREATE TABLE `userstate` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`credit` int(11) NOT NULL DEFAULT '80',
`balance` decimal(10,2) NOT NULL DEFAULT '0.00',
`modified` datetime DEFAULT NULL,
`uid` int(11) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of userstate
-- ----------------------------

-- ----------------------------
-- Table structure for userwant
-- ----------------------------
DROP TABLE IF EXISTS `userwant`;
CREATE TABLE `userwant` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`display` int(11) NOT NULL DEFAULT '1',
`name` varchar(50) NOT NULL,
`sort` int(100) NOT NULL,
`quantity` int(11) NOT NULL,
`price` decimal(10,2) NOT NULL,
`remark` varchar(255) DEFAULT NULL,
`uid` int(11) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of userwant
-- ----------------------------

-- ----------------------------
-- Table structure for wantcontext
-- ----------------------------
DROP TABLE IF EXISTS `wantcontext`;
CREATE TABLE `wantcontext` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`modified` datetime DEFAULT NULL,
`uwid` int(11) NOT NULL,
`context` varchar(255) NOT NULL,
`display` int(11) NOT NULL DEFAULT '1',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of wantcontext
-- ----------------------------