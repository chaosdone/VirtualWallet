package com.service.UserServiceImpl;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.PrimaryAccountDao;
import com.dao.SavingsAccountDao;
import com.exception.NoSufficientBalanceException;
import com.model.PrimaryAccount;
import com.model.PrimaryTransaction;
import com.model.SavingsAccount;
import com.model.SavingsTransaction;
import com.model.User;
import com.service.AccountService;
import com.service.TransactionService;
import com.service.UserService;

@Service
public class AccountServiceImpl implements AccountService {
	
	private static int nextAccountNumber = 11223145;

    @Autowired
    private PrimaryAccountDao primaryAccountDao;

    @Autowired
    private SavingsAccountDao savingsAccountDao;

    @Autowired
    private UserService userService;
    
    @Autowired
    private TransactionService transactionService;

    public PrimaryAccount createPrimaryAccount() {
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal(0.0));
        primaryAccount.setAccountNumber(accountGen());

        primaryAccountDao.save(primaryAccount);

        return primaryAccountDao.findByAccountNumber(primaryAccount.getAccountNumber());
    }

    public SavingsAccount createSavingsAccount() {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal(0.0));
        savingsAccount.setAccountNumber(accountGen());

        savingsAccountDao.save(savingsAccount);

        return savingsAccountDao.findByAccountNumber(savingsAccount.getAccountNumber());
    }

    public void deposit(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Deposit to Primary Account", "Account", 
            		"Success", amount, primaryAccount.getAccountBalance(), primaryAccount);
            transactionService.savePrimaryDepositTransaction(primaryTransaction);
            
        } else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();
            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Deposit to savings Account", "Account", 
            		"Success", amount, savingsAccount.getAccountBalance(), savingsAccount);
            transactionService.saveSavingsDepositTransaction(savingsTransaction);
        }
    }
    
    public void withdraw(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            
            try {
            	if(primaryAccount.getAccountBalance().compareTo(new BigDecimal(amount)) < 0) {
            		Date date = new Date();

    	            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Withdraw from Primary Account", "Account", 
    	            		"Fail", amount, primaryAccount.getAccountBalance(), primaryAccount);
    	            transactionService.savePrimaryWithdrawTransaction(primaryTransaction);
    	            
            		throw new NoSufficientBalanceException("Cannot withdraw money over balance amount");
            	} else {
            		primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
    	            primaryAccountDao.save(primaryAccount);
    	            
    	            Date date = new Date();

    	            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Withdraw from Primary Account", "Account", 
    	            		"Success", amount, primaryAccount.getAccountBalance(), primaryAccount);
    	            transactionService.savePrimaryWithdrawTransaction(primaryTransaction);
            	}
            } catch (NoSufficientBalanceException e) {
            	e.printStackTrace();
            }
        } else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            
            try{
            	 if(savingsAccount.getAccountBalance().compareTo(new BigDecimal(amount)) < 0) {
                	Date date = new Date();

                	SavingsTransaction primaryTransaction = new SavingsTransaction(date, "Withdraw from Savings Account", "Account", 
        	            		"Fail", amount, savingsAccount.getAccountBalance(), savingsAccount);
        	        transactionService.saveSavingsWithdrawTransaction(primaryTransaction);
        	            
                	throw new NoSufficientBalanceException("Cannot withdraw money over balance amount");
            	 } else {
            		 savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
     	             savingsAccountDao.save(savingsAccount);
     	            
     	             Date date = new Date();

     	             SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Withdraw from Savings Account", "Account", 
     	            		"Success", amount, savingsAccount.getAccountBalance(), savingsAccount);
     	             transactionService.saveSavingsWithdrawTransaction(savingsTransaction);
            	 }
            } catch (NoSufficientBalanceException e) {
            	e.printStackTrace();
            }
        }
    }
    
    private int accountGen() {
        return ++nextAccountNumber;
    }

}
