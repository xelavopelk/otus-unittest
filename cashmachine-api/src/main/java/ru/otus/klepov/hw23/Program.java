package ru.otus.klepov.hw23;

import io.helidon.common.LogConfig;
import io.helidon.common.reactive.Single;
import io.helidon.config.Config;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jsonb.JsonbSupport;
import io.helidon.metrics.serviceapi.MetricsSupport;
import io.helidon.openapi.OpenAPISupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import otus.study.cashmachine.machine.data.CashMachine;

import java.io.IOException;


public final class Program {

    private Program() {
    }

    public static void main(final String[] args) throws IOException {
        startServer();
    }

    static Single<WebServer> startServer() throws IOException {

        LogConfig.configureRuntime();
        Config config = Config.create();
        Single<WebServer> server = WebServer.builder(createRouting(config))
                .config(config.get("server"))
                .addMediaSupport(JsonbSupport.create())
                .build()
                .start();

        server.thenAccept(ws -> {
            System.out.println(
                    "WEB server is up! http://localhost:" + ws.port() + "/openapi");
            ws.whenShutdown().thenRun(()
                    -> System.out.println("WEB server is DOWN. Good bye!"));
        })
                .exceptionally(t -> {
                    System.err.println("Startup failed: " + t.getMessage());
                    t.printStackTrace(System.err);
                    return null;
                });
        return server;
    }

    private static CashmachineRestService createBL(Config config) throws IOException {
        var cmf = new CashMachineServiceFactory();
        var cashMachineService = new CashMachineServiceTsImpl(cmf.create());
        var moneyBox = (new MoneyBoxFactory()).create(); //new MoneyBox(100, 100, 100, 100);
        var cashMachine = new CashMachine(moneyBox);
        return new CashmachineRestService(cashMachineService, cashMachine);
    }

    private static Routing createRouting(Config config) throws IOException {

        MetricsSupport metrics = MetricsSupport.create();
        var cms = createBL(config);
        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())   // Adds a convenient set of checks
                .build();

        return Routing.builder()
                .register(OpenAPISupport.create(config.get(OpenAPISupport.Builder.CONFIG_KEY)))
                .register(health)                   // Health at "/health"
                .register("/cash", cms)
                .build();
    }

}
