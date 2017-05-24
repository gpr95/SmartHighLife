package pl.bsp.services;

import pl.bsp.entities.Resource;
import pl.bsp.model.User;

public interface ResourceService {
	
	public boolean addResource(Resource resourceToAdd);
	pl.bsp.model.Resource findBySerialIdAndUser(int serialId, User user);
	public boolean deleteResource(int resourceId, String ownerUsername);

    boolean syncResource(int serialId, String userName);
}
