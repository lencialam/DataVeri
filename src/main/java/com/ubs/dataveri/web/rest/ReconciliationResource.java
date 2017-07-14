package com.ubs.dataveri.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ubs.dataveri.domain.Reconciliation;

import com.ubs.dataveri.repository.ReconciliationRepository;
import com.ubs.dataveri.repository.search.ReconciliationSearchRepository;
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
 * REST controller for managing Reconciliation.
 */
@RestController
@RequestMapping("/api")
public class ReconciliationResource {

    private final Logger log = LoggerFactory.getLogger(ReconciliationResource.class);

    private static final String ENTITY_NAME = "reconciliation";

    private final ReconciliationRepository reconciliationRepository;

    private final ReconciliationSearchRepository reconciliationSearchRepository;

    public ReconciliationResource(ReconciliationRepository reconciliationRepository, ReconciliationSearchRepository reconciliationSearchRepository) {
        this.reconciliationRepository = reconciliationRepository;
        this.reconciliationSearchRepository = reconciliationSearchRepository;
    }

    /**
     * POST  /reconciliations : Create a new reconciliation.
     *
     * @param reconciliation the reconciliation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new reconciliation, or with status 400 (Bad Request) if the reconciliation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/reconciliations")
    @Timed
    public ResponseEntity<Reconciliation> createReconciliation(@Valid @RequestBody Reconciliation reconciliation) throws URISyntaxException {
        log.debug("REST request to save Reconciliation : {}", reconciliation);
        if (reconciliation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new reconciliation cannot already have an ID")).body(null);
        }
        Reconciliation result = reconciliationRepository.save(reconciliation);
        reconciliationSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/reconciliations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /reconciliations : Updates an existing reconciliation.
     *
     * @param reconciliation the reconciliation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated reconciliation,
     * or with status 400 (Bad Request) if the reconciliation is not valid,
     * or with status 500 (Internal Server Error) if the reconciliation couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/reconciliations")
    @Timed
    public ResponseEntity<Reconciliation> updateReconciliation(@Valid @RequestBody Reconciliation reconciliation) throws URISyntaxException {
        log.debug("REST request to update Reconciliation : {}", reconciliation);
        if (reconciliation.getId() == null) {
            return createReconciliation(reconciliation);
        }
        Reconciliation result = reconciliationRepository.save(reconciliation);
        reconciliationSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, reconciliation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /reconciliations : get all the reconciliations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of reconciliations in body
     */
    @GetMapping("/reconciliations")
    @Timed
    public ResponseEntity<List<Reconciliation>> getAllReconciliations(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Reconciliations");
        Page<Reconciliation> page = reconciliationRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reconciliations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /reconciliations/:id : get the "id" reconciliation.
     *
     * @param id the id of the reconciliation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the reconciliation, or with status 404 (Not Found)
     */
    @GetMapping("/reconciliations/{id}")
    @Timed
    public ResponseEntity<Reconciliation> getReconciliation(@PathVariable Long id) {
        log.debug("REST request to get Reconciliation : {}", id);
        Reconciliation reconciliation = reconciliationRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(reconciliation));
    }

    /**
     * DELETE  /reconciliations/:id : delete the "id" reconciliation.
     *
     * @param id the id of the reconciliation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/reconciliations/{id}")
    @Timed
    public ResponseEntity<Void> deleteReconciliation(@PathVariable Long id) {
        log.debug("REST request to delete Reconciliation : {}", id);
        reconciliationRepository.delete(id);
        reconciliationSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/reconciliations?query=:query : search for the reconciliation corresponding
     * to the query.
     *
     * @param query the query of the reconciliation search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/reconciliations")
    @Timed
    public ResponseEntity<List<Reconciliation>> searchReconciliations(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Reconciliations for query {}", query);
        Page<Reconciliation> page = reconciliationSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/reconciliations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
