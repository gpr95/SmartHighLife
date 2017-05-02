package pl.bsp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.bsp.entities.Resource;

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
}
