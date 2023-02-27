package tdd.techGlobal;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pojoClasses.techGlobal.CreateUser;
import utilities.ConfigReader;

public class CreateNewStudent {
    Faker faker = new Faker();

    Response response;

    @BeforeTest
    public void beforeTest() {
        RestAssured.baseURI = ConfigReader.getProperty("BaseUrl");
    }

    @Test(priority = 1, description = "Create , update ,delete new student")
    public void createStudent() {
        CreateUser createUser = CreateUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .dob("2020-09-20")
                .build();
        response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when().post("/students")
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response();

        int studentId = response.jsonPath().getInt("id");

        // update student information

        CreateUser createUser1 = CreateUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .dob("2020-09-20")
                .build();
        response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(createUser1)
                .when().put("/students/" + studentId)
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response();

        // delete the student

        response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(createUser1)
                .when().delete("/students/" + studentId)
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response();

        //make sure student has been deleted

        response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(createUser1)
                .when().get("/students/" + studentId)
                .then().log().all()
                .assertThat().statusCode(404)
                .extract().response();

        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "Student with id " + studentId + " does not exists.");


    }
}
