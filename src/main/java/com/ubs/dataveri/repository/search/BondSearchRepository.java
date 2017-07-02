package com.ubs.dataveri.repository.search;

import com.ubs.dataveri.domain.Bond;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Bond entity.
 */
public interface BondSearchRepository extends ElasticsearchRepository<Bond, Long> {
}
