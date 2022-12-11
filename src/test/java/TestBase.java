import helpers.Configuration;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import java.util.List;

import static io.restassured.RestAssured.*;

public class TestBase {

    public String baseUrl = Configuration.getBaseUrl();
    public String boards = "/boards";
    public String lists = "/lists";
    public String cards = "/cards";
    public String actions = "/actions";

    public RequestSpecBuilder reqBuilder;
    public RequestSpecification reqSpecification;

    @BeforeMethod
    public void testSetup() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        reqBuilder = new RequestSpecBuilder();
        reqBuilder.addQueryParam("key", Configuration.getKey());
        reqBuilder.addQueryParam("token", Configuration.getToken());
        reqBuilder.setContentType(ContentType.JSON);
        reqSpecification = reqBuilder.build();
    }

    @AfterSuite
    public void testsCleaup() {
        Response response =
                given()
                        .spec(reqSpecification)
                        .when()
                        .pathParam("id", "testuser05695085")
                        .get(baseUrl + "/organizations/{id}/boards")
                        .then()
                        .statusCode(200)
                        .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<String> allBoardIds = jsonPath.getList("id");

        for (String id : allBoardIds) {
            given()
                    .spec(reqSpecification)
                    .pathParam("id", id)
                    .when()
                    .delete(baseUrl + boards + "/{id}")
                    .then()
                    .statusCode(200);
        }
    }

}