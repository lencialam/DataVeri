package com.ubs.dataveri.repository.search;

import com.ubs.dataveri.domain.Transaction;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Transaction entity.
 */
public interface TransactionSearchRepository extends ElasticsearchRepository<Transaction, Long> {
}
