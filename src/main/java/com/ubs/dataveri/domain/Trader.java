package com.ubs.dataveri.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Trader.
 */
@Entity
@Table(name = "trader")
@Document(indexName = "trader")
public class Trader implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "trader")
    @JsonIgnore
    private Set<Transaction> transactions = new HashSet<>();

    @OneToMany(mappedBy = "trader")
    @JsonIgnore
    private Set<Report> reports = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public Trader user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public Trader transactions(Set<Transaction> transactions) {
        this.transactions = transactions;
        return this;
    }

    public Trader addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setTrader(this);
        return this;
    }

    public Trader removeTransaction(Transaction transaction) {
        this.transactions.remove(transaction);
        transaction.setTrader(null);
        return this;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public Trader reports(Set<Report> reports) {
        this.reports = reports;
        return this;
    }

    public Trader addReport(Report report) {
        this.reports.add(report);
        report.setTrader(this);
        return this;
    }

    public Trader removeReport(Report report) {
        this.reports.remove(report);
        report.setTrader(null);
        return this;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Trader trader = (Trader) o;
        if (trader.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), trader.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Trader{" +
            "id=" + getId() +
            "}";
    }
}
