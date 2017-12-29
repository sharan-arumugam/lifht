-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: lifht
-- ------------------------------------------------------
-- Server version	5.7.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `access`
--

DROP TABLE IF EXISTS `access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `access` (
  `access_id` int(11) NOT NULL AUTO_INCREMENT,
  `ps_number` varchar(100) NOT NULL,
  `roles_role_id` int(11) NOT NULL,
  PRIMARY KEY (`access_id`),
  UNIQUE KEY `UK_qpdqedj2oa0kao6nmc8nc48xp` (`roles_role_id`),
  KEY `fk_role_idx` (`roles_role_id`),
  KEY `FKgundor8pb7fd8enhf9qdfkpa2` (`ps_number`),
  CONSTRAINT `FKfqjkmtw2rddr24sv5lcb40t59` FOREIGN KEY (`roles_role_id`) REFERENCES `role_master` (`role_id`),
  CONSTRAINT `FKgundor8pb7fd8enhf9qdfkpa2` FOREIGN KEY (`ps_number`) REFERENCES `employee` (`ps_number`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `employee` (
  `ps_number` varchar(100) NOT NULL,
  `ps_name` varchar(100) DEFAULT NULL,
  `ds_id` varchar(45) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `apple_mail` varchar(100) DEFAULT NULL,
  `lti_mail` varchar(100) DEFAULT NULL,
  `apple_manager` varchar(100) DEFAULT NULL,
  `business_unit` varchar(100) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `reset_token` varchar(100) DEFAULT NULL,
  `employee_ps_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ps_number`),
  KEY `FKeyt5n6bcxifri0ey2q4ryry3q` (`employee_ps_number`),
  CONSTRAINT `FKeyt5n6bcxifri0ey2q4ryry3q` FOREIGN KEY (`employee_ps_number`) REFERENCES `employee` (`ps_number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `entry_date`
--

DROP TABLE IF EXISTS `entry_date`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entry_date` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ps_number` varchar(100) NOT NULL,
  `swipe_date` date NOT NULL,
  `swipe_door` varchar(100) NOT NULL,
  `duration` varchar(255) NOT NULL COMMENT 'sum of Time difference siwpe_out minus swipe_in\nPTxxHxxMxxs\nXXH hours\nXXM Minutes\nXXS Seconds\n',
  `compliance` varchar(255) NOT NULL COMMENT 'sum of (Duration minus 8) hours\nPTxxHxxMxxs\nXXH hours\nXXM Minutes\nXXS Seconds\n',
  `first_in` time NOT NULL,
  `last_out` time NOT NULL,
  `filo` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmi936ad0m1bww1w99n7coyl8p` (`ps_number`,`swipe_date`,`swipe_door`,`duration`)
) ENGINE=InnoDB AUTO_INCREMENT=516 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `entry_pair`
--

DROP TABLE IF EXISTS `entry_pair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entry_pair` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ps_number` varchar(100) NOT NULL,
  `swipe_date` date NOT NULL,
  `swipe_in` time NOT NULL,
  `swipe_out` time NOT NULL,
  `swipe_door` varchar(100) NOT NULL,
  `duration` varchar(100) NOT NULL DEFAULT 'PT0S' COMMENT 'Time difference siwpe_out minus swipe_in\nPTxxHxxMxxs\nXXH hours\nXXM Minutes\nXXS Seconds\n',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt4d89ddqt8bxa4vv7ahdhonv` (`ps_number`,`swipe_date`,`swipe_in`,`swipe_out`,`swipe_door`)
) ENGINE=InnoDB AUTO_INCREMENT=1562 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_master`
--

DROP TABLE IF EXISTS `role_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_master` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-12-26  3:24:33
