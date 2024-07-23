package yandex.test.petstore;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetStoreTest {

    private Long petId;

    @BeforeAll
    public void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    @Test
    @Order(1)
    public void testCreatePet() {
        String requestBody = """
                {
                  "id": 0,
                  "name": "doggie",
                  "status": "available"
                }""";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .body("name", equalTo("doggie"))
                .extract()
                .response();

        petId = response.path("id");
    }

    @Test
    @Order(2)
    public void testReadPet() {
        given()
                .when()
                .get("/pet/" + petId)
                .then()
                .statusCode(200)
                .body("id", equalTo(petId))
                .body("name", equalTo("doggie"));
    }

    @Test
    @Order(3)
    public void testUpdatePet() {
        String requestBody = "{\n" +
                "  \"id\": " + petId + ",\n" +
                "  \"name\": \"doggie-updated\",\n" +
                "  \"status\": \"sold\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .body("name", equalTo("doggie-updated"))
                .body("status", equalTo("sold"));
    }

    @Test
    @Order(4)
    public void testDeletePet() {
        given()
                .when()
                .delete("/pet/" + petId)
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/pet/" + petId)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(5)
    public void testCreateUsersWithList() {
        String requestBody = """
                [
                  {
                    "id": 1,
                    "username": "user1",
                    "firstName": "Alice",
                    "lastName": "Pitt",
                    "email": "alice@example.com",
                    "password": "password1",
                    "phone": "1234567890",
                    "userStatus": 1
                  },
                  {
                    "id": 2,
                    "username": "user2",
                    "firstName": "Bob",
                    "lastName": "James",
                    "email": "bob@example.com",
                    "password": "password2",
                    "phone": "0987654321",
                    "userStatus": 1
                  }
                ]""";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/user/createWithList")
                .then()
                .statusCode(200)
                .body("message", equalTo("ok"));
    }

    @Test
    @Order(6)
    public void testGetUserByUsername() {
        given()
                .when()
                .get("/user/user1")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Alice"));
    }

    @Test
    @Order(7)
    public void testDeleteUserByUsername() {
        given()
                .when()
                .delete("/user/user1")
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/user/user1")
                .then()
                .statusCode(404)
                .body("message", equalTo("User not found"));
    }

    @Test
    @Order(8)
    public void testGetSecondUserByUsername() {
        given()
                .when()
                .get("/user/user2")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Bob"))
                .body("lastName", equalTo("James"));
    }
}
