package pl.bsp.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.bsp.arduino.ObserveMotion;
import pl.bsp.entities.User;
import pl.bsp.services.ArduinoService;
import pl.bsp.services.ArduinoServiceImpl;
import pl.bsp.services.ResourceServiceImpl;
import pl.bsp.services.UserServiceImpl;

@RestController
public class UserController {

	@Autowired
	UserServiceImpl userService;
	
	@Autowired
	ResourceServiceImpl resServ;

	ArduinoService ardServ = new ArduinoServiceImpl();
	
	@RequestMapping(value = "/login-post", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<User> login(@RequestBody User user) {
		Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
		SecurityContextHolder.getContext().setAuthentication(auth);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	@RequestMapping(value = "/register-post", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<String> register(@RequestBody User user) {
		if(user.getPassword().equals(user.getConfirm_password())){
			userService.save(user);
			return new ResponseEntity<String>("{\"status\": \""+user.getUsername()+"\"}", HttpStatus.OK);
		}else{
			return new ResponseEntity<String>("{\"status\": \"PASSWORD_NOT_MATCH\"}", HttpStatus.CONFLICT);
		}
	}
}
