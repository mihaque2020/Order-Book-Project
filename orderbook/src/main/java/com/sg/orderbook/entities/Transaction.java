/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.orderbook.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Minul
 */
@Entity
@Table( name = "`transaction`" )
public class Transaction {
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "transactionid")
    private int id;
    
    @Column(name = "finaltime")
    private LocalDateTime finalTime;
    
    @Column(name = "finalprice")
    private BigDecimal finalPrice;
    
    @Column
    private int amount;
    
    @Column(name = "finalsymbol")
    private String finalSymbol;
    
    @ManyToOne
    @JoinColumn(name = "buyorderid")
    private Order buyOrder;
    
    @ManyToOne
    @JoinColumn(name = "sellorderid")
    private Order sellOrder;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(LocalDateTime finalTime) {
        this.finalTime = finalTime;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getFinalSymbol() {
        return finalSymbol;
    }

    public void setFinalSymbol(String finalSymbol) {
        this.finalSymbol = finalSymbol;
    }

    public Order getBuyOrder() {
        return buyOrder;
    }

    public void setBuyOrder(Order buyOrder) {
        this.buyOrder = buyOrder;
    }

    public Order getSellOrder() {
        return sellOrder;
    }

    public void setSellOrder(Order sellOrder) {
        this.sellOrder = sellOrder;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.id;
        hash = 11 * hash + Objects.hashCode(this.finalTime);
        hash = 11 * hash + Objects.hashCode(this.finalPrice);
        hash = 11 * hash + this.amount;
        hash = 11 * hash + Objects.hashCode(this.finalSymbol);
        hash = 11 * hash + Objects.hashCode(this.buyOrder);
        hash = 11 * hash + Objects.hashCode(this.sellOrder);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transaction other = (Transaction) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.amount != other.amount) {
            return false;
        }
        if (!Objects.equals(this.finalSymbol, other.finalSymbol)) {
            return false;
        }
        if (!Objects.equals(this.finalTime, other.finalTime)) {
            return false;
        }
        if (!Objects.equals(this.finalPrice, other.finalPrice)) {
            return false;
        }
        if (!Objects.equals(this.buyOrder, other.buyOrder)) {
            return false;
        }
        if (!Objects.equals(this.sellOrder, other.sellOrder)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Transaction{" + "id=" + id + ", finalTime=" + finalTime + ", finalPrice=" + finalPrice + ", amount=" + amount + ", finalSymbol=" + finalSymbol + ", buyOrder=" + buyOrder + ", sellOrder=" + sellOrder + '}';
    }
    
}
