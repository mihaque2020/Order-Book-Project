/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.orderbook.repositories;

import com.sg.orderbook.entities.Order;
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
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    
    @Query("SELECT o FROM Order o WHERE o.size > 0")
    List<Order> findAllActiveOrders();
    
    @Query("SELECT o FROM Order o WHERE o.side = 1 AND o.size > 0 AND o.symbol = :symbol ORDER BY o.offerPrice DESC, time ASC")
    List<Order> findAllBuyOrdersForSymbol(@Param("symbol") String symbol);
    
    @Query("SELECT o FROM Order o WHERE o.side = 0 AND o.size > 0 AND o.symbol = :symbol ORDER BY o.offerPrice ASC, time ASC")
    List<Order> findAllSellOrdersForSymbol(@Param("symbol") String symbol);
    
    @Query("SELECT DISTINCT symbol FROM Order o ORDER BY o.symbol ASC")
    List getSymbols();
}
