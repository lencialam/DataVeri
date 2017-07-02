package com.ubs.dataveri.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ubs.dataveri.domain.Trader;

import com.ubs.dataveri.repository.TraderRepository;
import com.ubs.dataveri.repository.search.TraderSearchRepository;
import com.ubs.dataveri.web.rest.util.HeaderUtil;
import com.ubs.dataveri.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Trader.
 */
@RestController
@RequestMapping("/api")
public class TraderResource {

    private final Logger log = LoggerFactory.getLogger(TraderResource.class);

    private static final String ENTITY_NAME = "trader";

    private final TraderRepository traderRepository;

    private final TraderSearchRepository traderSearchRepository;

    public TraderResource(TraderRepository traderRepository, TraderSearchRepository traderSearchRepository) {
        this.traderRepository = traderRepository;
        this.traderSearchRepository = traderSearchRepository;
    }

    /**
     * POST  /traders : Create a new trader.
     *
     * @param trader the trader to create
     * @return the ResponseEntity with status 201 (Created) and with body the new trader, or with status 400 (Bad Request) if the trader has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/traders")
    @Timed
    public ResponseEntity<Trader> createTrader(@Valid @RequestBody Trader trader) throws URISyntaxException {
        log.debug("REST request to save Trader : {}", trader);
        if (trader.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new trader cannot already have an ID")).body(null);
        }
        Trader result = traderRepository.save(trader);
        traderSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/traders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /traders : Updates an existing trader.
     *
     * @param trader the trader to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated trader,
     * or with status 400 (Bad Request) if the trader is not valid,
     * or with status 500 (Internal Server Error) if the trader couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/traders")
    @Timed
    public ResponseEntity<Trader> updateTrader(@Valid @RequestBody Trader trader) throws URISyntaxException {
        log.debug("REST request to update Trader : {}", trader);
        if (trader.getId() == null) {
            return createTrader(trader);
        }
        Trader result = traderRepository.save(trader);
        traderSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, trader.getId().toString()))
            .body(result);
    }

    /**
     * GET  /traders : get all the traders.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of traders in body
     */
    @GetMapping("/traders")
    @Timed
    public ResponseEntity<List<Trader>> getAllTraders(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Traders");
        Page<Trader> page = traderRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/traders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /traders/:id : get the "id" trader.
     *
     * @param id the id of the trader to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the trader, or with status 404 (Not Found)
     */
    @GetMapping("/traders/{id}")
    @Timed
    public ResponseEntity<Trader> getTrader(@PathVariable Long id) {
        log.debug("REST request to get Trader : {}", id);
        Trader trader = traderRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(trader));
    }

    /**
     * DELETE  /traders/:id : delete the "id" trader.
     *
     * @param id the id of the trader to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/traders/{id}")
    @Timed
    public ResponseEntity<Void> deleteTrader(@PathVariable Long id) {
        log.debug("REST request to delete Trader : {}", id);
        traderRepository.delete(id);
        traderSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/traders?query=:query : search for the trader corresponding
     * to the query.
     *
     * @param query the query of the trader search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/traders")
    @Timed
    public ResponseEntity<List<Trader>> searchTraders(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Traders for query {}", query);
        Page<Trader> page = traderSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/traders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
