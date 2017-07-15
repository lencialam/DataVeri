package com.ubs.dataveri.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import com.ubs.dataveri.domain.enumeration.ProductType;

/**
 * A Reconciliation.
 */
@Entity
@Table(name = "reconciliation")
@Document(indexName = "reconciliation")
public class Reconciliation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Column(name = "symbol")
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "product")
    private ProductType product;

    @Column(name = "position")
    private Long position;

    @Column(name = "internal_close")
    private Double internalClose;

    @Column(name = "internal_pnl", precision=10, scale=2)
    private BigDecimal internalPnl;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Report report;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public Reconciliation symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public ProductType getProduct() {
        return product;
    }

    public Reconciliation product(ProductType product) {
        this.product = product;
        return this;
    }

    public void setProduct(ProductType product) {
        this.product = product;
    }

    public Long getPosition() {
        return position;
    }

    public Reconciliation position(Long position) {
        this.position = position;
        return this;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Double getInternalClose() {
        return internalClose;
    }

    public Reconciliation internalClose(Double internalClose) {
        this.internalClose = internalClose;
        return this;
    }

    public void setInternalClose(Double internalClose) {
        this.internalClose = internalClose;
    }

    public BigDecimal getInternalPnl() {
        return internalPnl;
    }

    public Reconciliation internalPnl(BigDecimal internalPnl) {
        this.internalPnl = internalPnl;
        return this;
    }

    public void setInternalPnl(BigDecimal internalPnl) {
        this.internalPnl = internalPnl;
    }

    public Report getReport() {
        return report;
    }

    public Reconciliation report(Report report) {
        this.report = report;
        return this;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reconciliation reconciliation = (Reconciliation) o;
        if (reconciliation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), reconciliation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Reconciliation{" +
            "id=" + getId() +
            ", symbol='" + getSymbol() + "'" +
            ", product='" + getProduct() + "'" +
            ", position='" + getPosition() + "'" +
            ", internalClose='" + getInternalClose() + "'" +
            ", internalPnl='" + getInternalPnl() + "'" +
            "}";
    }
}
