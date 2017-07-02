package com.ubs.dataveri.web.rest;

import com.ubs.dataveri.DataVeriApp;

import com.ubs.dataveri.domain.Trader;
import com.ubs.dataveri.domain.User;
import com.ubs.dataveri.repository.TraderRepository;
import com.ubs.dataveri.repository.search.TraderSearchRepository;
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
 * Test class for the TraderResource REST controller.
 *
 * @see TraderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataVeriApp.class)
public class TraderResourceIntTest {

    @Autowired
    private TraderRepository traderRepository;

    @Autowired
    private TraderSearchRepository traderSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTraderMockMvc;

    private Trader trader;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TraderResource traderResource = new TraderResource(traderRepository, traderSearchRepository);
        this.restTraderMockMvc = MockMvcBuilders.standaloneSetup(traderResource)
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
    public static Trader createEntity(EntityManager em) {
        Trader trader = new Trader();
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        trader.setUser(user);
        return trader;
    }

    @Before
    public void initTest() {
        traderSearchRepository.deleteAll();
        trader = createEntity(em);
    }

    @Test
    @Transactional
    public void createTrader() throws Exception {
        int databaseSizeBeforeCreate = traderRepository.findAll().size();

        // Create the Trader
        restTraderMockMvc.perform(post("/api/traders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(trader)))
            .andExpect(status().isCreated());

        // Validate the Trader in the database
        List<Trader> traderList = traderRepository.findAll();
        assertThat(traderList).hasSize(databaseSizeBeforeCreate + 1);
        Trader testTrader = traderList.get(traderList.size() - 1);

        // Validate the Trader in Elasticsearch
        Trader traderEs = traderSearchRepository.findOne(testTrader.getId());
        assertThat(traderEs).isEqualToComparingFieldByField(testTrader);
    }

    @Test
    @Transactional
    public void createTraderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = traderRepository.findAll().size();

        // Create the Trader with an existing ID
        trader.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTraderMockMvc.perform(post("/api/traders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(trader)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Trader> traderList = traderRepository.findAll();
        assertThat(traderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllTraders() throws Exception {
        // Initialize the database
        traderRepository.saveAndFlush(trader);

        // Get all the traderList
        restTraderMockMvc.perform(get("/api/traders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trader.getId().intValue())));
    }

    @Test
    @Transactional
    public void getTrader() throws Exception {
        // Initialize the database
        traderRepository.saveAndFlush(trader);

        // Get the trader
        restTraderMockMvc.perform(get("/api/traders/{id}", trader.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(trader.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTrader() throws Exception {
        // Get the trader
        restTraderMockMvc.perform(get("/api/traders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTrader() throws Exception {
        // Initialize the database
        traderRepository.saveAndFlush(trader);
        traderSearchRepository.save(trader);
        int databaseSizeBeforeUpdate = traderRepository.findAll().size();

        // Update the trader
        Trader updatedTrader = traderRepository.findOne(trader.getId());

        restTraderMockMvc.perform(put("/api/traders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTrader)))
            .andExpect(status().isOk());

        // Validate the Trader in the database
        List<Trader> traderList = traderRepository.findAll();
        assertThat(traderList).hasSize(databaseSizeBeforeUpdate);
        Trader testTrader = traderList.get(traderList.size() - 1);

        // Validate the Trader in Elasticsearch
        Trader traderEs = traderSearchRepository.findOne(testTrader.getId());
        assertThat(traderEs).isEqualToComparingFieldByField(testTrader);
    }

    @Test
    @Transactional
    public void updateNonExistingTrader() throws Exception {
        int databaseSizeBeforeUpdate = traderRepository.findAll().size();

        // Create the Trader

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTraderMockMvc.perform(put("/api/traders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(trader)))
            .andExpect(status().isCreated());

        // Validate the Trader in the database
        List<Trader> traderList = traderRepository.findAll();
        assertThat(traderList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTrader() throws Exception {
        // Initialize the database
        traderRepository.saveAndFlush(trader);
        traderSearchRepository.save(trader);
        int databaseSizeBeforeDelete = traderRepository.findAll().size();

        // Get the trader
        restTraderMockMvc.perform(delete("/api/traders/{id}", trader.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean traderExistsInEs = traderSearchRepository.exists(trader.getId());
        assertThat(traderExistsInEs).isFalse();

        // Validate the database is empty
        List<Trader> traderList = traderRepository.findAll();
        assertThat(traderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTrader() throws Exception {
        // Initialize the database
        traderRepository.saveAndFlush(trader);
        traderSearchRepository.save(trader);

        // Search the trader
        restTraderMockMvc.perform(get("/api/_search/traders?query=id:" + trader.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trader.getId().intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Trader.class);
        Trader trader1 = new Trader();
        trader1.setId(1L);
        Trader trader2 = new Trader();
        trader2.setId(trader1.getId());
        assertThat(trader1).isEqualTo(trader2);
        trader2.setId(2L);
        assertThat(trader1).isNotEqualTo(trader2);
        trader1.setId(null);
        assertThat(trader1).isNotEqualTo(trader2);
    }
}
