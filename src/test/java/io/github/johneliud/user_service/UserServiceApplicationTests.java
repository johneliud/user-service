package io.github.johneliud.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/test",
    "jwt.secret=testSecretKeyForTestingPurposeOnly123456",
    "jwt.expiration=86400000",
    "spring.kafka.bootstrap-servers=localhost:9092",
    "spring.kafka.consumer.group-id=user-service",
    "spring.kafka.listener.auto-startup=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
})
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}