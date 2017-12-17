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
  CONSTRAINT `FKfqjkmtw2rddr24sv5lcb40t59` FOREIGN KEY (`roles_role_id`) REFERENCES `role_master` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `access`
--

LOCK TABLES `access` WRITE;
/*!40000 ALTER TABLE `access` DISABLE KEYS */;
INSERT INTO `access` VALUES (1,'admin',1),(2,'10643503',2);
/*!40000 ALTER TABLE `access` ENABLE KEYS */;
UNLOCK TABLES;

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
  PRIMARY KEY (`ps_number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES ('admin',NULL,NULL,'admin',NULL,NULL,NULL,NULL,1),('inactive',NULL,NULL,'inactive',NULL,NULL,NULL,NULL,0),('user',NULL,NULL,'user',NULL,NULL,NULL,NULL,1);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entry_date`
--

DROP TABLE IF EXISTS `entry_date`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entry_date` (
  `ps_number` varchar(100) NOT NULL,
  `swipe_date` date NOT NULL,
  `swipe_door` varchar(100) NOT NULL,
  `duration` varchar(255) DEFAULT NULL COMMENT 'sum of Time difference siwpe_out minus swipe_in\nPTxxHxxMxxs\nXXH hours\nXXM Minutes\nXXS Seconds\n',
  `compliance` varchar(255) DEFAULT NULL COMMENT 'sum of (Duration minus 8) hours\nPTxxHxxMxxs\nXXH hours\nXXM Minutes\nXXS Seconds\n',
  `first_in` time DEFAULT NULL,
  `last_out` time DEFAULT NULL,
  `filo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ps_number`,`swipe_date`,`swipe_door`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entry_date`
--

LOCK TABLES `entry_date` WRITE;
/*!40000 ALTER TABLE `entry_date` DISABLE KEYS */;
/*!40000 ALTER TABLE `entry_date` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entry_pair`
--

DROP TABLE IF EXISTS `entry_pair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entry_pair` (
  `ps_number` varchar(100) NOT NULL,
  `swipe_date` date NOT NULL,
  `swipe_in` time NOT NULL,
  `swipe_out` time NOT NULL,
  `swipe_door` varchar(100) NOT NULL,
  `duration` varchar(100) DEFAULT 'PT0S' COMMENT 'Time difference siwpe_out minus swipe_in\nPTxxHxxMxxs\nXXH hours\nXXM Minutes\nXXS Seconds\n',
  PRIMARY KEY (`ps_number`,`swipe_date`,`swipe_in`,`swipe_out`,`swipe_door`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entry_pair`
--

LOCK TABLES `entry_pair` WRITE;
/*!40000 ALTER TABLE `entry_pair` DISABLE KEYS */;
/*!40000 ALTER TABLE `entry_pair` ENABLE KEYS */;
UNLOCK TABLES;

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

--
-- Dumping data for table `role_master`
--

LOCK TABLES `role_master` WRITE;
/*!40000 ALTER TABLE `role_master` DISABLE KEYS */;
INSERT INTO `role_master` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_USER');
/*!40000 ALTER TABLE `role_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-12-17 19:33:37
