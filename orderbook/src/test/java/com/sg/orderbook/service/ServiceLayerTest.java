/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.orderbook.service;

import com.sg.orderbook.entities.Order;
import com.sg.orderbook.entities.Transaction;
import com.sg.orderbook.repositories.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.Rollback;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author R Lara
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class ServiceLayerTest {

    @TestConfiguration
    static class ServiceLayerTestContextConfiguration {

        @Bean
        public ServiceLayer serviceLayer() {
            return new ServiceLayerImpl();
        }
    }

    @Autowired
    private TransactionRepository transactions;

    @Autowired
    private OrderRepository orders;

    @Autowired
    private ServiceLayerImpl service;

    @Test
    public void testGetAllOrders() {
        Order order = new Order();

        order.setActive(true);
        order.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        order.setSide(true);
        order.setSize(10);
        order.setSymbol("APPL");
        order.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        order = orders.save(order);

        Order gotOrder = orders.findById(order.getId()).orElse(null);

        assertEquals(order, gotOrder);
    }

    @Test
    public void testGetAllActiveOrders() {
        Order firstOrder = new Order();

        firstOrder.setActive(true);
        firstOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        firstOrder.setSide(true);
        firstOrder.setSize(10);
        firstOrder.setSymbol("APPL");
        firstOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        firstOrder = orders.save(firstOrder);

        Order secondOrder = new Order();

        secondOrder.setActive(false);
        secondOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        secondOrder.setSide(true);
        secondOrder.setSize(0);
        secondOrder.setSymbol("APPL");
        secondOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        secondOrder = orders.save(secondOrder);

        Order gotFirstOrder = orders.findById(firstOrder.getId()).orElse(null);
        Order gotSecondOrder = orders.findById(secondOrder.getId()).orElse(null);
        List<Order> gotActiveOrders = orders.findAllActiveOrders();

        assertEquals(firstOrder, gotFirstOrder);
        assertEquals(secondOrder, gotSecondOrder);
        assertEquals(1, gotActiveOrders.size());
    }

    @Test
    public void testGetAllBuyOrders() {
        // Create Buy Order
        Order buyOrder = new Order();

        buyOrder.setActive(true);
        buyOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        buyOrder.setSide(true);
        buyOrder.setSize(10);
        buyOrder.setSymbol("APPL");
        buyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        buyOrder = orders.save(buyOrder);

        // Create Sell Order
        Order sellOrder = new Order();

        sellOrder.setActive(false);
        sellOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        sellOrder.setSide(true);
        sellOrder.setSize(0);
        sellOrder.setSymbol("APPL");
        sellOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        sellOrder = orders.save(sellOrder);

        // Retrieve orders, list of buy orders
        Order gotBuyOrder = orders.findById(buyOrder.getId()).orElse(null);
        Order gotSellOrder = orders.findById(sellOrder.getId()).orElse(null);
        List<Order> gotBuyOrders = service.getAllBuyOrdersForSymbol(buyOrder.getSymbol());

        // Assert
        assertEquals(buyOrder, gotBuyOrder);
        assertEquals(sellOrder, gotSellOrder);
        assertEquals(1, gotBuyOrders.size());
        assertEquals(gotBuyOrders.get(0), gotBuyOrder);
        assertEquals(gotBuyOrders.get(0).isSide(), true);
    }

    @Test
    public void testGetAllSellOrders() {
        // Create Buy Order
        Order buyOrder = new Order();

        buyOrder.setActive(true);
        buyOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        buyOrder.setSide(true);
        buyOrder.setSize(10);
        buyOrder.setSymbol("APPL");
        buyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        buyOrder = orders.save(buyOrder);

        // Create Sell Order
        Order sellOrder = new Order();

        sellOrder.setActive(false);
        sellOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        sellOrder.setSide(false);
        sellOrder.setSize(10);
        sellOrder.setSymbol("APPL");
        sellOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        sellOrder = orders.save(sellOrder);

        // Retrieve orders, list of sell orders
        Order gotBuyOrder = orders.findById(buyOrder.getId()).orElse(null);
        Order gotSellOrder = orders.findById(sellOrder.getId()).orElse(null);
        List<Order> gotSellOrders = service.getAllSellOrdersForSymbol(sellOrder.getSymbol());

        // Assert
        assertEquals(buyOrder, gotBuyOrder);
        assertEquals(sellOrder, gotSellOrder);
        assertEquals(1, gotSellOrders.size());
        assertEquals(gotSellOrders.get(0), sellOrder);
        assertEquals(gotSellOrders.get(0).isSide(), false);
    }

    @Test
    public void testGetAllTransactions() {
        // Create Buy Order
        Order buyOrder = new Order();

        buyOrder.setActive(true);
        buyOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        buyOrder.setSide(true);
        buyOrder.setSize(10);
        buyOrder.setSymbol("APPL");
        buyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        buyOrder = orders.save(buyOrder);

        // Create Sell Order
        Order sellOrder = new Order();

        sellOrder.setActive(false);
        sellOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        sellOrder.setSide(true);
        sellOrder.setSize(0);
        sellOrder.setSymbol("APPL");
        sellOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        sellOrder = orders.save(sellOrder);

        // Retrieve orders, list of sell orders
        Order gotBuyOrder = orders.findById(buyOrder.getId()).orElse(null);
        Order gotSellOrder = orders.findById(sellOrder.getId()).orElse(null);

        boolean buySizeBigger = gotBuyOrder.getSize() > gotSellOrder.getSize();

        Transaction transaction = new Transaction();
        transaction.setBuyOrder(buyOrder);
        transaction.setSellOrder(sellOrder);
        transaction.setFinalPrice(gotBuyOrder.getOfferPrice());
        transaction.setFinalSymbol(gotBuyOrder.getSymbol());
        transaction.setAmount(buySizeBigger ? gotBuyOrder.getSize() - gotSellOrder.getSize()
                : gotSellOrder.getSize() - gotBuyOrder.getSize());
        transaction.setFinalTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        gotBuyOrder.setSize(buySizeBigger ? gotBuyOrder.getSize() - transaction.getSellOrder().getSize() : 0);
        gotSellOrder.setSize(buySizeBigger ? 0 : gotSellOrder.getSize() - transaction.getBuyOrder().getSize());

        transaction = transactions.save(transaction);
        gotBuyOrder = orders.save(gotBuyOrder);
        gotSellOrder = orders.save(gotSellOrder);

        List<Transaction> listTransactions = service.getAllTransactions();
        assertNotNull(listTransactions);
        assertEquals(1, listTransactions.size());
        assertEquals(gotBuyOrder, transaction.getBuyOrder());
        assertEquals(gotSellOrder, transaction.getSellOrder());
    }

    @Test
    public void testGetAllTransactionsForSymbol() {
        // Create AAPL Buy Order
        Order buyOrder = new Order();

        buyOrder.setActive(true);
        buyOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        buyOrder.setSide(true);
        buyOrder.setSize(10);
        buyOrder.setSymbol("AAPL");
        buyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        buyOrder = orders.save(buyOrder);

        // Create AAPL Sell Order
        Order sellOrder = new Order();

        sellOrder.setActive(false);
        sellOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        sellOrder.setSide(true);
        sellOrder.setSize(0);
        sellOrder.setSymbol("AAPL");
        sellOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        sellOrder = orders.save(sellOrder);

        // Retrieve orders, list of sell orders
        Order gotBuyOrder = orders.findById(buyOrder.getId()).orElse(null);
        Order gotSellOrder = orders.findById(sellOrder.getId()).orElse(null);

        boolean buySizeBigger = gotBuyOrder.getSize() > gotSellOrder.getSize();

        Transaction transaction = new Transaction();
        transaction.setBuyOrder(buyOrder);
        transaction.setSellOrder(sellOrder);
        transaction.setFinalPrice(gotBuyOrder.getOfferPrice());
        transaction.setFinalSymbol(gotBuyOrder.getSymbol());
        transaction.setAmount(buySizeBigger ? gotBuyOrder.getSize() - gotSellOrder.getSize()
                : gotSellOrder.getSize() - gotBuyOrder.getSize());
        transaction.setFinalTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        gotBuyOrder.setSize(buySizeBigger ? gotBuyOrder.getSize() - transaction.getSellOrder().getSize() : 0);
        gotSellOrder.setSize(buySizeBigger ? 0 : gotSellOrder.getSize() - transaction.getBuyOrder().getSize());

        transaction = transactions.save(transaction);
        gotBuyOrder = orders.save(gotBuyOrder);
        gotSellOrder = orders.save(gotSellOrder);

        List<Transaction> aaplTransactions = service.getAllTransactionsForSymbol(gotBuyOrder.getSymbol());

        assertEquals(1, aaplTransactions.size());
        assertEquals(gotBuyOrder, transaction.getBuyOrder());
        assertEquals(gotSellOrder, transaction.getSellOrder());
    }

    @Test
    public void testDeleteOrder() {
        Order order = new Order();

        order.setActive(true);
        order.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        order.setSide(true);
        order.setSize(10);
        order.setSymbol("APPL");
        order.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        order = orders.save(order);

        Order gotOrder = orders.findById(order.getId()).orElse(null);

        assertNotNull(gotOrder);

        orders.deleteById(gotOrder.getId());

        assertEquals(0, orders.findAll().size());
    }

    @Test
    public void testMakeTransaction() {
        Order firstOrder = new Order();

        firstOrder.setActive(true);
        firstOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        firstOrder.setSide(true);
        firstOrder.setSize(10);
        firstOrder.setSymbol("APPL");
        firstOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        firstOrder = orders.save(firstOrder);

        Order secondOrder = new Order();

        secondOrder.setActive(true);
        secondOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        secondOrder.setSide(!firstOrder.isSide());
        secondOrder.setSize(10);
        secondOrder.setSymbol("APPL");
        secondOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        secondOrder = orders.save(secondOrder);

        Transaction transaction = service.makeTransaction(firstOrder, secondOrder);

        assertEquals(firstOrder, transaction.getBuyOrder());
        assertEquals(secondOrder, transaction.getSellOrder());
        assertEquals(0, transaction.getBuyOrder().getSize());
        assertEquals(0, transaction.getSellOrder().getSize());
    }

    @Test
    public void testMatchOrders() {
        Order firstOrder = new Order();

        firstOrder.setActive(true);
        firstOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        firstOrder.setSide(true);
        firstOrder.setSize(10);
        firstOrder.setSymbol("APPL");
        firstOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        firstOrder = orders.save(firstOrder);

        Transaction transaction = service.matchOrders(firstOrder.getId());
        Order secondOrder = firstOrder.isSide()
                ? transaction.getSellOrder()
                : transaction.getBuyOrder();

        assertNotNull(secondOrder);
        assertEquals(1, transactions.findAll().size());
        assertEquals(firstOrder.getOfferPrice(), secondOrder.getOfferPrice());
        assertEquals(firstOrder.getSymbol(), secondOrder.getSymbol());
        assertNotEquals(firstOrder.isSide(), secondOrder.isSide());
    }

    @Test
    public void testFindPotentialTransactions() {
//        BUY ORDERS
        Order firstBuyOrder = new Order();

        firstBuyOrder.setActive(true);
        firstBuyOrder.setOfferPrice(new BigDecimal("576.52").setScale(2, RoundingMode.HALF_UP));
        firstBuyOrder.setSide(true);
        firstBuyOrder.setSize(10);
        firstBuyOrder.setSymbol("GOOG");
        firstBuyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        firstBuyOrder = orders.save(firstBuyOrder);

        Order secondBuyOrder = new Order();

        secondBuyOrder.setActive(true);
        secondBuyOrder.setOfferPrice(new BigDecimal("576.40").setScale(2, RoundingMode.HALF_UP));
        secondBuyOrder.setSide(true);
        secondBuyOrder.setSize(10);
        secondBuyOrder.setSymbol("GOOG");
        secondBuyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        secondBuyOrder = orders.save(secondBuyOrder);

        Order thirdBuyOrder = new Order();

        thirdBuyOrder.setActive(true);
        thirdBuyOrder.setOfferPrice(new BigDecimal("576.37").setScale(2, RoundingMode.HALF_UP));
        thirdBuyOrder.setSide(true);
        thirdBuyOrder.setSize(10);
        thirdBuyOrder.setSymbol("GOOG");
        thirdBuyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        thirdBuyOrder = orders.save(thirdBuyOrder);

//        SELL ORDERS
        Order firstSellOrder = new Order();

        firstSellOrder.setActive(true);
        firstSellOrder.setOfferPrice(new BigDecimal("576.42").setScale(2, RoundingMode.HALF_UP));
        firstSellOrder.setSide(false);
        firstSellOrder.setSize(10);
        firstSellOrder.setSymbol("GOOG");
        firstSellOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        firstSellOrder = orders.save(firstSellOrder);

        Order secondSellOrder = new Order();

        secondSellOrder.setActive(true);
        secondSellOrder.setOfferPrice(new BigDecimal("576.58").setScale(2, RoundingMode.HALF_UP));
        secondSellOrder.setSide(false);
        secondSellOrder.setSize(10);
        secondSellOrder.setSymbol("GOOG");
        secondSellOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        secondSellOrder = orders.save(secondSellOrder);

        Order thirdSellOrder = new Order();

        thirdSellOrder.setActive(true);
        thirdSellOrder.setOfferPrice(new BigDecimal("576.67").setScale(2, RoundingMode.HALF_UP));
        thirdSellOrder.setSide(false);
        thirdSellOrder.setSize(10);
        thirdSellOrder.setSymbol("GOOG");
        thirdSellOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        thirdSellOrder = orders.save(thirdBuyOrder);

        service.findPotentialTransactions(firstBuyOrder.getSymbol());
        List<Transaction> transactionsForGoog = transactions.findByFinalSymbolOrderByFinalTimeDesc(firstBuyOrder.getSymbol());

        assertEquals(1, transactionsForGoog.size());
        assertEquals(firstBuyOrder, transactionsForGoog.get(0).getBuyOrder());
        assertEquals(firstSellOrder, transactionsForGoog.get(0).getSellOrder());
        assertEquals(0, transactionsForGoog.get(0).getBuyOrder().getSize());
        assertEquals(0, transactionsForGoog.get(0).getSellOrder().getSize());
    }

    @Test
    public void testGetSymbols() {
        Order firstBuyOrder = new Order();

        firstBuyOrder.setActive(true);
        firstBuyOrder.setOfferPrice(new BigDecimal("576.52").setScale(2, RoundingMode.HALF_UP));
        firstBuyOrder.setSide(true);
        firstBuyOrder.setSize(10);
        firstBuyOrder.setSymbol("GOOG");
        firstBuyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        firstBuyOrder = orders.save(firstBuyOrder);

        Order secondBuyOrder = new Order();

        secondBuyOrder.setActive(true);
        secondBuyOrder.setOfferPrice(new BigDecimal("576.40").setScale(2, RoundingMode.HALF_UP));
        secondBuyOrder.setSide(true);
        secondBuyOrder.setSize(10);
        secondBuyOrder.setSymbol("AAPL");
        secondBuyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        secondBuyOrder = orders.save(secondBuyOrder);

        Order thirdBuyOrder = new Order();

        thirdBuyOrder.setActive(true);
        thirdBuyOrder.setOfferPrice(new BigDecimal("576.37").setScale(2, RoundingMode.HALF_UP));
        thirdBuyOrder.setSide(true);
        thirdBuyOrder.setSize(10);
        thirdBuyOrder.setSymbol("MSFT");
        thirdBuyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        thirdBuyOrder = orders.save(thirdBuyOrder);

        List<String> symbols = service.getSymbols();

        assertEquals(3, symbols.size());
    }

    @Test
    public void testCreateOrderbook() {
        List<String> beforeSymbols = service.getSymbols();
        service.createOrderbook("MSFT");
        List<String> afterSymbols = service.getSymbols();

        assertNotEquals(beforeSymbols, afterSymbols);
    }

    @Test
    public void testGetAllTransactionsForDate() {
        Order buyOrder = new Order();

        buyOrder.setActive(true);
        buyOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        buyOrder.setSide(true);
        buyOrder.setSize(30);
        buyOrder.setSymbol("GOOG");
        buyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));
        buyOrder = orders.save(buyOrder);

        Order firstSellOrder = new Order();

        firstSellOrder.setActive(true);
        firstSellOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        firstSellOrder.setSide(false);
        firstSellOrder.setSize(10);
        firstSellOrder.setSymbol("GOOG");
        firstSellOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));
        firstSellOrder = orders.save(firstSellOrder);

        Order secondSellOrder = new Order();

        secondSellOrder.setActive(true);
        secondSellOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        secondSellOrder.setSide(false);
        secondSellOrder.setSize(10);
        secondSellOrder.setSymbol("GOOG");
        secondSellOrder.setTime(LocalDateTime.parse("2020-01-01T13:00:00"));
        secondSellOrder = orders.save(secondSellOrder);

        Order thirdSellOrder = new Order();

        thirdSellOrder.setActive(true);
        thirdSellOrder.setOfferPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        thirdSellOrder.setSide(false);
        thirdSellOrder.setSize(10);
        thirdSellOrder.setSymbol("GOOG");
        thirdSellOrder.setTime(LocalDateTime.parse("2020-01-02T12:00:00"));
        thirdSellOrder = orders.save(thirdSellOrder);

        Transaction firstTransaction = new Transaction();
        firstTransaction.setAmount(10);
        firstTransaction.setBuyOrder(buyOrder);
        firstTransaction.setSellOrder(firstSellOrder);
        firstTransaction.setFinalPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        firstTransaction.setFinalSymbol("GOOG");
        firstTransaction.setFinalTime(LocalDateTime.parse("2020-01-01T12:00:00"));
        firstTransaction = transactions.save(firstTransaction);

        Transaction secondTransaction = new Transaction();
        secondTransaction.setAmount(10);
        secondTransaction.setBuyOrder(buyOrder);
        secondTransaction.setSellOrder(secondSellOrder);
        secondTransaction.setFinalPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        secondTransaction.setFinalSymbol("GOOG");
        secondTransaction.setFinalTime(LocalDateTime.parse("2020-01-01T12:00:00"));
        secondTransaction = transactions.save(secondTransaction);

        Transaction thirdTransaction = new Transaction();
        thirdTransaction.setAmount(10);
        thirdTransaction.setBuyOrder(buyOrder);
        thirdTransaction.setSellOrder(thirdSellOrder);
        thirdTransaction.setFinalPrice(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP));
        thirdTransaction.setFinalSymbol("GOOG");
        thirdTransaction.setFinalTime(LocalDateTime.parse("2020-01-02T12:00:00"));
        thirdTransaction = transactions.save(thirdTransaction);

        List<Transaction> transactionList = service.getAllTransactionsForSymbolAndDate(firstTransaction.getFinalSymbol(), firstTransaction.getFinalTime().toLocalDate());

        assertEquals(2, transactionList.size());
        assertTrue(transactionList.contains(firstTransaction));
        assertTrue(transactionList.contains(secondTransaction));
        assertFalse(transactionList.contains(thirdTransaction));
    }

    @Test
    public void testIncrementBuyOrders() {
//        BUY ORDERS
        Order firstBuyOrder = new Order();

        firstBuyOrder.setActive(true);
        firstBuyOrder.setOfferPrice(new BigDecimal("576.52").setScale(2, RoundingMode.HALF_UP));
        firstBuyOrder.setSide(true);
        firstBuyOrder.setSize(10);
        firstBuyOrder.setSymbol("GOOG");
        firstBuyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        firstBuyOrder = orders.save(firstBuyOrder);

        Order secondBuyOrder = new Order();

        secondBuyOrder.setActive(true);
        secondBuyOrder.setOfferPrice(new BigDecimal("576.40").setScale(2, RoundingMode.HALF_UP));
        secondBuyOrder.setSide(true);
        secondBuyOrder.setSize(10);
        secondBuyOrder.setSymbol("GOOG");
        secondBuyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        secondBuyOrder = orders.save(secondBuyOrder);

        Order thirdBuyOrder = new Order();

        thirdBuyOrder.setActive(true);
        thirdBuyOrder.setOfferPrice(new BigDecimal("576.37").setScale(2, RoundingMode.HALF_UP));
        thirdBuyOrder.setSide(true);
        thirdBuyOrder.setSize(10);
        thirdBuyOrder.setSymbol("GOOG");
        thirdBuyOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        thirdBuyOrder = orders.save(thirdBuyOrder);

        BigDecimal firstOriginalPrice = firstBuyOrder.getOfferPrice();
        BigDecimal secondOriginalPrice = secondBuyOrder.getOfferPrice();
        BigDecimal thirdOriginalPrice = thirdBuyOrder.getOfferPrice();
        
        String tick = "0.01";

        service.incrementBuyOrders(tick, firstBuyOrder.getSymbol());

        assertNotEquals(firstOriginalPrice, firstBuyOrder.getOfferPrice());
        assertEquals(firstOriginalPrice.add(new BigDecimal(tick)), firstBuyOrder.getOfferPrice());
        assertNotEquals(secondOriginalPrice, secondBuyOrder.getOfferPrice());
        assertEquals(secondOriginalPrice.add(new BigDecimal(tick)), secondBuyOrder.getOfferPrice());
        assertNotEquals(thirdOriginalPrice, thirdBuyOrder.getOfferPrice());
        assertEquals(thirdOriginalPrice.add(new BigDecimal(tick)), thirdBuyOrder.getOfferPrice());
    }

    @Test
    public void testDecrementSellOrders() {
//        SELL ORDERS
        Order firstSellOrder = new Order();

        firstSellOrder.setActive(true);
        firstSellOrder.setOfferPrice(new BigDecimal("576.42").setScale(2, RoundingMode.HALF_UP));
        firstSellOrder.setSide(false);
        firstSellOrder.setSize(10);
        firstSellOrder.setSymbol("GOOG");
        firstSellOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        firstSellOrder = orders.save(firstSellOrder);

        Order secondSellOrder = new Order();

        secondSellOrder.setActive(true);
        secondSellOrder.setOfferPrice(new BigDecimal("576.58").setScale(2, RoundingMode.HALF_UP));
        secondSellOrder.setSide(false);
        secondSellOrder.setSize(10);
        secondSellOrder.setSymbol("GOOG");
        secondSellOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        secondSellOrder = orders.save(secondSellOrder);

        Order thirdSellOrder = new Order();

        thirdSellOrder.setActive(true);
        thirdSellOrder.setOfferPrice(new BigDecimal("576.67").setScale(2, RoundingMode.HALF_UP));
        thirdSellOrder.setSide(false);
        thirdSellOrder.setSize(10);
        thirdSellOrder.setSymbol("GOOG");
        thirdSellOrder.setTime(LocalDateTime.parse("2020-01-01T12:00:00"));

        thirdSellOrder = orders.save(thirdSellOrder);

        BigDecimal firstOriginalPrice = firstSellOrder.getOfferPrice();
        BigDecimal secondOriginalPrice = secondSellOrder.getOfferPrice();
        BigDecimal thirdOriginalPrice = thirdSellOrder.getOfferPrice();
        
        String tick = "0.01";

        service.decrementSellOrders(tick, firstSellOrder.getSymbol());

        assertNotEquals(firstOriginalPrice, firstSellOrder.getOfferPrice());
        assertEquals(firstOriginalPrice.subtract(new BigDecimal(tick)), firstSellOrder.getOfferPrice());
        assertNotEquals(secondOriginalPrice, secondSellOrder.getOfferPrice());
        assertEquals(secondOriginalPrice.subtract(new BigDecimal(tick)), secondSellOrder.getOfferPrice());
        assertNotEquals(thirdOriginalPrice, thirdSellOrder.getOfferPrice());
        assertEquals(thirdOriginalPrice.subtract(new BigDecimal(tick)), thirdSellOrder.getOfferPrice());
    }
}
