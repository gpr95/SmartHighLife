package pl.bsp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.bsp.entities.Resource;

import java.util.ArrayList;
import java.util.List;


@RestController
public class ResourceController {

	
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
