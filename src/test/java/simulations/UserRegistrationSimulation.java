package simulations;

import io.gatling.javaapi.core.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;

public class UserRegistrationSimulation extends ReqresBaseSimulation {

    private ScenarioBuilder registrationScenario = scenario("User Registration & Login Test")
            .exec(registerSuccessful)
            .pause(Duration.ofSeconds(1))
            .exec(loginSuccessful)
            .pause(Duration.ofSeconds(2))
            .exec(registerUnsuccessful)
            .pause(Duration.ofSeconds(1))
            .exec(loginUnsuccessful);

    {
        setUp(
                registrationScenario.injectOpen(
                        atOnceUsers(5),
                        rampUsers(15).during(Duration.ofSeconds(15)),
                        constantUsersPerSec(3).during(Duration.ofSeconds(30))
                )
        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().mean().lt(2000),
                        global().failedRequests().percent().lt(5.0)
                );
    }
}
