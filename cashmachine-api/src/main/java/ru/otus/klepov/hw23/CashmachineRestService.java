package ru.otus.klepov.hw23;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import jakarta.json.*;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.service.CashMachineService;
import ru.otus.klepov.hw23.RestDTO.BalanceRequest;
import ru.otus.klepov.hw23.RestDTO.ChangePinRequest;
import ru.otus.klepov.hw23.RestDTO.GetMoneyRequest;
import ru.otus.klepov.hw23.RestDTO.PutMoneyRequest;

import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;
import java.util.Collections;

public class CashmachineRestService implements Service {
    private static final int BANKNOTE_NOM_COUNT = 4;
    private CashMachineService srv;
    private CashMachine machine;

    public CashmachineRestService(CashMachineService srv, CashMachine machine) {
        this.srv = srv;
        this.machine = machine;
    }

    @Override
    public void update(Routing.Rules rules) {
        rules
                .post("/balance", this::balance)
                .post("/change-pin", this::changePin)
                .post("/get-money", this::getMoney)
                .post("/put-money", this::putMoney);
    }

    private void balance(ServerRequest request, ServerResponse response) {
        request.content()
                .as(BalanceRequest.class)
                .thenAccept(r -> {
                    var res = srv.checkBalance(machine, r.getCardNum(), r.getPin());
                    response.send(res.toString());
                })
                .exceptionally(response::send);
    }

    private void changePin(ServerRequest request, ServerResponse response) {
        request.content().as(ChangePinRequest.class)
                .thenAccept(r -> {
                    var res = srv.changePin(r.getCardNum(), r.getOldPin(), r.getNewPin());
                    response.send(String.valueOf(res));
                })
                .exceptionally(response::send);
    }

    private void getMoney(ServerRequest request, ServerResponse response) {
        request.content().as(GetMoneyRequest.class)
                .thenAccept(r -> {
                    var res = srv.getMoney(machine, r.getCardNum(), r.getPin(), BigDecimal.valueOf(r.getAmount()));
                    try {
                        response.send((new ObjectMapper()).writeValueAsString(res));
                    } catch (JsonProcessingException e) {
                        response.status(500).send();
                    }
                })
                .exceptionally(response::send);
    }

    private void putMoney(ServerRequest request, ServerResponse response) {
        request.content().as(PutMoneyRequest.class)
                .thenAccept(r -> {
                    if (r.getNotes().length != BANKNOTE_NOM_COUNT) {
                        response.send(new InvalidAlgorithmParameterException(String.format("Note array must have size=", BANKNOTE_NOM_COUNT)));
                    } else {
                        var res = srv.putMoney(machine, r.getCardNum(), r.getPin(), Arrays.asList(r.getNotes()));
                        response.send(String.valueOf(res));
                    }
                })
                .exceptionally(response::send);
    }
}
