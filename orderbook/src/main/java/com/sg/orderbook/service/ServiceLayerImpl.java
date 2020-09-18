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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Minul
 */
@Service
public class ServiceLayerImpl implements ServiceLayer {

    @Autowired
    OrderRepository orders;

    @Autowired
    TransactionRepository transactions;

    @Override
    public List<Order> getAllBuyOrdersForSymbol(String symbol) {
        return orders.findAllBuyOrdersForSymbol(symbol);
    }

    @Override
    public List<Order> getAllSellOrdersForSymbol(String symbol) {
        return orders.findAllSellOrdersForSymbol(symbol);
    }

    // get list of transactions stored in transaction database table
    @Override
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactionList = transactions.findAll();
        return transactionList;
    }

    // Gets all transaction from database table and filters them to only return transactions that have the given symbol
    @Override
    public List<Transaction> getAllTransactionsForSymbol(String symbol) {
        // pull all transactions from database
        List<Transaction> transactionList = transactions.findByFinalSymbolOrderByFinalTimeDesc(symbol);

        return transactionList;
    }

    // Create new transaction and save it to transaction database table; pull info from buy order
    @Override
    public Transaction makeTransaction(Order buyOrder, Order sellOrder) {
        // Create the time field for the new transaction w/ format that sql can handle
        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
//        String formattedDateTime = now.format(formatter);

        // Create and fill a new transaction
        Transaction newTransaction = new Transaction();
        newTransaction.setFinalSymbol(buyOrder.getSymbol());
        newTransaction.setFinalPrice(buyOrder.getOfferPrice());
        newTransaction.setFinalTime(now);

        boolean buySizeBigger = buyOrder.getSize() >= sellOrder.getSize();

        newTransaction.setAmount(buySizeBigger ? sellOrder.getSize()
                : buyOrder.getSize());

        int buyOrderSize = buyOrder.getSize();
        int sellOrderSize = sellOrder.getSize();

        buyOrder.setSize(buySizeBigger ? buyOrderSize - sellOrderSize : 0);
        sellOrder.setSize(buySizeBigger ? 0 : sellOrderSize - buyOrderSize);

        buyOrder = orders.save(buyOrder);
        sellOrder = orders.save(sellOrder);
        newTransaction.setBuyOrder(buyOrder);
        newTransaction.setSellOrder(sellOrder);

        newTransaction = transactions.save(newTransaction);

        return newTransaction;
    }

    // Compare the buy and sell order offer prices; if buy order price is greater than or equal to the sell order price, returns true (vlid match)-else false (not a match)
    @Override
    public Transaction matchOrders(int givenOrderId) {
        Order givenOrder = orders.getOne(givenOrderId);

        Order createdOrder = new Order();

        createdOrder.setSide(!givenOrder.isSide());
        createdOrder.setActive(false);
        createdOrder.setSize(givenOrder.getSize());
        createdOrder.setSymbol(givenOrder.getSymbol());
        createdOrder.setTime(LocalDateTime.now());
        createdOrder.setOfferPrice(givenOrder.getOfferPrice());

        createdOrder = orders.save(createdOrder);

        givenOrder.setActive(false);

        givenOrder = orders.save(givenOrder);

        Transaction transaction = new Transaction();

        if (givenOrder.isSide()) {
            transaction = makeTransaction(givenOrder, createdOrder);
        } else {
            transaction = makeTransaction(createdOrder, givenOrder);
        }

        return transaction;
    }

    @Override
    public void deleteUnmatchedOrder(int orderId) {
        //Order order = orders.getOne(orderId);
        Order order = orders.findById(orderId).orElse(null);

        // Attempts to delete an order from the db if and the order is active and
        // there exists no transactions that use said order
        List transactionList = transactions.findAllTransactionsForOrder(order);

        if (order.getSize() > 0) {

            if (transactionList.isEmpty()) {
                orders.deleteById(order.getId());
            } else {
                order.setSize(0);
                order.setActive(false);
                orders.save(order);
            }
        }
    }

    @Override
    public void findPotentialTransactions(String symbol) {
        List<Order> buyOrders = orders.findAllBuyOrdersForSymbol(symbol);
        List<Order> sellOrders = orders.findAllSellOrdersForSymbol(symbol);

        boolean doneMatching = false;
        while (!doneMatching) {
            if (buyOrders.size() == 0 || sellOrders.size() == 0) {
                doneMatching = true;
            } else if (sellOrders.get(0).getOfferPrice().compareTo(buyOrders.get(0).getOfferPrice()) <= 0) {
                makeTransaction(buyOrders.get(0), sellOrders.get(0));
                buyOrders = orders.findAllBuyOrdersForSymbol(symbol);
                sellOrders = orders.findAllSellOrdersForSymbol(symbol);
            } else {
                doneMatching = true;
            }
        }
    }

    @Override
    public List getSymbols() {
        return orders.getSymbols();
    }

    @Override
    public void createOrderbook(String symbol) {
        Order order = new Order();
        order.setSymbol(symbol);
        order.setOfferPrice(BigDecimal.ZERO);
        order.setActive(false);
        order.setTime(LocalDateTime.now());

        order = orders.save(order);
    }

    @Override
    public void addOrder(Order newOrder) {
        orders.save(newOrder);
    }

    @Override
    public List<Transaction> getTop5ByFinalDate() {
        return transactions.findTop5ByOrderByFinalTimeDesc();
    }

    @Override
    public List<Transaction> getAllTransactionsForSymbolAndDate(String symbol, LocalDate date) {
        return transactions.findAllTransactionsForSymbolAndDate(symbol, date);
    }

    @Override
    public void incrementBuyOrders(String tick, String symbol) {
        List<Order> buyOrderList = orders.findAllBuyOrdersForSymbol(symbol);
        buyOrderList.stream().forEach(
                (o) -> {
                    o.setOfferPrice(o.getOfferPrice().add(new BigDecimal(tick)));
                    orders.save(o);
                });
    }

    @Override
    public void decrementSellOrders(String tick, String symbol) {
        List<Order> sellOrderList = orders.findAllSellOrdersForSymbol(symbol);
        sellOrderList.stream().forEach(
                (o) -> {
                    o.setOfferPrice(o.getOfferPrice().subtract(new BigDecimal(tick)));
                    orders.save(o);
                });
    }

    @Override
    public Order getOrderById(int orderId) {        
        return orders.findById(orderId).orElse(null);
    }
}
