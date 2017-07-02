package com.ubs.dataveri.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Bond.
 */
@Entity
@Table(name = "bond")
@Document(indexName = "bond")
public class Bond implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "trade_id", nullable = false)
    private Long tradeId;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    @Column(name = "symbol", nullable = false)
    private String symbol;

    @NotNull
    @Column(name = "quote", nullable = false)
    private Double quote;

    @NotNull
    @Column(name = "position", nullable = false)
    private Long position;

    @NotNull
    @Column(name = "pnl", nullable = false)
    private Long pnl;

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

    public Bond tradeId(Long tradeId) {
        this.tradeId = tradeId;
        return this;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public String getSymbol() {
        return symbol;
    }

    public Bond symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getQuote() {
        return quote;
    }

    public Bond quote(Double quote) {
        this.quote = quote;
        return this;
    }

    public void setQuote(Double quote) {
        this.quote = quote;
    }

    public Long getPosition() {
        return position;
    }

    public Bond position(Long position) {
        this.position = position;
        return this;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Long getPnl() {
        return pnl;
    }

    public Bond pnl(Long pnl) {
        this.pnl = pnl;
        return this;
    }

    public void setPnl(Long pnl) {
        this.pnl = pnl;
    }

    public Trader getTrader() {
        return trader;
    }

    public Bond trader(Trader trader) {
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
        Bond bond = (Bond) o;
        if (bond.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), bond.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Bond{" +
            "id=" + getId() +
            ", tradeId='" + getTradeId() + "'" +
            ", symbol='" + getSymbol() + "'" +
            ", quote='" + getQuote() + "'" +
            ", position='" + getPosition() + "'" +
            ", pnl='" + getPnl() + "'" +
            "}";
    }
}
