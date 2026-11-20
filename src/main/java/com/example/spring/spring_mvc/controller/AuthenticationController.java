package com.example.spring.spring_mvc.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring.spring_mvc.dto.AuthLoginDto;
import com.example.spring.spring_mvc.dto.AuthRoleRegisterDto;
import com.example.spring.spring_mvc.dto.AuthUserRegisterDto;
import com.example.spring.spring_mvc.entity.Auth_Role;
import com.example.spring.spring_mvc.entity.Auth_User;
import com.example.spring.spring_mvc.repository.AuthRoleRepository;
import com.example.spring.spring_mvc.repository.AuthUserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * REST controller for managing authentication and registration endpoints.
 * Provides APIs to register new roles, register new users, and authenticate
 * existing users.
 */
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

	private AuthenticationManager authenticationManager;
	private AuthUserRepository authUserRepository;
	private AuthRoleRepository authRoleRepository;
	private BCryptPasswordEncoder passwordEncoder;

	public AuthenticationController(
			AuthenticationManager authenticationManager,
			AuthUserRepository authUserRepository,
			AuthRoleRepository authRoleRepository,
			BCryptPasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.authUserRepository = authUserRepository;
		this.authRoleRepository = authRoleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
     * Registers a new authorization role in the system.
     *
     * @param authRoleRegisterDto DTO containing the name of 
	 * the role to creates
     * @return a {@link ResponseEntity} with a success message and HTTP 200 OK,
     * or an error message with HTTP 400 Bad Request if the role exists
     */
	@PostMapping("/register_authorized_role")
	public ResponseEntity<String> register_authRole(
			@RequestBody AuthRoleRegisterDto authRoleRegisterDto) {
		if (authRoleRepository.existsByName(authRoleRegisterDto.getName())) {
			return new ResponseEntity<>(authRoleRegisterDto.getName()
					+ " already created.", HttpStatus.BAD_REQUEST);
		}

		Auth_Role auth_role = new Auth_Role();
		auth_role.setName(authRoleRegisterDto.getName());

		authRoleRepository.save(auth_role);

		return new ResponseEntity<>(auth_role.getName()
				+ " is successfully created", HttpStatus.OK);
	}

	/**
     * Registers a new user and assigns them the default "USER" role.
     *
     * @param authUserRegisterDto DTO containing the new user's username and
     * raw password
     * @return a {@link ResponseEntity} with a success message and HTTP 200 OK,
     * or an error message with HTTP 400 Bad Request if username is taken
     * @throws RuntimeException if the default "USER" role is not found in the
     * database
     */
	@PostMapping("/register_authorized_user")
	public ResponseEntity<String> register_authUser(
			@RequestBody AuthUserRegisterDto authUserRegisterDto) {
		if (authUserRepository.existsByUsername(
				authUserRegisterDto.getUsername())) {
			return new ResponseEntity<>(authUserRegisterDto.getUsername()
					+ " is already taken.", HttpStatus.BAD_REQUEST);
		}

		Auth_User auth_user = new Auth_User();
		auth_user.setUsername(authUserRegisterDto.getUsername());
		auth_user.setPassword(passwordEncoder.encode(
				authUserRegisterDto.getPassword()));

		Auth_Role auth_role = authRoleRepository.findByName("USER")
				.orElseThrow(() -> new RuntimeException(
						"Roles USER not found"));
		auth_user.setRoles(Collections.singletonList(auth_role));

		authUserRepository.save(auth_user);

		return new ResponseEntity<>(auth_user.getUsername()
				+ " is succesfully registered", HttpStatus.OK);
	}

	/**
     * Authenticates a user with the provided login credentials and sets the
     * security context.
     *
     * @param authLoginDto DTO containing the user's login username and 
	 * password
     * @return a {@link ResponseEntity} confirming successful sign-in with
     * HTTP 200 OK
     */
	@PostMapping("/auth_login")
	public ResponseEntity<String> login(
			@RequestBody AuthLoginDto authLoginDto) {
		Authentication authenticate = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						authLoginDto.getUsername(),
						authLoginDto.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authenticate);
		return new ResponseEntity<>("Sign in successfully!", HttpStatus.OK);
	}

	/**
	 * Authenticates a user with the provided login credentials, sets the
	 * security context, and persists it inside an active HTTP session.
	 *
	 * @param authLoginDto DTO containing the user's login username and
	 * password
	 * @param request      the {@link HttpServletRequest} used to create or
	 * retrieve the {@link HttpSession}
	 * @return a {@link ResponseEntity} containing a JSON confirmation message
	 *         with HTTP 200 OK
	 */
    @PostMapping("/auth_login_session")    
    public ResponseEntity<?> loginWithSession(
            @RequestBody AuthLoginDto authLoginDto,
            HttpServletRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authLoginDto.getUsername(),
                        authLoginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute(
                "SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext()
        );

        Map<String, String> response = new HashMap<>();
        response.put(
            "message", "Authenticated successfully via session cookie.");
        return ResponseEntity.ok(response);
    }

	/**
	 * Invalidates the current user's HTTP session (if one exists) and clears
	 * the Spring Security context.
	 *
	 * @param request the {@link HttpServletRequest} used to retrieve the
	 *                existing {@link HttpSession}
	 * @return a {@link ResponseEntity} with HTTP 401 Unauthorized confirming
	 *         session termination
	 */
	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null){
			session.invalidate();
		}
		SecurityContextHolder.clearContext();
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body("Logged out from session container.");
	}
}
