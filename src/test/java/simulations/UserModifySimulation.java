package simulations;

import io.gatling.javaapi.core.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;

public class UserModifySimulation extends ReqresBaseSimulation {

    private ScenarioBuilder crudScenario = scenario("User CRUD Operations Test")
            .exec(createUser)
            .pause(Duration.ofSeconds(1))
            .exec(updateUserPut)
            .pause(Duration.ofSeconds(1))
            .exec(updateUserPatch)
            .pause(Duration.ofSeconds(1))
            .exec(deleteUser);

    {
        setUp(
                crudScenario.injectOpen(
                        rampUsers(20).during(Duration.ofSeconds(20)),
                        constantUsersPerSec(10).during(Duration.ofSeconds(40))
                )
        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().percentile3().lt(3000),
                        global().successfulRequests().percent().gt(98.0)
                );
    }
}
