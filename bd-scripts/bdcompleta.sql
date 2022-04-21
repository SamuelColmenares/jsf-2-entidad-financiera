-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         10.4.14-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.0.0.6468
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para basededatos_entidadfinanciera
CREATE DATABASE IF NOT EXISTS `basededatos_entidadfinanciera` /*!40100 DEFAULT CHARACTER SET utf16 */;
USE `basededatos_entidadfinanciera`;

-- Volcando estructura para tabla basededatos_entidadfinanciera.clientes
CREATE TABLE IF NOT EXISTS `clientes` (
  `Num_identificacion` int(12) NOT NULL,
  `Apellidos` varchar(50) NOT NULL,
  `Nombres` varchar(50) NOT NULL,
  `Direccion` varchar(50) NOT NULL DEFAULT '',
  `Telefono` int(11) NOT NULL,
  PRIMARY KEY (`Num_identificacion`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COMMENT='Esta es la tabla para los clientes de entidad financiera';

-- Volcando datos para la tabla basededatos_entidadfinanciera.clientes: ~5 rows (aproximadamente)
DELETE FROM `clientes`;
INSERT INTO `clientes` (`Num_identificacion`, `Apellidos`, `Nombres`, `Direccion`, `Telefono`) VALUES
	(19657428, 'alvarez ramirez', 'jesus alfonso', 'diagonal 2 51-41', 321411254),
	(41687253, 'gonzalez hernandez', 'alirio', 'carrera 9 96-90', 313478619),
	(55555555, 'dos', 'prueba', 'retyuiuytre', 312444444),
	(1022658941, 'riaño bermudez', 'fabian arnoldo', 'transversal 8 95-63', 323657418),
	(1025687231, ' perdomo alzate', 'miguel alejandro', 'carrera 50 # 52-14', 325874145),
	(1035478951, 'rojas suarez', 'laura carolina', 'tranversal 4 63-85', 320147961);

-- Volcando estructura para tabla basededatos_entidadfinanciera.datacredito
CREATE TABLE IF NOT EXISTS `datacredito` (
  `Documento` bigint(20) NOT NULL,
  `Calificacion` varchar(1) NOT NULL DEFAULT '',
  `TiempoMora` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`Documento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16;

-- Volcando datos para la tabla basededatos_entidadfinanciera.datacredito: ~0 rows (aproximadamente)
DELETE FROM `datacredito`;
INSERT INTO `datacredito` (`Documento`, `Calificacion`, `TiempoMora`) VALUES
	(41687253, 'A', 0),
	(1022658941, 'B', 8);

-- Volcando estructura para tabla basededatos_entidadfinanciera.listanegra
CREATE TABLE IF NOT EXISTS `listanegra` (
  `IdProducto` int(11) NOT NULL AUTO_INCREMENT,
  `PagosVencidos` int(11) unsigned NOT NULL,
  `DiasMora` int(11) unsigned NOT NULL,
  `Num_identificacion` int(11) NOT NULL,
  PRIMARY KEY (`IdProducto`) USING BTREE,
  KEY `FK_listanegra_cliente` (`Num_identificacion`),
  CONSTRAINT `FK_listanegra_cliente` FOREIGN KEY (`Num_identificacion`) REFERENCES `clientes` (`Num_identificacion`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf16;

-- Volcando datos para la tabla basededatos_entidadfinanciera.listanegra: ~0 rows (aproximadamente)
DELETE FROM `listanegra`;
INSERT INTO `listanegra` (`IdProducto`, `PagosVencidos`, `DiasMora`, `Num_identificacion`) VALUES
	(1, 1, 45, 19657428),
	(2, 0, 0, 41687253);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
