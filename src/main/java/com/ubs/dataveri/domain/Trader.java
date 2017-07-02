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
    private Long id;

    @OneToOne(optional = false)
    @NotNull
    @MapsId
    private User user;

    @OneToMany(mappedBy = "trader")
    @JsonIgnore
    private Set<Bond> bonds = new HashSet<>();

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

    public Set<Bond> getBonds() {
        return bonds;
    }

    public Trader bonds(Set<Bond> bonds) {
        this.bonds = bonds;
        return this;
    }

    public Trader addBond(Bond bond) {
        this.bonds.add(bond);
        bond.setTrader(this);
        return this;
    }

    public Trader removeBond(Bond bond) {
        this.bonds.remove(bond);
        bond.setTrader(null);
        return this;
    }

    public void setBonds(Set<Bond> bonds) {
        this.bonds = bonds;
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
