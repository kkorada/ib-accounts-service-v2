package com.abcbank.ib.web.rest;

import com.abcbank.ib.domain.BankingAccount;
import com.abcbank.ib.repository.BankingAccountRepository;
import com.abcbank.ib.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.abcbank.ib.domain.BankingAccount}.
 */
@RestController
@RequestMapping("/api")
public class BankingAccountResource {

    private final Logger log = LoggerFactory.getLogger(BankingAccountResource.class);

    private static final String ENTITY_NAME = "bankingAccount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BankingAccountRepository bankingAccountRepository;

    public BankingAccountResource(BankingAccountRepository bankingAccountRepository) {
        this.bankingAccountRepository = bankingAccountRepository;
    }

    /**
     * {@code POST  /banking-accounts} : Create a new bankingAccount.
     *
     * @param bankingAccount the bankingAccount to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bankingAccount, or with status {@code 400 (Bad Request)} if the bankingAccount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/banking-accounts")
    public ResponseEntity<BankingAccount> createBankingAccount(@RequestBody BankingAccount bankingAccount) throws URISyntaxException {
        log.debug("REST request to save BankingAccount : {}", bankingAccount);
        if (bankingAccount.getId() != null) {
            throw new BadRequestAlertException("A new bankingAccount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BankingAccount result = bankingAccountRepository.save(bankingAccount);
        return ResponseEntity.created(new URI("/api/banking-accounts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /banking-accounts} : Updates an existing bankingAccount.
     *
     * @param bankingAccount the bankingAccount to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bankingAccount,
     * or with status {@code 400 (Bad Request)} if the bankingAccount is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bankingAccount couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/banking-accounts")
    public ResponseEntity<BankingAccount> updateBankingAccount(@RequestBody BankingAccount bankingAccount) throws URISyntaxException {
        log.debug("REST request to update BankingAccount : {}", bankingAccount);
        if (bankingAccount.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BankingAccount result = bankingAccountRepository.save(bankingAccount);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bankingAccount.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /banking-accounts} : get all the bankingAccounts.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bankingAccounts in body.
     */
    @GetMapping("/banking-accounts")
    public List<BankingAccount> getAllBankingAccounts() {
        log.debug("REST request to get all BankingAccounts");
        return bankingAccountRepository.findAll();
    }

    /**
     * {@code GET  /banking-accounts/:id} : get the "id" bankingAccount.
     *
     * @param id the id of the bankingAccount to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bankingAccount, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/banking-accounts/{id}")
    public ResponseEntity<BankingAccount> getBankingAccount(@PathVariable Long id) {
        log.debug("REST request to get BankingAccount : {}", id);
        Optional<BankingAccount> bankingAccount = bankingAccountRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(bankingAccount);
    }
    
    /**
     * {@code GET  /banking-accounts/:id} : get the "customerId" bankingAccount.
     *
     * @param id the id of the bankingAccount to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bankingAccount, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/banking-accounts/customer/{customerId}")
    public List<BankingAccount> getBankingAccountByCustomer(@PathVariable String customerId) {
        log.debug("REST request to get BankingAccount by customer : {}", customerId);
        List<BankingAccount> bankingAccounts = bankingAccountRepository.findByCustomerId(customerId);
        return bankingAccounts;
    }

    @GetMapping("/version")
    public String getBankingAccountByCustomer() {
        log.debug("REST request to get version of api");
        return "v2";
    }

    /**
     * {@code DELETE  /banking-accounts/:id} : delete the "id" bankingAccount.
     *
     * @param id the id of the bankingAccount to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/banking-accounts/{id}")
    public ResponseEntity<Void> deleteBankingAccount(@PathVariable Long id) {
        log.debug("REST request to delete BankingAccount : {}", id);
        bankingAccountRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
