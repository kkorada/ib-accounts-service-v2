package com.abcbank.ib.web.rest;

import com.abcbank.ib.AccountsserviceApp;
import com.abcbank.ib.domain.BankingAccount;
import com.abcbank.ib.repository.BankingAccountRepository;
import com.abcbank.ib.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.abcbank.ib.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link BankingAccountResource} REST controller.
 */
@SpringBootTest(classes = AccountsserviceApp.class)
public class BankingAccountResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CUSTOMER_ID = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_NUMBER = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BALANCE = new BigDecimal(2);

    private static final LocalDate DEFAULT_DATE_OF_CREATION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_CREATION = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    @Autowired
    private BankingAccountRepository bankingAccountRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restBankingAccountMockMvc;

    private BankingAccount bankingAccount;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BankingAccountResource bankingAccountResource = new BankingAccountResource(bankingAccountRepository);
        this.restBankingAccountMockMvc = MockMvcBuilders.standaloneSetup(bankingAccountResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BankingAccount createEntity(EntityManager em) {
        BankingAccount bankingAccount = new BankingAccount()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .customerId(DEFAULT_CUSTOMER_ID)
            .number(DEFAULT_NUMBER)
            .balance(DEFAULT_BALANCE)
            .dateOfCreation(DEFAULT_DATE_OF_CREATION)
            .status(DEFAULT_STATUS);
        return bankingAccount;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BankingAccount createUpdatedEntity(EntityManager em) {
        BankingAccount bankingAccount = new BankingAccount()
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .customerId(UPDATED_CUSTOMER_ID)
            .number(UPDATED_NUMBER)
            .balance(UPDATED_BALANCE)
            .dateOfCreation(UPDATED_DATE_OF_CREATION)
            .status(UPDATED_STATUS);
        return bankingAccount;
    }

    @BeforeEach
    public void initTest() {
        bankingAccount = createEntity(em);
    }

    @Test
    @Transactional
    public void createBankingAccount() throws Exception {
        int databaseSizeBeforeCreate = bankingAccountRepository.findAll().size();

        // Create the BankingAccount
        restBankingAccountMockMvc.perform(post("/api/banking-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bankingAccount)))
            .andExpect(status().isCreated());

        // Validate the BankingAccount in the database
        List<BankingAccount> bankingAccountList = bankingAccountRepository.findAll();
        assertThat(bankingAccountList).hasSize(databaseSizeBeforeCreate + 1);
        BankingAccount testBankingAccount = bankingAccountList.get(bankingAccountList.size() - 1);
        assertThat(testBankingAccount.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBankingAccount.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testBankingAccount.getCustomerId()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testBankingAccount.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testBankingAccount.getBalance()).isEqualTo(DEFAULT_BALANCE);
        assertThat(testBankingAccount.getDateOfCreation()).isEqualTo(DEFAULT_DATE_OF_CREATION);
        assertThat(testBankingAccount.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createBankingAccountWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bankingAccountRepository.findAll().size();

        // Create the BankingAccount with an existing ID
        bankingAccount.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBankingAccountMockMvc.perform(post("/api/banking-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bankingAccount)))
            .andExpect(status().isBadRequest());

        // Validate the BankingAccount in the database
        List<BankingAccount> bankingAccountList = bankingAccountRepository.findAll();
        assertThat(bankingAccountList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllBankingAccounts() throws Exception {
        // Initialize the database
        bankingAccountRepository.saveAndFlush(bankingAccount);

        // Get all the bankingAccountList
        restBankingAccountMockMvc.perform(get("/api/banking-accounts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bankingAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID)))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].balance").value(hasItem(DEFAULT_BALANCE.intValue())))
            .andExpect(jsonPath("$.[*].dateOfCreation").value(hasItem(DEFAULT_DATE_OF_CREATION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }
    
    @Test
    @Transactional
    public void getBankingAccount() throws Exception {
        // Initialize the database
        bankingAccountRepository.saveAndFlush(bankingAccount);

        // Get the bankingAccount
        restBankingAccountMockMvc.perform(get("/api/banking-accounts/{id}", bankingAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bankingAccount.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.customerId").value(DEFAULT_CUSTOMER_ID))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER))
            .andExpect(jsonPath("$.balance").value(DEFAULT_BALANCE.intValue()))
            .andExpect(jsonPath("$.dateOfCreation").value(DEFAULT_DATE_OF_CREATION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    public void getNonExistingBankingAccount() throws Exception {
        // Get the bankingAccount
        restBankingAccountMockMvc.perform(get("/api/banking-accounts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBankingAccount() throws Exception {
        // Initialize the database
        bankingAccountRepository.saveAndFlush(bankingAccount);

        int databaseSizeBeforeUpdate = bankingAccountRepository.findAll().size();

        // Update the bankingAccount
        BankingAccount updatedBankingAccount = bankingAccountRepository.findById(bankingAccount.getId()).get();
        // Disconnect from session so that the updates on updatedBankingAccount are not directly saved in db
        em.detach(updatedBankingAccount);
        updatedBankingAccount
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .customerId(UPDATED_CUSTOMER_ID)
            .number(UPDATED_NUMBER)
            .balance(UPDATED_BALANCE)
            .dateOfCreation(UPDATED_DATE_OF_CREATION)
            .status(UPDATED_STATUS);

        restBankingAccountMockMvc.perform(put("/api/banking-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBankingAccount)))
            .andExpect(status().isOk());

        // Validate the BankingAccount in the database
        List<BankingAccount> bankingAccountList = bankingAccountRepository.findAll();
        assertThat(bankingAccountList).hasSize(databaseSizeBeforeUpdate);
        BankingAccount testBankingAccount = bankingAccountList.get(bankingAccountList.size() - 1);
        assertThat(testBankingAccount.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBankingAccount.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBankingAccount.getCustomerId()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testBankingAccount.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testBankingAccount.getBalance()).isEqualTo(UPDATED_BALANCE);
        assertThat(testBankingAccount.getDateOfCreation()).isEqualTo(UPDATED_DATE_OF_CREATION);
        assertThat(testBankingAccount.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingBankingAccount() throws Exception {
        int databaseSizeBeforeUpdate = bankingAccountRepository.findAll().size();

        // Create the BankingAccount

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBankingAccountMockMvc.perform(put("/api/banking-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bankingAccount)))
            .andExpect(status().isBadRequest());

        // Validate the BankingAccount in the database
        List<BankingAccount> bankingAccountList = bankingAccountRepository.findAll();
        assertThat(bankingAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBankingAccount() throws Exception {
        // Initialize the database
        bankingAccountRepository.saveAndFlush(bankingAccount);

        int databaseSizeBeforeDelete = bankingAccountRepository.findAll().size();

        // Delete the bankingAccount
        restBankingAccountMockMvc.perform(delete("/api/banking-accounts/{id}", bankingAccount.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BankingAccount> bankingAccountList = bankingAccountRepository.findAll();
        assertThat(bankingAccountList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BankingAccount.class);
        BankingAccount bankingAccount1 = new BankingAccount();
        bankingAccount1.setId(1L);
        BankingAccount bankingAccount2 = new BankingAccount();
        bankingAccount2.setId(bankingAccount1.getId());
        assertThat(bankingAccount1).isEqualTo(bankingAccount2);
        bankingAccount2.setId(2L);
        assertThat(bankingAccount1).isNotEqualTo(bankingAccount2);
        bankingAccount1.setId(null);
        assertThat(bankingAccount1).isNotEqualTo(bankingAccount2);
    }
}
