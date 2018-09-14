/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : filebox

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-07-12 15:44:45
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for file_box_owner
-- ----------------------------
DROP TABLE IF EXISTS `file_box_owner`;
CREATE TABLE `file_box_owner` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '信报箱用户ID',
  `phone` varchar(13) NOT NULL COMMENT '手机号码',
  `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态 1-正常 0-禁用',
  `sysupdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `password` varchar(50) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of file_box_owner
-- ----------------------------
INSERT INTO `file_box_owner` VALUES ('2', '13800138000', null, '2017-05-04 23:17:51', '1', '2017-06-12 16:18:30', 'e10adc3949ba59abbe56e057f20f883e');
INSERT INTO `file_box_owner` VALUES ('4', '13455552225', '/upload/avatar/1499843733744.jpg', null, '1', '2017-07-12 15:15:33', 'd93fb6a810c1d39053505bcee5d98c9c');
INSERT INTO `file_box_owner` VALUES ('5', '1345555225', null, null, '1', '2017-05-06 11:29:45', 'c6632b4fb0a16e34d4d188a6b980e3f7');
INSERT INTO `file_box_owner` VALUES ('9', '13750002530', '/upload/avatar/1499843698489.jpg', null, '1', '2017-07-12 15:14:58', 'e10adc3949ba59abbe56e057f20f883e');
INSERT INTO `file_box_owner` VALUES ('10', '13112395231', null, null, '1', '2017-06-19 09:42:18', 'ebbbd5eb5e81cf6240c1a8c235bd308d');
INSERT INTO `file_box_owner` VALUES ('11', '13750002531', null, null, '1', '2017-06-12 14:36:21', '64b90f34278be061bf5523d485ccc82a');
INSERT INTO `file_box_owner` VALUES ('12', '13750002511', null, null, '1', '2017-06-12 14:40:50', '168e0186859bd365dae727b756bafc85');
INSERT INTO `file_box_owner` VALUES ('16', '13631274616', null, null, '1', '2017-06-17 14:48:42', '75bd44a4f4967a0056ae77ad9367ae72');
INSERT INTO `file_box_owner` VALUES ('18', '13631276924', null, null, '1', '2017-06-17 14:23:48', '308ec4e8603a6cb5065bf4a684b79741');
INSERT INTO `file_box_owner` VALUES ('19', 'fsdf', null, null, '1', '2017-06-20 23:17:46', 'a7ae6610ef707c24402c6b7430308611');
INSERT INTO `file_box_owner` VALUES ('23', '13750052222', null, null, '1', '2017-06-21 00:29:52', '597aa6a7a585feeb528eb9041d52c7d0');
INSERT INTO `file_box_owner` VALUES ('24', '137500544228', null, null, '1', '2017-06-21 00:29:52', '24c8c18a87b135d5080b899bd89766a4');
INSERT INTO `file_box_owner` VALUES ('25', '13950522282', null, null, '1', '2017-06-21 00:29:52', 'b8f1e360ddadb0c6d21a61e341d920b0');
INSERT INTO `file_box_owner` VALUES ('26', '12345678911', null, null, '1', '2017-07-05 10:46:40', 'e10adc3949ba59abbe56e057f20f883e');
INSERT INTO `file_box_owner` VALUES ('33', '12345678911', null, null, '1', '2017-07-05 11:08:13', 'e10adc3949ba59abbe56e057f20f883e');
INSERT INTO `file_box_owner` VALUES ('34', '12345678911', null, null, '1', '2017-07-05 11:09:11', 'e10adc3949ba59abbe56e057f20f883e');
INSERT INTO `file_box_owner` VALUES ('35', '12345678911', null, null, '1', '2017-07-05 11:34:32', 'e10adc3949ba59abbe56e057f20f883e');
INSERT INTO `file_box_owner` VALUES ('36', '12345678911', null, null, '1', '2017-07-05 11:37:46', 'e10adc3949ba59abbe56e057f20f883e');
INSERT INTO `file_box_owner` VALUES ('37', '11111111111', null, null, '1', '2017-07-06 11:29:45', 'e10adc3949ba59abbe56e057f20f883e');
INSERT INTO `file_box_owner` VALUES ('38', '11111111112', null, null, '1', '2017-07-06 11:30:17', 'e10adc3949ba59abbe56e057f20f883e');
