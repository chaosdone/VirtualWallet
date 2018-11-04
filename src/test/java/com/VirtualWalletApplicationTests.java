package com;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.security.Principal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.dao.PrimaryAccountDao;
import com.dao.SavingsAccountDao;
import com.service.AccountService;
import com.service.TransactionService;
import com.service.UserService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class VirtualWalletApplicationTests {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private PrimaryAccountDao primaryAccountDao;
	
	@Autowired
	private SavingsAccountDao savingsAccountDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TransactionService transactionService;
	
	Principal mockp = mock(Principal.class);
	
	@Test
	public void contextLoads() {
	}
	
	@Before
	public void clear() {
		Mockito.when(mockp.getName()).thenReturn("test1");
		accountService.withdraw("primary", userService.findByUsername("test1").getPrimaryAccount().getAccountBalance().doubleValue(), mockp);
		accountService.withdraw("savings", userService.findByUsername("test1").getSavingsAccount().getAccountBalance().doubleValue(), mockp);
	}
	
	@Test
	public void testDepositToPrimary() {
		accountService.deposit("primary", 10.00, mockp);	
		assertEquals(new BigDecimal(10).setScale(2), userService.findByUsername("test1").getPrimaryAccount().getAccountBalance());
	}
	
	@Test
	public void testWithdrawFromSavings() {
		accountService.deposit("savings", 10.00, mockp);	
		accountService.withdraw("savings", 5.00, mockp);
		assertEquals(new BigDecimal(5).setScale(2), userService.findByUsername("test1").getSavingsAccount().getAccountBalance());
	}

	@Test
	public void testTransacitonFromAccountToAccount() {
		try {
			accountService.deposit("primary", 10.00, mockp);
			transactionService.betweenAccountsTransfer("primary", "savings", "10.00", primaryAccountDao.findByAccountNumber(11223146), 
					savingsAccountDao.findByAccountNumber(11223147));
			assertEquals(new BigDecimal(0).setScale(2), userService.findByUsername("test1").getPrimaryAccount().getAccountBalance());
			assertEquals(new BigDecimal(10).setScale(2), userService.findByUsername("test1").getSavingsAccount().getAccountBalance());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}