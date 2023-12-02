package ru.otus.klepov.hw23;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;
import otus.study.cashmachine.machine.service.impl.MoneyBoxServiceImpl;
import ru.otus.klepov.hw23.FileDTO.Cards;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class CashMachineServiceFactory {
    public CashMachineServiceImpl create() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("DbCards.json");
        String result = CharStreams.toString(new InputStreamReader(
                is, Charsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();
        Cards cards = mapper.readValue(result, Cards.class);

        var accountDao = new AccountDao();
        var cardsDao = new CardsDao();
        var accountService = new AccountServiceImpl(accountDao);
        for (var crd : cards.cards) {
            var acc = accountService.createAccount(crd.accountId);
            cardsDao.createCard(crd.num, acc.getId(), Utils.getHash(crd.pin));
        }

        var cardService = new CardServiceImpl(accountService, cardsDao);
        var moneyBoxService = new MoneyBoxServiceImpl();
        var cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
        return cashMachineService;
    }
}
