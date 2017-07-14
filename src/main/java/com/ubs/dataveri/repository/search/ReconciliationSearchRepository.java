package com.ubs.dataveri.repository.search;

import com.ubs.dataveri.domain.Reconciliation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Reconciliation entity.
 */
public interface ReconciliationSearchRepository extends ElasticsearchRepository<Reconciliation, Long> {
}
