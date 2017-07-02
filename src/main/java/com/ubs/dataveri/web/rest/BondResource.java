package com.ubs.dataveri.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ubs.dataveri.domain.Bond;

import com.ubs.dataveri.repository.BondRepository;
import com.ubs.dataveri.repository.search.BondSearchRepository;
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
 * REST controller for managing Bond.
 */
@RestController
@RequestMapping("/api")
public class BondResource {

    private final Logger log = LoggerFactory.getLogger(BondResource.class);

    private static final String ENTITY_NAME = "bond";

    private final BondRepository bondRepository;

    private final BondSearchRepository bondSearchRepository;

    public BondResource(BondRepository bondRepository, BondSearchRepository bondSearchRepository) {
        this.bondRepository = bondRepository;
        this.bondSearchRepository = bondSearchRepository;
    }

    /**
     * POST  /bonds : Create a new bond.
     *
     * @param bond the bond to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bond, or with status 400 (Bad Request) if the bond has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/bonds")
    @Timed
    public ResponseEntity<Bond> createBond(@Valid @RequestBody Bond bond) throws URISyntaxException {
        log.debug("REST request to save Bond : {}", bond);
        if (bond.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new bond cannot already have an ID")).body(null);
        }
        Bond result = bondRepository.save(bond);
        bondSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/bonds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bonds : Updates an existing bond.
     *
     * @param bond the bond to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bond,
     * or with status 400 (Bad Request) if the bond is not valid,
     * or with status 500 (Internal Server Error) if the bond couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/bonds")
    @Timed
    public ResponseEntity<Bond> updateBond(@Valid @RequestBody Bond bond) throws URISyntaxException {
        log.debug("REST request to update Bond : {}", bond);
        if (bond.getId() == null) {
            return createBond(bond);
        }
        Bond result = bondRepository.save(bond);
        bondSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, bond.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bonds : get all the bonds.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of bonds in body
     */
    @GetMapping("/bonds")
    @Timed
    public ResponseEntity<List<Bond>> getAllBonds(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Bonds");
        Page<Bond> page = bondRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bonds");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /bonds/:id : get the "id" bond.
     *
     * @param id the id of the bond to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bond, or with status 404 (Not Found)
     */
    @GetMapping("/bonds/{id}")
    @Timed
    public ResponseEntity<Bond> getBond(@PathVariable Long id) {
        log.debug("REST request to get Bond : {}", id);
        Bond bond = bondRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(bond));
    }

    /**
     * DELETE  /bonds/:id : delete the "id" bond.
     *
     * @param id the id of the bond to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/bonds/{id}")
    @Timed
    public ResponseEntity<Void> deleteBond(@PathVariable Long id) {
        log.debug("REST request to delete Bond : {}", id);
        bondRepository.delete(id);
        bondSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/bonds?query=:query : search for the bond corresponding
     * to the query.
     *
     * @param query the query of the bond search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/bonds")
    @Timed
    public ResponseEntity<List<Bond>> searchBonds(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Bonds for query {}", query);
        Page<Bond> page = bondSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/bonds");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
