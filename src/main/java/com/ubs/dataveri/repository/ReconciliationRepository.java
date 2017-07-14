package com.ubs.dataveri.repository;

import com.ubs.dataveri.domain.Reconciliation;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Reconciliation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReconciliationRepository extends JpaRepository<Reconciliation,Long> {
    
}
