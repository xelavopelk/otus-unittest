package ru.otus.klepov.hw23;

import io.helidon.common.LogConfig;
import io.helidon.common.reactive.Single;
import io.helidon.config.Config;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.metrics.serviceapi.MetricsSupport;
import io.helidon.openapi.OpenAPISupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.CashMachineService;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;
import otus.study.cashmachine.machine.service.impl.MoneyBoxServiceImpl;

/**
 * Simple Hello World rest application.
 */
public final class Program {

    private Program() { }

    /**
     * Application main entry point.
     * @param args command line arguments.
     */
    public static void main(final String[] args) {
        startServer();
    }

    /**
     * Start the server.
     * @return the created {@link WebServer} instance
     */
    static Single<WebServer> startServer() {
        MoneyBox moneyBox = new MoneyBox();
        CashMachine cashMachine = new CashMachine(moneyBox);
        // load logging configuration
        LogConfig.configureRuntime();

        // By default this will pick up application.yaml from the classpath
        Config config = Config.create();

        // Get webserver config from the "server" section of application.yaml and register JSON support
        Single<WebServer> server = WebServer.builder(createRouting(config))
                .config(config.get("server"))
                .addMediaSupport(JsonpSupport.create())
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

    /**
     * Creates new {@link Routing}.
     *
     * @return routing configured with a health check, and a service
     * @param config configuration of this server
     */
    private static CashmachineRestService createBL(Config config) {
        var accountDao = new AccountDao();
        var cardsDao = new CardsDao();
        var accountService = new AccountServiceImpl(accountDao);
        var cardService = new CardServiceImpl(accountService, cardsDao);
        var moneyBoxService = new MoneyBoxServiceImpl();
        var cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
        var moneyBox = new MoneyBox();
        var cashMachine = new CashMachine(moneyBox);
        return new CashmachineRestService(config, cashMachineService);
    }
    private static Routing createRouting(Config config) {

        MetricsSupport metrics = MetricsSupport.create();
        var cms = createBL(config);
        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())   // Adds a convenient set of checks
                .build();

        return Routing.builder()
                .register(OpenAPISupport.create(config.get(OpenAPISupport.Builder.CONFIG_KEY)))
                .register(health)                   // Health at "/health"
                .register(metrics)                  // Metrics at "/metrics"
                .register("/cash", cms)
                .build();
    }

}
