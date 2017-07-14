package com.ubs.dataveri.web.rest;

import com.ubs.dataveri.DataVeriApp;

import com.ubs.dataveri.domain.Transaction;
import com.ubs.dataveri.domain.Trader;
import com.ubs.dataveri.repository.TransactionRepository;
import com.ubs.dataveri.repository.search.TransactionSearchRepository;
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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.ubs.dataveri.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ubs.dataveri.domain.enumeration.ProductType;
import com.ubs.dataveri.domain.enumeration.TransactionType;
/**
 * Test class for the TransactionResource REST controller.
 *
 * @see TransactionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataVeriApp.class)
public class TransactionResourceIntTest {

    private static final Long DEFAULT_TRADE_ID = 1L;
    private static final Long UPDATED_TRADE_ID = 2L;

    private static final ZonedDateTime DEFAULT_TRADE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TRADE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final ProductType DEFAULT_PRODUCT = ProductType.Stock;
    private static final ProductType UPDATED_PRODUCT = ProductType.Bond;

    private static final TransactionType DEFAULT_TYPE = TransactionType.Put;
    private static final TransactionType UPDATED_TYPE = TransactionType.Call;

    private static final Double DEFAULT_STRIKE_PRICE = 1D;
    private static final Double UPDATED_STRIKE_PRICE = 2D;

    private static final BigDecimal DEFAULT_CASH = new BigDecimal(1);
    private static final BigDecimal UPDATED_CASH = new BigDecimal(2);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionSearchRepository transactionSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TransactionResource transactionResource = new TransactionResource(transactionRepository, transactionSearchRepository);
        this.restTransactionMockMvc = MockMvcBuilders.standaloneSetup(transactionResource)
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
    public static Transaction createEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .tradeId(DEFAULT_TRADE_ID)
            .tradeTime(DEFAULT_TRADE_TIME)
            .symbol(DEFAULT_SYMBOL)
            .product(DEFAULT_PRODUCT)
            .type(DEFAULT_TYPE)
            .strikePrice(DEFAULT_STRIKE_PRICE)
            .cash(DEFAULT_CASH);
        // Add required entity
        Trader trader = TraderResourceIntTest.createEntity(em);
        em.persist(trader);
        em.flush();
        transaction.setTrader(trader);
        return transaction;
    }

    @Before
    public void initTest() {
        transactionSearchRepository.deleteAll();
        transaction = createEntity(em);
    }

    @Test
    @Transactional
    public void createTransaction() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTradeId()).isEqualTo(DEFAULT_TRADE_ID);
        assertThat(testTransaction.getTradeTime()).isEqualTo(DEFAULT_TRADE_TIME);
        assertThat(testTransaction.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testTransaction.getProduct()).isEqualTo(DEFAULT_PRODUCT);
        assertThat(testTransaction.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTransaction.getStrikePrice()).isEqualTo(DEFAULT_STRIKE_PRICE);
        assertThat(testTransaction.getCash()).isEqualTo(DEFAULT_CASH);

        // Validate the Transaction in Elasticsearch
        Transaction transactionEs = transactionSearchRepository.findOne(testTransaction.getId());
        assertThat(transactionEs).isEqualToComparingFieldByField(testTransaction);
    }

    @Test
    @Transactional
    public void createTransactionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction with an existing ID
        transaction.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTradeIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setTradeId(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTradeTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setTradeTime(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSymbolIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setSymbol(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkProductIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setProduct(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setType(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStrikePriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setStrikePrice(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTransactions() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList
        restTransactionMockMvc.perform(get("/api/transactions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].tradeId").value(hasItem(DEFAULT_TRADE_ID.intValue())))
            .andExpect(jsonPath("$.[*].tradeTime").value(hasItem(sameInstant(DEFAULT_TRADE_TIME))))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL.toString())))
            .andExpect(jsonPath("$.[*].product").value(hasItem(DEFAULT_PRODUCT.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].strikePrice").value(hasItem(DEFAULT_STRIKE_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].cash").value(hasItem(DEFAULT_CASH.intValue())));
    }

    @Test
    @Transactional
    public void getTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc.perform(get("/api/transactions/{id}", transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.tradeId").value(DEFAULT_TRADE_ID.intValue()))
            .andExpect(jsonPath("$.tradeTime").value(sameInstant(DEFAULT_TRADE_TIME)))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL.toString()))
            .andExpect(jsonPath("$.product").value(DEFAULT_PRODUCT.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.strikePrice").value(DEFAULT_STRIKE_PRICE.doubleValue()))
            .andExpect(jsonPath("$.cash").value(DEFAULT_CASH.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get("/api/transactions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);
        transactionSearchRepository.save(transaction);
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findOne(transaction.getId());
        updatedTransaction
            .tradeId(UPDATED_TRADE_ID)
            .tradeTime(UPDATED_TRADE_TIME)
            .symbol(UPDATED_SYMBOL)
            .product(UPDATED_PRODUCT)
            .type(UPDATED_TYPE)
            .strikePrice(UPDATED_STRIKE_PRICE)
            .cash(UPDATED_CASH);

        restTransactionMockMvc.perform(put("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTransaction)))
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTradeId()).isEqualTo(UPDATED_TRADE_ID);
        assertThat(testTransaction.getTradeTime()).isEqualTo(UPDATED_TRADE_TIME);
        assertThat(testTransaction.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testTransaction.getProduct()).isEqualTo(UPDATED_PRODUCT);
        assertThat(testTransaction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTransaction.getStrikePrice()).isEqualTo(UPDATED_STRIKE_PRICE);
        assertThat(testTransaction.getCash()).isEqualTo(UPDATED_CASH);

        // Validate the Transaction in Elasticsearch
        Transaction transactionEs = transactionSearchRepository.findOne(testTransaction.getId());
        assertThat(transactionEs).isEqualToComparingFieldByField(testTransaction);
    }

    @Test
    @Transactional
    public void updateNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Create the Transaction

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTransactionMockMvc.perform(put("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);
        transactionSearchRepository.save(transaction);
        int databaseSizeBeforeDelete = transactionRepository.findAll().size();

        // Get the transaction
        restTransactionMockMvc.perform(delete("/api/transactions/{id}", transaction.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean transactionExistsInEs = transactionSearchRepository.exists(transaction.getId());
        assertThat(transactionExistsInEs).isFalse();

        // Validate the database is empty
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);
        transactionSearchRepository.save(transaction);

        // Search the transaction
        restTransactionMockMvc.perform(get("/api/_search/transactions?query=id:" + transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].tradeId").value(hasItem(DEFAULT_TRADE_ID.intValue())))
            .andExpect(jsonPath("$.[*].tradeTime").value(hasItem(sameInstant(DEFAULT_TRADE_TIME))))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL.toString())))
            .andExpect(jsonPath("$.[*].product").value(hasItem(DEFAULT_PRODUCT.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].strikePrice").value(hasItem(DEFAULT_STRIKE_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].cash").value(hasItem(DEFAULT_CASH.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Transaction.class);
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        Transaction transaction2 = new Transaction();
        transaction2.setId(transaction1.getId());
        assertThat(transaction1).isEqualTo(transaction2);
        transaction2.setId(2L);
        assertThat(transaction1).isNotEqualTo(transaction2);
        transaction1.setId(null);
        assertThat(transaction1).isNotEqualTo(transaction2);
    }
}
