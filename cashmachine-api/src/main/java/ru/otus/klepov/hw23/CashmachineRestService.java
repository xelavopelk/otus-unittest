package ru.otus.klepov.hw23;

import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import jakarta.json.Json;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonReaderFactory;
import otus.study.cashmachine.machine.service.CashMachineService;

import java.util.Collections;

public class CashmachineRestService implements Service {
    private String configPath = "app.cachmashine";

    private String configData;

    private static final JsonBuilderFactory JSON_BF = Json.createBuilderFactory(Collections.emptyMap());

    private static final JsonReaderFactory JSON_RF = Json.createReaderFactory(Collections.emptyMap());

    private CashMachineService machine;
    public CashmachineRestService(Config config, CashMachineService machine){
        this.machine = machine;
        this.configData = config.get(configPath).asString().orElse("default config info");
    }
    @Override
    public void update(Routing.Rules rules) {
        rules
            .post("/balance", this::balance);
    }

    private void balance(ServerRequest request,
                  ServerResponse response) {
        String cardNum = request.path().param("cardNum");
        String pin = request.path().param("pin");
        request.content().as(JsonObject.class)
                .thenAccept(jo -> System.out.println(jo.toString()))
                        .exceptionally(response::send);
        response.send("100$");
    }
}
