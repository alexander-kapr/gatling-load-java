package simulations;

import io.gatling.javaapi.core.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;

public class UserDataSimulation extends ReqresBaseSimulation {

    private ScenarioBuilder userDataScenario = scenario("User Data Load Test")
            .exec(getUsersList)
            .pause(Duration.ofSeconds(1))
            .exec(getSingleUser)
            .pause(Duration.ofSeconds(1))
            .exec(getSingleUserNotFound)
            .pause(Duration.ofSeconds(2))
            .exec(getUsersListWithDelay);

    {
        setUp(
                userDataScenario.injectOpen(
                        rampUsers(10).during(Duration.ofSeconds(10)),
                        constantUsersPerSec(5).during(Duration.ofSeconds(30)),
                        rampUsersPerSec(5).to(20).during(Duration.ofSeconds(20))
                )
        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().max().lt(5000),
                        global().successfulRequests().percent().gt(95.0)
                );
    }
}
