package com.ubs.dataveri.web.rest;

import com.ubs.dataveri.DataVeriApp;

import com.ubs.dataveri.domain.Bond;
import com.ubs.dataveri.domain.Trader;
import com.ubs.dataveri.repository.BondRepository;
import com.ubs.dataveri.repository.search.BondSearchRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BondResource REST controller.
 *
 * @see BondResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataVeriApp.class)
public class BondResourceIntTest {

    private static final Long DEFAULT_TRADE_ID = 1L;
    private static final Long UPDATED_TRADE_ID = 2L;

    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final Double DEFAULT_QUOTE = 1D;
    private static final Double UPDATED_QUOTE = 2D;

    private static final Long DEFAULT_POSITION = 1L;
    private static final Long UPDATED_POSITION = 2L;

    private static final Long DEFAULT_PNL = 1L;
    private static final Long UPDATED_PNL = 2L;

    @Autowired
    private BondRepository bondRepository;

    @Autowired
    private BondSearchRepository bondSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBondMockMvc;

    private Bond bond;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BondResource bondResource = new BondResource(bondRepository, bondSearchRepository);
        this.restBondMockMvc = MockMvcBuilders.standaloneSetup(bondResource)
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
    public static Bond createEntity(EntityManager em) {
        Bond bond = new Bond()
            .tradeId(DEFAULT_TRADE_ID)
            .symbol(DEFAULT_SYMBOL)
            .quote(DEFAULT_QUOTE)
            .position(DEFAULT_POSITION)
            .pnl(DEFAULT_PNL);
        // Add required entity
        Trader trader = TraderResourceIntTest.createEntity(em);
        em.persist(trader);
        em.flush();
        bond.setTrader(trader);
        return bond;
    }

    @Before
    public void initTest() {
        bondSearchRepository.deleteAll();
        bond = createEntity(em);
    }

    @Test
    @Transactional
    public void createBond() throws Exception {
        int databaseSizeBeforeCreate = bondRepository.findAll().size();

        // Create the Bond
        restBondMockMvc.perform(post("/api/bonds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bond)))
            .andExpect(status().isCreated());

        // Validate the Bond in the database
        List<Bond> bondList = bondRepository.findAll();
        assertThat(bondList).hasSize(databaseSizeBeforeCreate + 1);
        Bond testBond = bondList.get(bondList.size() - 1);
        assertThat(testBond.getTradeId()).isEqualTo(DEFAULT_TRADE_ID);
        assertThat(testBond.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testBond.getQuote()).isEqualTo(DEFAULT_QUOTE);
        assertThat(testBond.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testBond.getPnl()).isEqualTo(DEFAULT_PNL);

        // Validate the Bond in Elasticsearch
        Bond bondEs = bondSearchRepository.findOne(testBond.getId());
        assertThat(bondEs).isEqualToComparingFieldByField(testBond);
    }

    @Test
    @Transactional
    public void createBondWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bondRepository.findAll().size();

        // Create the Bond with an existing ID
        bond.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBondMockMvc.perform(post("/api/bonds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bond)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Bond> bondList = bondRepository.findAll();
        assertThat(bondList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTradeIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = bondRepository.findAll().size();
        // set the field null
        bond.setTradeId(null);

        // Create the Bond, which fails.

        restBondMockMvc.perform(post("/api/bonds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bond)))
            .andExpect(status().isBadRequest());

        List<Bond> bondList = bondRepository.findAll();
        assertThat(bondList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSymbolIsRequired() throws Exception {
        int databaseSizeBeforeTest = bondRepository.findAll().size();
        // set the field null
        bond.setSymbol(null);

        // Create the Bond, which fails.

        restBondMockMvc.perform(post("/api/bonds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bond)))
            .andExpect(status().isBadRequest());

        List<Bond> bondList = bondRepository.findAll();
        assertThat(bondList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkQuoteIsRequired() throws Exception {
        int databaseSizeBeforeTest = bondRepository.findAll().size();
        // set the field null
        bond.setQuote(null);

        // Create the Bond, which fails.

        restBondMockMvc.perform(post("/api/bonds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bond)))
            .andExpect(status().isBadRequest());

        List<Bond> bondList = bondRepository.findAll();
        assertThat(bondList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPositionIsRequired() throws Exception {
        int databaseSizeBeforeTest = bondRepository.findAll().size();
        // set the field null
        bond.setPosition(null);

        // Create the Bond, which fails.

        restBondMockMvc.perform(post("/api/bonds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bond)))
            .andExpect(status().isBadRequest());

        List<Bond> bondList = bondRepository.findAll();
        assertThat(bondList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPnlIsRequired() throws Exception {
        int databaseSizeBeforeTest = bondRepository.findAll().size();
        // set the field null
        bond.setPnl(null);

        // Create the Bond, which fails.

        restBondMockMvc.perform(post("/api/bonds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bond)))
            .andExpect(status().isBadRequest());

        List<Bond> bondList = bondRepository.findAll();
        assertThat(bondList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBonds() throws Exception {
        // Initialize the database
        bondRepository.saveAndFlush(bond);

        // Get all the bondList
        restBondMockMvc.perform(get("/api/bonds?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bond.getId().intValue())))
            .andExpect(jsonPath("$.[*].tradeId").value(hasItem(DEFAULT_TRADE_ID.intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL.toString())))
            .andExpect(jsonPath("$.[*].quote").value(hasItem(DEFAULT_QUOTE.doubleValue())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION.intValue())))
            .andExpect(jsonPath("$.[*].pnl").value(hasItem(DEFAULT_PNL.intValue())));
    }

    @Test
    @Transactional
    public void getBond() throws Exception {
        // Initialize the database
        bondRepository.saveAndFlush(bond);

        // Get the bond
        restBondMockMvc.perform(get("/api/bonds/{id}", bond.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bond.getId().intValue()))
            .andExpect(jsonPath("$.tradeId").value(DEFAULT_TRADE_ID.intValue()))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL.toString()))
            .andExpect(jsonPath("$.quote").value(DEFAULT_QUOTE.doubleValue()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION.intValue()))
            .andExpect(jsonPath("$.pnl").value(DEFAULT_PNL.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingBond() throws Exception {
        // Get the bond
        restBondMockMvc.perform(get("/api/bonds/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBond() throws Exception {
        // Initialize the database
        bondRepository.saveAndFlush(bond);
        bondSearchRepository.save(bond);
        int databaseSizeBeforeUpdate = bondRepository.findAll().size();

        // Update the bond
        Bond updatedBond = bondRepository.findOne(bond.getId());
        updatedBond
            .tradeId(UPDATED_TRADE_ID)
            .symbol(UPDATED_SYMBOL)
            .quote(UPDATED_QUOTE)
            .position(UPDATED_POSITION)
            .pnl(UPDATED_PNL);

        restBondMockMvc.perform(put("/api/bonds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBond)))
            .andExpect(status().isOk());

        // Validate the Bond in the database
        List<Bond> bondList = bondRepository.findAll();
        assertThat(bondList).hasSize(databaseSizeBeforeUpdate);
        Bond testBond = bondList.get(bondList.size() - 1);
        assertThat(testBond.getTradeId()).isEqualTo(UPDATED_TRADE_ID);
        assertThat(testBond.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testBond.getQuote()).isEqualTo(UPDATED_QUOTE);
        assertThat(testBond.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testBond.getPnl()).isEqualTo(UPDATED_PNL);

        // Validate the Bond in Elasticsearch
        Bond bondEs = bondSearchRepository.findOne(testBond.getId());
        assertThat(bondEs).isEqualToComparingFieldByField(testBond);
    }

    @Test
    @Transactional
    public void updateNonExistingBond() throws Exception {
        int databaseSizeBeforeUpdate = bondRepository.findAll().size();

        // Create the Bond

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBondMockMvc.perform(put("/api/bonds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bond)))
            .andExpect(status().isCreated());

        // Validate the Bond in the database
        List<Bond> bondList = bondRepository.findAll();
        assertThat(bondList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBond() throws Exception {
        // Initialize the database
        bondRepository.saveAndFlush(bond);
        bondSearchRepository.save(bond);
        int databaseSizeBeforeDelete = bondRepository.findAll().size();

        // Get the bond
        restBondMockMvc.perform(delete("/api/bonds/{id}", bond.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean bondExistsInEs = bondSearchRepository.exists(bond.getId());
        assertThat(bondExistsInEs).isFalse();

        // Validate the database is empty
        List<Bond> bondList = bondRepository.findAll();
        assertThat(bondList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBond() throws Exception {
        // Initialize the database
        bondRepository.saveAndFlush(bond);
        bondSearchRepository.save(bond);

        // Search the bond
        restBondMockMvc.perform(get("/api/_search/bonds?query=id:" + bond.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bond.getId().intValue())))
            .andExpect(jsonPath("$.[*].tradeId").value(hasItem(DEFAULT_TRADE_ID.intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL.toString())))
            .andExpect(jsonPath("$.[*].quote").value(hasItem(DEFAULT_QUOTE.doubleValue())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION.intValue())))
            .andExpect(jsonPath("$.[*].pnl").value(hasItem(DEFAULT_PNL.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bond.class);
        Bond bond1 = new Bond();
        bond1.setId(1L);
        Bond bond2 = new Bond();
        bond2.setId(bond1.getId());
        assertThat(bond1).isEqualTo(bond2);
        bond2.setId(2L);
        assertThat(bond1).isNotEqualTo(bond2);
        bond1.setId(null);
        assertThat(bond1).isNotEqualTo(bond2);
    }
}
