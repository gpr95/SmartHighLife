package pl.bsp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Kamil on 27.04.2017.
 */
@RestController
public class ResourceController {

	@RequestMapping("/resource")
	  public Map<String,Object> home() {
	    Map<String,Object> model = new HashMap<String,Object>();
	    model.put("id", UUID.randomUUID().toString());
	    model.put("content", "Hello World");
	    return model;
	  }
}
