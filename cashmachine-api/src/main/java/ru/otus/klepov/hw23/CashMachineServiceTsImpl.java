package ru.otus.klepov.hw23;

import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.service.CashMachineService;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class CashMachineServiceTsImpl implements CashMachineService {
    private CashMachineService toWrap;
    private ReentrantLock lock = new ReentrantLock();
    public CashMachineServiceTsImpl(CashMachineService toWrap) {
        this.toWrap=toWrap;
    }
    @Override
    public List<Integer> getMoney(CashMachine machine, String cardNum, String pin, BigDecimal amount) {
        lock.lock();
        try {
            var res = this.toWrap.getMoney(machine, cardNum, pin, amount);
            lock.unlock();
            return res;
        }
        catch (Exception ex) {
            lock.unlock();
            throw ex;
        }
    }

    @Override
    public BigDecimal putMoney(CashMachine machine, String cardNum, String pin, List<Integer> notes) {
        lock.lock();
        try {
            var res = this.toWrap.putMoney(machine,cardNum,pin,notes);
            lock.unlock();
            return res;
        }
        catch (Exception ex) {
            lock.unlock();
            throw ex;
        }
    }

    @Override
    public BigDecimal checkBalance(CashMachine machine, String cardNum, String pin) {
        lock.lock();
        try {
            var res = this.toWrap.checkBalance(machine,cardNum,pin);
            lock.unlock();
            return res;
        }
        catch (Exception ex) {
            lock.unlock();
            throw ex;
        }
    }

    @Override
    public boolean changePin(String cardNum, String oldPin, String newPin) {
        lock.lock();
        try {
            var res = this.toWrap.changePin(cardNum,oldPin,newPin);
            lock.unlock();
            return res;
        }
        catch (Exception ex) {
            lock.unlock();
            throw ex;
        }
    }
}
