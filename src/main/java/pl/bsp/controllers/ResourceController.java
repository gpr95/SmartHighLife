package pl.bsp.controllers;

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

import pl.bsp.arduino.ArduinoReader;
import pl.bsp.arduino.ControllerBrigde;
import pl.bsp.arduino.ObserveMotion;
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
		ArduinoReader.iNeedToCheckDatabase = true;




		if (resServ.addResource(resource)) {
			ControllerBrigde.addResource(resource,
					userService.findByUsername(resource.getUsername()).getIpAddress());
			return new ResponseEntity<>(resource, HttpStatus.OK);
		}
		else
			return new ResponseEntity<>(resource, HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/get-arduino-uno-address", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> getArduinoAddress() {
		String arduinoAddress = ardServ.findArduinoInNetwork();
		if (arduinoAddress != null)
			return new ResponseEntity<>("{\"status\": \"" + arduinoAddress + "\"}", HttpStatus.OK);
		else
			return new ResponseEntity<>("{\"status\": \"" + arduinoAddress + "\"}", HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/post-value/{username}/{serial_id}/{val}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<List<pl.bsp.model.Resource>> postValueToResource(@PathVariable("username") String userName,
			@PathVariable("serial_id") String resourceId, @PathVariable("val") String val) {
		User resourcesOwner = userService.findByUsername(userName);
		List<pl.bsp.model.Resource> resources = resourcesOwner.getResources();
		String arduinoIp = resourcesOwner.getIpAddress();
		int serialId = Integer.parseInt(resourceId);

		if (val.equals("ON"))
			ardServ.turnOnTheLight(arduinoIp, serialId);
		else if (val.equals("OFF"))
			ardServ.turnOffTheLight(arduinoIp, serialId);
		return new ResponseEntity<>(resources, HttpStatus.OK);
	}

	@RequestMapping(value = "/get-value/{username}/{serial_id}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> getValueFrnResource(@PathVariable("username") String userName,
			@PathVariable("serial_id") String resourceId) {
		User resourcesOwner = userService.findByUsername(userName);
		String arduinoIp = resourcesOwner.getIpAddress();
		String result = ardServ.getResourceValue(arduinoIp, Integer.parseInt(resourceId));
		System.out.println(result);
		if (result != null && !result.isEmpty())
			return new ResponseEntity<>("{\"status\": \"" + result + "\"}", HttpStatus.OK);
		else
			return new ResponseEntity<>("{\"status\": \"" + result + "\"}", HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/delete-resource/{username}/{serial_id}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> deleteResource(@PathVariable("username") String userName,
			@PathVariable("serial_id") String serialId) {
		if (resServ.deleteResource(Integer.parseInt(serialId), userName))
			return new ResponseEntity<>("{\"status\": \"deleted\"}",HttpStatus.OK);
		else
			return new ResponseEntity<>("{\"status\": \"error\"}",HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/synchronize/{username}/{serial_id}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> zeroesResource(@PathVariable("username") String userName,
												 @PathVariable("serial_id") String serialId) {
		if (resServ.syncResource(Integer.parseInt(serialId), userName))
			return new ResponseEntity<>("{\"status\": \"deleted\"}",HttpStatus.OK);
		else
			return new ResponseEntity<>("{\"status\": \"error\"}",HttpStatus.NOT_FOUND);
	}
}
