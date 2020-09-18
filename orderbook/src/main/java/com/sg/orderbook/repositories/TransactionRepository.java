/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.orderbook.repositories;

import com.sg.orderbook.entities.Order;
import com.sg.orderbook.entities.Transaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Minul
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer>{
    
    List findByFinalSymbolOrderByFinalTimeDesc(String symbol);
    
    List findByFinalTime(LocalDateTime finalTime);
    
    @Query("SELECT t FROM Transaction t WHERE t.buyOrder = :order OR t.sellOrder = :order")
    List findAllTransactionsForOrder(@Param("order") Order order);
    
    List findTop5ByOrderByFinalTimeDesc();

    @Query(value = "SELECT * FROM Transaction t WHERE t.finalSymbol = :symbol AND CAST(t.finalTime AS DATE) = :date", nativeQuery = true)
    List<Transaction> findAllTransactionsForSymbolAndDate(@Param("symbol") String symbol, @Param("date") LocalDate date);
}
