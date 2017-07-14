package com.ubs.dataveri.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

import com.ubs.dataveri.domain.enumeration.CurrencyType;

/**
 * A Stock.
 */
@Entity
@Table(name = "stock")
@Document(indexName = "stock")
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "quote")
    private Double quote;

    @Column(name = "jhi_close")
    private Double close;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private CurrencyType currency;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public Stock symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getQuote() {
        return quote;
    }

    public Stock quote(Double quote) {
        this.quote = quote;
        return this;
    }

    public void setQuote(Double quote) {
        this.quote = quote;
    }

    public Double getClose() {
        return close;
    }

    public Stock close(Double close) {
        this.close = close;
        return this;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public Stock currency(CurrencyType currency) {
        this.currency = currency;
        return this;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Stock stock = (Stock) o;
        if (stock.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), stock.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Stock{" +
            "id=" + getId() +
            ", symbol='" + getSymbol() + "'" +
            ", quote='" + getQuote() + "'" +
            ", close='" + getClose() + "'" +
            ", currency='" + getCurrency() + "'" +
            "}";
    }
}
