package com.ubs.dataveri.repository.search;

import com.ubs.dataveri.domain.Trader;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Trader entity.
 */
public interface TraderSearchRepository extends ElasticsearchRepository<Trader, Long> {
}
