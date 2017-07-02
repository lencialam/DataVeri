package com.ubs.dataveri.repository;

import com.ubs.dataveri.domain.Trader;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Trader entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TraderRepository extends JpaRepository<Trader,Long> {
    
}
