package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static otus.study.cashmachine.TestUtil.getHash;

@ExtendWith(MockitoExtension.class)
class CashMachineServiceTest {

    @Spy
    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardsDao cardsDao;

    @Mock
    private AccountService accountService;

    @Mock
    private MoneyBoxService moneyBoxService;

    private CashMachineServiceImpl cashMachineService;

    private CashMachine cashMachine;
    private MoneyBox mb;

    @BeforeEach
    void init() {
        mb = new MoneyBox(100,100,100,100);
        cashMachine = new CashMachine(mb);
        cardsDao = mock(CardsDao.class);
        accountService = mock(AccountService.class);
        moneyBoxService = mock(MoneyBoxService.class);
        cardService = new CardServiceImpl(accountService, cardsDao);
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    void getMoney() {
// @TODO create get money test using spy as mock
        var num = "1234";
        var pin = "0000";
        Card card = new Card(1L, num, 1L, getHash(pin));
        when(cardsDao.getCardByNumber(num)).thenReturn(card);
        when(cardService.getMoney(num,pin,BigDecimal.valueOf(1)))
                .thenReturn(BigDecimal.valueOf(1));
        when(moneyBoxService.getMoney(mb,1)).thenReturn(List.of(1));
        var r = cashMachineService.getMoney(cashMachine,num,pin,BigDecimal.valueOf(1));
        assertEquals(r, List.of(1));
    }

    @Test
    void putMoney() {
        var oldSum=1000;
        var pin = "0000";
        var num = "1234";
        var carId = 1L;
        var accId=1L;
        var addSum = 500;
        Card card = new Card(carId, num, accId, TestUtil.getHash(pin));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);
        when(accountService.putMoney(accId, new BigDecimal(addSum))).thenReturn(new BigDecimal(oldSum+addSum));
        when(cardService.getBalance(num,pin)).thenReturn(BigDecimal.valueOf(2000));
        when(cardService.putMoney(num, pin, BigDecimal.valueOf(500))).thenReturn(BigDecimal.valueOf(oldSum+addSum));
        var sum = cashMachineService.putMoney(cashMachine,num, pin,List.of(0,0,1,0));
        assertEquals(0, sum.compareTo(BigDecimal.valueOf(1500)));


    }

    @Test
    void checkBalance() {
        var pin = "0000";
        var num = "1234";
        var carId = 1L;
        var accId=1L;
        Card card = new Card(carId, num, accId, TestUtil.getHash(pin));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);
        when(cardService.getBalance(num,pin)).thenReturn(BigDecimal.valueOf(2000));

        var sum = cashMachineService.checkBalance(cashMachine,num, pin);
        assertEquals(0, sum.compareTo(BigDecimal.valueOf(2000)));

    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        ArgumentCaptor<Card> pinCaptor = ArgumentCaptor.forClass(Card.class);
        var newPin="0001";
        var pin = "0000";
        var num = "1234";
        var carId = 1L;
        var accId=1L;
        Card card = new Card(carId, num, accId, getHash(pin));
        when(cardsDao.getCardByNumber(num)).thenReturn(card);
        when(cardsDao.saveCard(pinCaptor.capture())).thenReturn(card);

        cardService.cnangePin(num, pin, "0001");
        assertEquals(getHash(newPin), pinCaptor.getValue().getPinCode());

    }
    private Card createCard(String pinCode) {
        var num = "1234";
        var carId = 1L;
        var accId=1L;
        return new Card(carId, num, accId, getHash(pinCode));
    }
    @Test
    void changePinWithAnswer() {
// @TODO create change pin test using spy as implementation and mock an thenAnswer
        var newPin="0001";
        var answer = new Answer<Card>() {
            @Override
            public Card answer(InvocationOnMock invocation) throws Throwable
            {
                return createCard(newPin);
            }
        };


        ArgumentCaptor<Card> pinCaptor = ArgumentCaptor.forClass(Card.class);
        var old = createCard("0000");
        when(cardsDao.getCardByNumber(old.getNumber())).thenReturn(old);
        when(cardsDao.saveCard(pinCaptor.capture())).thenAnswer(answer);

        cardService.cnangePin(old.getNumber(), old.getPinCode(), newPin);

        verify(cardsDao, only()).saveCard(any());
        assertEquals(getHash(newPin), pinCaptor.getValue().getPinCode());
    }
}