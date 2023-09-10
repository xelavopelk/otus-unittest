package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AccountServiceTest {

    AccountDao accountDao;

    AccountServiceImpl accountServiceImpl;

    @BeforeEach
    void init() {
        accountDao = mock(AccountDao.class);
        accountServiceImpl = new AccountServiceImpl(accountDao);
    }

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher
        when(accountDao.saveAccount(argThat(argument -> argument.getId()==0))).thenReturn(
                new Account(100L, new BigDecimal("200.0")));
        Account acc = accountServiceImpl.createAccount(new BigDecimal("200.0"));
        assertEquals(100L, acc.getId());
        assertEquals(new BigDecimal("200.0"), acc.getAmount());

    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        when(accountDao.saveAccount(argThat(argument -> argument.getId()==0))).thenReturn(
                new Account(100L, new BigDecimal("200.0")));

        when(accountDao.getAccount(idCaptor.capture())).thenReturn(new Account(100L, new BigDecimal("200.0")));

        accountServiceImpl.createAccount(new BigDecimal("200.0"));
        accountServiceImpl.getAccount(100L);

        assertEquals(100L, idCaptor.getValue());
    }

    @Test
    void addSum() {
        var accId=5L;
        var accSum="200.0";
        var putSum=new BigDecimal("50.0");
        when(accountDao.getAccount(accId)).thenReturn(new Account(accId, new BigDecimal(accSum)));
        var bal = accountServiceImpl.putMoney(accId, putSum);
        assertEquals(new BigDecimal("250.0"), bal);
    }

    @Test
    void getSum() {
        var accId=5L;
        var accSum="200.0";
        var getSum=new BigDecimal("50.0");
        when(accountDao.getAccount(accId)).thenReturn(new Account(accId, new BigDecimal(accSum)));
        var bal = accountServiceImpl.getMoney(accId, getSum);
        assertEquals(new BigDecimal("150.0"), bal);
    }

    @Test
    void getAccount() {
        var accId=5L;
        var accSum="200.0";
        when(accountDao.getAccount(accId)).thenReturn(new Account(accId, new BigDecimal(accSum)));
        Account acc = accountServiceImpl.getAccount(accId);
        assertEquals(accId, acc.getId());
        assertEquals(new BigDecimal(accSum), acc.getAmount());
    }

    @Test
    void checkBalance() {
        var accId=5L;
        var accSum="200.0";
        when(accountDao.getAccount(accId)).thenReturn(new Account(accId, new BigDecimal(accSum)));
        var bal = accountServiceImpl.checkBalance(accId);
        assertEquals(new BigDecimal(accSum), bal);
    }
}
