package com.hoaxifymohan.hoaxifymohan;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

import org.aspectj.lang.annotation.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.hoaxifymohan.hoaxifymohan.error.ApiError;
import com.hoaxifymohan.hoaxifymohan.shared.GenericResponse;
import com.hoaxifymohan.hoaxifymohan.user.User;
import com.hoaxifymohan.hoaxifymohan.user.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT )
@ActiveProfiles("test")


public class UserControllerTest {
	
	private static final String API_1_0_USERS = "/api/1.0/users";
	@Autowired
	TestRestTemplate testRestTemplate;
	
	@Autowired
	UserRepository userRepository;

	@Before(value = "")
	public void cleanup() {
		userRepository.deleteAll();
	}
	@Test
	public void postUser_whenUserIsValid_receivedOk() {
		User user = createValidUser();
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	
	@Test
	public void postUser_whenUserIsValid_userSavedToDatabase() {
		User user = createValidUser();
		postSignup(user, Object.class);
		assertThat(userRepository.count()).isEqualTo(1);
	}

	
	@Test
	public void postUser_whenUserIsValid_receiveSuccessMessage() {
		User user = createValidUser();
		ResponseEntity<GenericResponse> response = postSignup(user, GenericResponse.class);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	public void postUser_whenUserIsValid_passwordHasheedInDatabase() {
		User user = createValidUser();
		postSignup(user, Object.class);
		List<User> users = userRepository.findAll();
		User inDB = users.get(0);
		assertThat(inDB.getPassword()).isNotEqualTo(user.getPassword());
		
	}
	
	@Test
	public void postUser_whenUserHasNullUsername_receivedBadRequest() {
		User user = createValidUser();
		user.setUsername(null);
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void postUser_whenUserHasNullDisplayname_receivedBadRequest() {
		User user = createValidUser();
		user.setDisplayName(null);
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void postUser_whenUserHasNullPassword_receivedBadRequest() {
		User user = createValidUser();
		user.setPassword(null);
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void postUser_whenUserHasUsernameWithLessThanRequired_receivedBadRequest() {
		User user = createValidUser();
		user.setUsername("abc");
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void postUser_whenUserHasDisplayNameWithLessThanRequired_receivedBadRequest() {
		User user = createValidUser();
		user.setDisplayName("abc");
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test
	public void postUser_whenUserHasPasswordWithLessThanRequired_receivedBadRequest() {
		User user = createValidUser();
		user.setPassword("P4sswd");
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void postUser_whenUserHasUsernameExceedsTheLengthLimit_receivedBadRequest() {
		User user = createValidUser();
		String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x-> "a").collect(Collectors.joining());
		user.setUsername(valueOf256Chars);
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	
	@Test
	public void postUser_whenUserHasDisplayNameExceedsTheLengthLimit_receivedBadRequest() {
		User user = createValidUser();
		String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x-> "a").collect(Collectors.joining());
		user.setDisplayName(valueOf256Chars);
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void postUser_whenUserHasPasswordExceedsTheLengthLimit_receivedBadRequest() {
		User user = createValidUser();
		String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x-> "a").collect(Collectors.joining());
		user.setPassword(valueOf256Chars + "A1");
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void postUser_whenUserHasPasswordWithAllLowercase_receivedBadRequest() {
		User user = createValidUser();
		user.setPassword("alllowercase");
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void postUser_whenUserHasPasswordWithAllUppercase_receivedBadRequest() {
		User user = createValidUser();
		user.setPassword("ALLUPPERCASE");
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void postUser_whenUserHasPasswordWithAllNumber_receivedBadRequest() {
		User user = createValidUser();
		user.setPassword("123456789");
		ResponseEntity<Object> response = postSignup(user, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void postUser_whenUserIsInvalid_receiveApiError() {
		User user = new User();
		ResponseEntity<ApiError> response = postSignup(user,ApiError.class);
		assertThat(response.getBody().getUrl()).isEqualTo(API_1_0_USERS);
	}

	
	@Test
	public void postUser_whenUserIsInvalid_receiveApiErrorWithValidationErrors() {
		User user = new User();
		ResponseEntity<ApiError> response = postSignup(user,ApiError.class);
		assertThat(response.getBody().getValidationErrors().size()).isEqualTo(3);
	}

	@Test
	public void postuser_whenUserHasNullUsername_receiveMessageOfNullErrorForUsername() {
		User user = createValidUser();
		user.setUsername(null);
		ResponseEntity<ApiError> response = postSignup(user,ApiError.class);
		Map<String, String> validationErrors = response.getBody().getValidationErrors();
		assertThat(validationErrors.get("username")).isEqualTo("Username cannot be null");
	}

	@Test
	public void postuser_whenUserHasNullPassword_receiveGenericMessageOfNullError() {
		User user = createValidUser();
		user.setPassword(null);
		ResponseEntity<ApiError> response = postSignup(user,ApiError.class);
		Map<String, String> validationErrors = response.getBody().getValidationErrors();
		assertThat(validationErrors.get("password")).isEqualTo("Cannot be null");
	}
	
	@Test
	public void postuser_whenUserHasInvalidLengthUsername_receiveGenericMessageOfSizeError() {
		User user = createValidUser();
		user.setUsername("abc");
		ResponseEntity<ApiError> response = postSignup(user,ApiError.class);
		Map<String, String> validationErrors = response.getBody().getValidationErrors();
		assertThat(validationErrors.get("username")).isEqualTo("It must have minimum 4 and maximum 255 characters");
	}
	
	@Test
	public void postuser_whenUserHasInvalidPasswordPattern_receiveGenericMessageOfPatternError() {
		User user = createValidUser();
		user.setPassword("alllowercase");
		ResponseEntity<ApiError> response = postSignup(user,ApiError.class);
		Map<String, String> validationErrors = response.getBody().getValidationErrors();
		assertThat(validationErrors.get("password")).isEqualTo("Password must have at least one uppercase, one lowercase and one number");
	}

	@Test
	public void postUser_whenAnotherUserHasSameUsername_receiveBadRequest() {
		userRepository.save(createValidUser());
		User user = createValidUser();
		ResponseEntity<Object> response = postSignup(user,Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
	}
	public <T> ResponseEntity<T> postSignup(Object request, Class<T> response) {
		return testRestTemplate.postForEntity(API_1_0_USERS, request, response);
	}
	private User createValidUser() {
		User user = new User();
		user.setUsername("test-user");
		user.setDisplayName("test-display");
		user.setPassword("P4ssword");
		return user;
	}

}
