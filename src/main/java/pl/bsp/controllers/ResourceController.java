package pl.bsp.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.bsp.entities.Resource;
import pl.bsp.model.User;
import pl.bsp.services.ResourceServiceImpl;
import pl.bsp.services.UserServiceImpl;


@RestController
public class ResourceController {

	@Autowired
	ResourceServiceImpl resServ;
	
	@Autowired
	UserServiceImpl userService;
	
	@RequestMapping(value="/resources/{username}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE } )
	  public  ResponseEntity<List<pl.bsp.model.Resource>> resources(@PathVariable("username") String username) {
		User resourcesOwner = userService.findByUsername(username);
		List<pl.bsp.model.Resource> resources = resourcesOwner.getResources();
		if(resources == null || resources.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<List<pl.bsp.model.Resource>>(resources, HttpStatus.OK);
	  }

	@RequestMapping(value = "/add-resource", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Resource> addResource(@RequestBody Resource resource) {
		if(resServ.addResource(resource))
			return new ResponseEntity<Resource>(resource, HttpStatus.OK);
		else
			return new ResponseEntity<Resource>(resource, HttpStatus.BAD_REQUEST);
	}
}
