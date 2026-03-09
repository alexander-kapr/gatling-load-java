package simulations;

import io.gatling.javaapi.core.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;

public class FullLoadSimulation extends ReqresBaseSimulation {

    private ScenarioBuilder readOnlyScenario = scenario("Read-Only User Journey")
            .exec(getUsersList)
            .pause(Duration.ofMillis(500), Duration.ofSeconds(2))
            .exec(getSingleUser)
            .pause(Duration.ofMillis(500), Duration.ofSeconds(1))
            .exec(getResourcesList);

    private ScenarioBuilder writeScenario = scenario("Write Operations Journey")
            .exec(registerSuccessful)
            .pause(Duration.ofSeconds(1))
            .exec(createUser)
            .pause(Duration.ofMillis(500), Duration.ofSeconds(1))
            .exec(updateUserPatch)
            .pause(Duration.ofMillis(500))
            .exec(deleteUser);

    private ScenarioBuilder mixedScenario = scenario("Mixed Operations Journey")
            .exec(getUsersList)
            .pause(Duration.ofMillis(300))
            .exec(createUser)
            .pause(Duration.ofMillis(500))
            .exec(getSingleUser)
            .pause(Duration.ofMillis(300))
            .exec(loginSuccessful)
            .pause(Duration.ofSeconds(1))
            .exec(updateUserPut);

    {
        setUp(
                readOnlyScenario.injectOpen(
                        rampUsers(50).during(Duration.ofSeconds(30)),
                        constantUsersPerSec(20).during(Duration.ofMinutes(2))
                ).protocols(httpProtocol),

                writeScenario.injectOpen(
                        nothingFor(Duration.ofSeconds(10)),
                        rampUsers(20).during(Duration.ofSeconds(20)),
                        constantUsersPerSec(5).during(Duration.ofMinutes(2))
                ).protocols(httpProtocol),

                mixedScenario.injectOpen(
                        nothingFor(Duration.ofSeconds(5)),
                        rampUsers(30).during(Duration.ofSeconds(25)),
                        constantUsersPerSec(10).during(Duration.ofMinutes(2))
                ).protocols(httpProtocol)
        ).assertions(
                global().responseTime().mean().lt(2000),
                global().responseTime().percentile4().lt(5000),
                global().successfulRequests().percent().gt(95.0),
                forAll().failedRequests().percent().lt(5.0)
        );
    }
}
