package com.ubs.dataveri.repository;

import com.ubs.dataveri.domain.Bond;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Bond entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BondRepository extends JpaRepository<Bond,Long> {
    
}
