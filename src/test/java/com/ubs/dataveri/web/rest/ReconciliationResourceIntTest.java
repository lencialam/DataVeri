package com.ubs.dataveri.web.rest;

import com.ubs.dataveri.DataVeriApp;

import com.ubs.dataveri.domain.Reconciliation;
import com.ubs.dataveri.domain.Report;
import com.ubs.dataveri.repository.ReconciliationRepository;
import com.ubs.dataveri.repository.search.ReconciliationSearchRepository;
import com.ubs.dataveri.service.MailService;
import com.ubs.dataveri.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ubs.dataveri.domain.enumeration.ProductType;
/**
 * Test class for the ReconciliationResource REST controller.
 *
 * @see ReconciliationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataVeriApp.class)
public class ReconciliationResourceIntTest {

    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final ProductType DEFAULT_PRODUCT = ProductType.Stock;
    private static final ProductType UPDATED_PRODUCT = ProductType.Bond;

    private static final Long DEFAULT_POSITION = 1L;
    private static final Long UPDATED_POSITION = 2L;

    private static final Double DEFAULT_INTERNAL_CLOSE = 1D;
    private static final Double UPDATED_INTERNAL_CLOSE = 2D;

    private static final BigDecimal DEFAULT_INTERNAL_PNL = new BigDecimal(1);
    private static final BigDecimal UPDATED_INTERNAL_PNL = new BigDecimal(2);

    @Autowired
    private ReconciliationRepository reconciliationRepository;

    @Autowired
    private ReconciliationSearchRepository reconciliationSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private MailService mailService;

    @Autowired
    private EntityManager em;

    private MockMvc restReconciliationMockMvc;

    private Reconciliation reconciliation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReconciliationResource reconciliationResource = new ReconciliationResource(reconciliationRepository, reconciliationSearchRepository, mailService);
        this.restReconciliationMockMvc = MockMvcBuilders.standaloneSetup(reconciliationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reconciliation createEntity(EntityManager em) {
        Reconciliation reconciliation = new Reconciliation()
            .symbol(DEFAULT_SYMBOL)
            .product(DEFAULT_PRODUCT)
            .position(DEFAULT_POSITION)
            .internalClose(DEFAULT_INTERNAL_CLOSE)
            .internalPnl(DEFAULT_INTERNAL_PNL);
        // Add required entity
        Report report = ReportResourceIntTest.createEntity(em);
        em.persist(report);
        em.flush();
        reconciliation.setReport(report);
        return reconciliation;
    }

    @Before
    public void initTest() {
        reconciliationSearchRepository.deleteAll();
        reconciliation = createEntity(em);
    }

    @Test
    @Transactional
    public void createReconciliation() throws Exception {
        int databaseSizeBeforeCreate = reconciliationRepository.findAll().size();

        // Create the Reconciliation
        restReconciliationMockMvc.perform(post("/api/reconciliations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reconciliation)))
            .andExpect(status().isCreated());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeCreate + 1);
        Reconciliation testReconciliation = reconciliationList.get(reconciliationList.size() - 1);
        assertThat(testReconciliation.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testReconciliation.getProduct()).isEqualTo(DEFAULT_PRODUCT);
        assertThat(testReconciliation.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testReconciliation.getInternalClose()).isEqualTo(DEFAULT_INTERNAL_CLOSE);
        assertThat(testReconciliation.getInternalPnl()).isEqualTo(DEFAULT_INTERNAL_PNL);

        // Validate the Reconciliation in Elasticsearch
        Reconciliation reconciliationEs = reconciliationSearchRepository.findOne(testReconciliation.getId());
        assertThat(reconciliationEs).isEqualToComparingFieldByField(testReconciliation);
    }

    @Test
    @Transactional
    public void createReconciliationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = reconciliationRepository.findAll().size();

        // Create the Reconciliation with an existing ID
        reconciliation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restReconciliationMockMvc.perform(post("/api/reconciliations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reconciliation)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllReconciliations() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList
        restReconciliationMockMvc.perform(get("/api/reconciliations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reconciliation.getId().intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL.toString())))
            .andExpect(jsonPath("$.[*].product").value(hasItem(DEFAULT_PRODUCT.toString())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION.intValue())))
            .andExpect(jsonPath("$.[*].internalClose").value(hasItem(DEFAULT_INTERNAL_CLOSE.doubleValue())))
            .andExpect(jsonPath("$.[*].internalPnl").value(hasItem(DEFAULT_INTERNAL_PNL.intValue())));
    }

    @Test
    @Transactional
    public void getReconciliation() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get the reconciliation
        restReconciliationMockMvc.perform(get("/api/reconciliations/{id}", reconciliation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(reconciliation.getId().intValue()))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL.toString()))
            .andExpect(jsonPath("$.product").value(DEFAULT_PRODUCT.toString()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION.intValue()))
            .andExpect(jsonPath("$.internalClose").value(DEFAULT_INTERNAL_CLOSE.doubleValue()))
            .andExpect(jsonPath("$.internalPnl").value(DEFAULT_INTERNAL_PNL.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingReconciliation() throws Exception {
        // Get the reconciliation
        restReconciliationMockMvc.perform(get("/api/reconciliations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReconciliation() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);
        reconciliationSearchRepository.save(reconciliation);
        int databaseSizeBeforeUpdate = reconciliationRepository.findAll().size();

        // Update the reconciliation
        Reconciliation updatedReconciliation = reconciliationRepository.findOne(reconciliation.getId());
        updatedReconciliation
            .symbol(UPDATED_SYMBOL)
            .product(UPDATED_PRODUCT)
            .position(UPDATED_POSITION)
            .internalClose(UPDATED_INTERNAL_CLOSE)
            .internalPnl(UPDATED_INTERNAL_PNL);

        restReconciliationMockMvc.perform(put("/api/reconciliations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedReconciliation)))
            .andExpect(status().isOk());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeUpdate);
        Reconciliation testReconciliation = reconciliationList.get(reconciliationList.size() - 1);
        assertThat(testReconciliation.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testReconciliation.getProduct()).isEqualTo(UPDATED_PRODUCT);
        assertThat(testReconciliation.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testReconciliation.getInternalClose()).isEqualTo(UPDATED_INTERNAL_CLOSE);
        assertThat(testReconciliation.getInternalPnl()).isEqualTo(UPDATED_INTERNAL_PNL);

        // Validate the Reconciliation in Elasticsearch
        Reconciliation reconciliationEs = reconciliationSearchRepository.findOne(testReconciliation.getId());
        assertThat(reconciliationEs).isEqualToComparingFieldByField(testReconciliation);
    }

    @Test
    @Transactional
    public void updateNonExistingReconciliation() throws Exception {
        int databaseSizeBeforeUpdate = reconciliationRepository.findAll().size();

        // Create the Reconciliation

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restReconciliationMockMvc.perform(put("/api/reconciliations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reconciliation)))
            .andExpect(status().isCreated());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteReconciliation() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);
        reconciliationSearchRepository.save(reconciliation);
        int databaseSizeBeforeDelete = reconciliationRepository.findAll().size();

        // Get the reconciliation
        restReconciliationMockMvc.perform(delete("/api/reconciliations/{id}", reconciliation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean reconciliationExistsInEs = reconciliationSearchRepository.exists(reconciliation.getId());
        assertThat(reconciliationExistsInEs).isFalse();

        // Validate the database is empty
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchReconciliation() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);
        reconciliationSearchRepository.save(reconciliation);

        // Search the reconciliation
        restReconciliationMockMvc.perform(get("/api/_search/reconciliations?query=id:" + reconciliation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reconciliation.getId().intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL.toString())))
            .andExpect(jsonPath("$.[*].product").value(hasItem(DEFAULT_PRODUCT.toString())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION.intValue())))
            .andExpect(jsonPath("$.[*].internalClose").value(hasItem(DEFAULT_INTERNAL_CLOSE.doubleValue())))
            .andExpect(jsonPath("$.[*].internalPnl").value(hasItem(DEFAULT_INTERNAL_PNL.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reconciliation.class);
        Reconciliation reconciliation1 = new Reconciliation();
        reconciliation1.setId(1L);
        Reconciliation reconciliation2 = new Reconciliation();
        reconciliation2.setId(reconciliation1.getId());
        assertThat(reconciliation1).isEqualTo(reconciliation2);
        reconciliation2.setId(2L);
        assertThat(reconciliation1).isNotEqualTo(reconciliation2);
        reconciliation1.setId(null);
        assertThat(reconciliation1).isNotEqualTo(reconciliation2);
    }
}
