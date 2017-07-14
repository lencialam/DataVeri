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

    @Column(name = "jhi_close")
    private Double close;

    @Column(name = "pnl", precision=10, scale=2)
    private BigDecimal pnl;

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

    public Double getClose() {
        return close;
    }

    public Reconciliation close(Double close) {
        this.close = close;
        return this;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public BigDecimal getPnl() {
        return pnl;
    }

    public Reconciliation pnl(BigDecimal pnl) {
        this.pnl = pnl;
        return this;
    }

    public void setPnl(BigDecimal pnl) {
        this.pnl = pnl;
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
            ", close='" + getClose() + "'" +
            ", pnl='" + getPnl() + "'" +
            "}";
    }
}
