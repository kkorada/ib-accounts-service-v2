package com.abcbank.ib.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abcbank.ib.domain.BankingAccount;


/**
 * Spring Data  repository for the BankingAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BankingAccountRepository extends JpaRepository<BankingAccount, Long> {

	List<BankingAccount> findByCustomerId(String customerId);

}
