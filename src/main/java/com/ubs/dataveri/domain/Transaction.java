package com.ubs.dataveri.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

import com.ubs.dataveri.domain.enumeration.ProductType;

import com.ubs.dataveri.domain.enumeration.TransactionType;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@Document(indexName = "transaction")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "trade_id", nullable = false)
    private Long tradeId;

    @NotNull
    @Column(name = "trade_time", nullable = false)
    private ZonedDateTime tradeTime;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Column(name = "symbol", nullable = false)
    private String symbol;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "product", nullable = false)
    private ProductType product;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_type", nullable = false)
    private TransactionType type;

    @NotNull
    @Column(name = "strike_price", nullable = false)
    private Double strikePrice;

    @Column(name = "cash", precision=10, scale=2)
    private BigDecimal cash;

    @ManyToOne(optional = false)
    @NotNull
    private Trader trader;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTradeId() {
        return tradeId;
    }

    public Transaction tradeId(Long tradeId) {
        this.tradeId = tradeId;
        return this;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public ZonedDateTime getTradeTime() {
        return tradeTime;
    }

    public Transaction tradeTime(ZonedDateTime tradeTime) {
        this.tradeTime = tradeTime;
        return this;
    }

    public void setTradeTime(ZonedDateTime tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public Transaction symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public ProductType getProduct() {
        return product;
    }

    public Transaction product(ProductType product) {
        this.product = product;
        return this;
    }

    public void setProduct(ProductType product) {
        this.product = product;
    }

    public TransactionType getType() {
        return type;
    }

    public Transaction type(TransactionType type) {
        this.type = type;
        return this;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Double getStrikePrice() {
        return strikePrice;
    }

    public Transaction strikePrice(Double strikePrice) {
        this.strikePrice = strikePrice;
        return this;
    }

    public void setStrikePrice(Double strikePrice) {
        this.strikePrice = strikePrice;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public Transaction cash(BigDecimal cash) {
        this.cash = cash;
        return this;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public Trader getTrader() {
        return trader;
    }

    public Transaction trader(Trader trader) {
        this.trader = trader;
        return this;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction transaction = (Transaction) o;
        if (transaction.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), transaction.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", tradeId='" + getTradeId() + "'" +
            ", tradeTime='" + getTradeTime() + "'" +
            ", symbol='" + getSymbol() + "'" +
            ", product='" + getProduct() + "'" +
            ", type='" + getType() + "'" +
            ", strikePrice='" + getStrikePrice() + "'" +
            ", cash='" + getCash() + "'" +
            "}";
    }
}
