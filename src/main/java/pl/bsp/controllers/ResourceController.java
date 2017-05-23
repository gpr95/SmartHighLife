package pl.bsp.controllers;

import java.util.ArrayList;
import java.util.List;

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
import pl.bsp.services.ArduinoService;
import pl.bsp.services.ArduinoServiceImpl;
import pl.bsp.services.ResourceServiceImpl;
import pl.bsp.services.UserServiceImpl;

@RestController
public class ResourceController {

	@Autowired
	ResourceServiceImpl resServ;
	@Autowired
	UserServiceImpl userService;

	
	ArduinoService ardServ = new ArduinoServiceImpl();

	@RequestMapping("/resources")
	public List<Resource> resources() {
		List<Resource> resources = new ArrayList<Resource>();
		Resource res1 = new Resource();
		res1.setName("n1");
		res1.setDescription("desc1");
		res1.setLocalization("10.0.23.1/loc1/1");
		res1.setResourceType("type1");
		Resource res2 = new Resource();
		res2.setName("n2");
		res2.setDescription("desc2");
		res2.setLocalization("10.0.23.1/loc1/2");
		res2.setResourceType("type2");
		resources.add(res1);
		resources.add(res2);
		return resources;
	}

	@RequestMapping(value = "/resources/{username}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<List<pl.bsp.model.Resource>> resources(@PathVariable("username") String username) {
		User resourcesOwner = userService.findByUsername(username);
		List<pl.bsp.model.Resource> resources = resourcesOwner.getResources();

		if (resources == null || resources.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<>(resources, HttpStatus.OK);
	}

	@RequestMapping(value = "/add-resource", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Resource> addResource(@RequestBody Resource resource) {
		if (resServ.addResource(resource))
			return new ResponseEntity<>(resource, HttpStatus.OK);
		else
			return new ResponseEntity<>(resource, HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/get-arduino-uno-address", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> getArduinoAddress() {
		String arduinoAddress = ardServ.findArduinoInNetwork();
		if (arduinoAddress != null)
			return new ResponseEntity<>("{\"status\": \""+arduinoAddress+"\"}", HttpStatus.OK);
		else
			return new ResponseEntity<>("{\"status\": \""+arduinoAddress+"\"}", HttpStatus.NOT_FOUND);
	}

}
