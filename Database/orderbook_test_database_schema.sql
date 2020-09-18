-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema orderbooktest
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema orderbooktest
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `orderbooktest` DEFAULT CHARACTER SET utf8 ;
USE `orderbooktest` ;

-- -----------------------------------------------------
-- Table `orderbooktest`.`order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `orderbooktest`.`order` (
  `orderId` INT NOT NULL AUTO_INCREMENT,
  `size` INT NOT NULL COMMENT 'Number of stocks for order',
  `side` TINYINT NOT NULL COMMENT 'Whether order is a buy or sell',
  `time` DATETIME NOT NULL COMMENT 'Time of order placement',
  `active` TINYINT NOT NULL DEFAULT 1 COMMENT 'Whether order is active',
  `offerPrice` DECIMAL(8,2) NOT NULL COMMENT 'Price for oder',
  `symbol` VARCHAR(45) NOT NULL COMMENT 'Symbol of stock in order',
  PRIMARY KEY (`orderId`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `orderbooktest`.`transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `orderbooktest`.`transaction` (
  `transactionId` INT NOT NULL AUTO_INCREMENT,
  `buyOrderId` INT NOT NULL COMMENT 'Id for buy order',
  `sellOrderId` INT NOT NULL COMMENT 'Id for sell order',
  `finalTime` DATETIME NOT NULL COMMENT 'Time transaction occurs',
  `finalPrice` DECIMAL(8,2) NOT NULL COMMENT 'final price of transaction',
  `amount` INT NOT NULL COMMENT 'amount of stocks traded during transaction',
  `finalSymbol` VARCHAR(45) NOT NULL COMMENT 'symbol of stock being traded during transaction',
  INDEX `fk_order_has_order_order1_idx` (`sellOrderId` ASC) VISIBLE,
  INDEX `fk_order_has_order_order_idx` (`buyOrderId` ASC) VISIBLE,
  PRIMARY KEY (`transactionId`),
  CONSTRAINT `fk_order_has_order_order`
    FOREIGN KEY (`buyOrderId`)
    REFERENCES `orderbooktest`.`order` (`orderId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_order_has_order_order1`
    FOREIGN KEY (`sellOrderId`)
    REFERENCES `orderbooktest`.`order` (`orderId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
