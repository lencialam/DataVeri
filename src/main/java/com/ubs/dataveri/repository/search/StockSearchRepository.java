package com.ubs.dataveri.repository.search;

import com.ubs.dataveri.domain.Stock;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Stock entity.
 */
public interface StockSearchRepository extends ElasticsearchRepository<Stock, Long> {
}
