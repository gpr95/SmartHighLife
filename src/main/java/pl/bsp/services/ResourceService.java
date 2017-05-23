package pl.bsp.services;

import pl.bsp.entities.Resource;

public interface ResourceService {
	
	public boolean addResource(Resource resourceToAdd);
	pl.bsp.model.Resource findByName(String name);
}
