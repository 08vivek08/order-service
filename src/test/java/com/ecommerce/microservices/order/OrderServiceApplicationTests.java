package com.ecommerce.microservices.order;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

import static org.hamcrest.MatcherAssert.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {
	@ServiceConnection
	static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.3.0");

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void globalSetup()
	{
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		// Start the container
		mySQLContainer.start();
	}

	@Test
	void shouldCreateOrder() {
		String requestBody = """
					{
						"skuCode" : "iphone_15",
						"price" : 1000,
						"quantity" : 1
					}
				""";

		var responseBodyString = RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/order")
				.then()
				.log().all()
				.statusCode(201)
				.extract()
				.body().asString();

		assertThat(responseBodyString, Matchers.is("Order Placed Successfully"));
//				.body("id", Matchers.notNullValue())
//				.body("order_number", Matchers.notNullValue())
//				.body("sku_code", Matchers.equalTo("iphone_15"))
//				.body("price", Matchers.equalTo(1000))
//				.body("quantity", Matchers.equalTo(1));
	}
}
