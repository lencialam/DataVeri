package com.ubs.dataveri.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import com.ubs.dataveri.domain.enumeration.ProductType;

/**
 * A Report.
 */
@Entity
@Table(name = "report")
@Document(indexName = "report")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Column(name = "symbol", nullable = false)
    private String symbol;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "product", nullable = false)
    private ProductType product;

    @NotNull
    @Column(name = "position", nullable = false)
    private Long position;

    @NotNull
    @Column(name = "internal_close", nullable = false)
    private Double internalClose;

    @Column(name = "internal_pnl", precision=10, scale=2)
    private BigDecimal internalPnl;

    @ManyToOne(optional = false)
    @NotNull
    private Trader trader;

    @OneToOne(mappedBy = "report")
    //@JsonIgnore
    private Reconciliation reconciliation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public Report symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public ProductType getProduct() {
        return product;
    }

    public Report product(ProductType product) {
        this.product = product;
        return this;
    }

    public void setProduct(ProductType product) {
        this.product = product;
    }

    public Long getPosition() {
        return position;
    }

    public Report position(Long position) {
        this.position = position;
        return this;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Double getInternalClose() {
        return internalClose;
    }

    public Report internalClose(Double internalClose) {
        this.internalClose = internalClose;
        return this;
    }

    public void setInternalClose(Double internalClose) {
        this.internalClose = internalClose;
    }

    public BigDecimal getInternalPnl() {
        return internalPnl;
    }

    public Report internalPnl(BigDecimal internalPnl) {
        this.internalPnl = internalPnl;
        return this;
    }

    public void setInternalPnl(BigDecimal internalPnl) {
        this.internalPnl = internalPnl;
    }

    public Trader getTrader() {
        return trader;
    }

    public Report trader(Trader trader) {
        this.trader = trader;
        return this;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    public Reconciliation getReconciliation() {
        return reconciliation;
    }

    public Report reconciliation(Reconciliation reconciliation) {
        this.reconciliation = reconciliation;
        return this;
    }

    public void setReconciliation(Reconciliation reconciliation) {
        this.reconciliation = reconciliation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Report report = (Report) o;
        if (report.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), report.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Report{" +
            "id=" + getId() +
            ", symbol='" + getSymbol() + "'" +
            ", product='" + getProduct() + "'" +
            ", position='" + getPosition() + "'" +
            ", internalClose='" + getInternalClose() + "'" +
            ", internalPnl='" + getInternalPnl() + "'" +
            "}";
    }
}
