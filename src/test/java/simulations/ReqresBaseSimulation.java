package simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public abstract class ReqresBaseSimulation extends Simulation {

    protected static final String BASE_URL = "https://reqres.in";

    protected HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling Performance Test");

    protected ChainBuilder getUsersList = exec(
            http("Get Users List - Page 2")
                    .get("/api/users?page=2")
                    .check(status().is(200))
                    .check(jsonPath("$.data[*].id").findAll().saveAs("userIds"))
                    .check(jsonPath("$.data[*].email").findAll().saveAs("emails"))
                    .check(jsonPath("$.data[*].avatar").findAll().saveAs("avatars"))
                    .check(bodyString().saveAs("responseBody"))
    );

    protected ChainBuilder getUsersListWithDelay = exec(
            http("Get Users List with Delay")
                    .get("/api/users?delay=3")
                    .check(status().is(200))
                    .check(jsonPath("$.data[*].email").findAll()
                            .transform(emails -> emails.stream()
                                    .allMatch(email -> email.endsWith("@reqres.in")))
                            .is(true))
    );

    protected ChainBuilder getSingleUser = exec(
            http("Get Single User")
                    .get("/api/users/3")
                    .check(status().is(200))
                    .check(jsonPath("$.data.id").is("3"))
                    .check(jsonPath("$.data.avatar").exists())
                    .check(jsonPath("$.data.email").saveAs("userEmail"))
    );

    protected ChainBuilder getSingleUserNotFound = exec(
            http("Get Non-Existent User")
                    .get("/api/users/23")
                    .check(status().is(404))
    );

    protected ChainBuilder getResourcesList = exec(
            http("Get Resources List")
                    .get("/api/unknown")
                    .check(status().is(200))
                    .check(jsonPath("$.data[*].id").findAll().saveAs("resourceIds"))
    );

    protected ChainBuilder getSingleResource = exec(
            http("Get Single Resource")
                    .get("/api/unknown/2")
                    .check(status().is(200))
                    .check(jsonPath("$.data.id").is("2"))
                    .check(jsonPath("$.data.name").exists())
    );

    protected ChainBuilder createUser = exec(
            http("Create User")
                    .post("/api/users")
                    .body(StringBody("""
                            {
                                "name": "morpheus",
                                "job": "leader"
                            }
                            """))
                    .check(status().is(201))
                    .check(jsonPath("$.id").saveAs("createdUserId"))
                    .check(jsonPath("$.createdAt").exists())
    );

    protected ChainBuilder updateUserPut = exec(
            http("Update User (PUT)")
                    .put("/api/users/2")
                    .body(StringBody("""
                            {
                                "name": "morpheus",
                                "job": "zion resident"
                            }
                            """))
                    .check(status().is(200))
                    .check(jsonPath("$.updatedAt").exists())
    );

    protected ChainBuilder updateUserPatch = exec(
            http("Update User (PATCH)")
                    .patch("/api/users/2")
                    .body(StringBody("""
                            {
                                "name": "morpheus",
                                "job": "zion resident"
                            }
                            """))
                    .check(status().is(200))
                    .check(jsonPath("$.updatedAt").exists())
    );

    protected ChainBuilder deleteUser = exec(
            http("Delete User")
                    .delete("/api/users/2")
                    .check(status().is(204))
    );

    protected ChainBuilder registerSuccessful = exec(
            http("Register Successful")
                    .post("/api/register")
                    .body(StringBody("""
                            {
                                "email": "eve.holt@reqres.in",
                                "password": "pistol"
                            }
                            """))
                    .check(status().is(200))
                    .check(jsonPath("$.id").exists())
                    .check(jsonPath("$.token").saveAs("authToken"))
    );

    protected ChainBuilder registerUnsuccessful = exec(
            http("Register Unsuccessful")
                    .post("/api/register")
                    .body(StringBody("""
                            {
                                "email": "sydney@fife"
                            }
                            """))
                    .check(status().is(400))
                    .check(jsonPath("$.error").is("Missing password"))
    );

    protected ChainBuilder loginSuccessful = exec(
            http("Login Successful")
                    .post("/api/login")
                    .body(StringBody("""
                            {
                                "email": "eve.holt@reqres.in",
                                "password": "cityslicka"
                            }
                            """))
                    .check(status().is(200))
                    .check(jsonPath("$.token").saveAs("loginToken"))
    );

    protected ChainBuilder loginUnsuccessful = exec(
            http("Login Unsuccessful")
                    .post("/api/login")
                    .body(StringBody("""
                            {
                                "email": "peter@klaven"
                            }
                            """))
                    .check(status().is(400))
                    .check(jsonPath("$.error").is("Missing password"))
    );
}
