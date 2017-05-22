package pl.bsp.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.bsp.entities.Resource;
import pl.bsp.entities.ResourceType;
import pl.bsp.model.ResourceDAO;
import pl.bsp.model.User;
import pl.bsp.model.UserRepository;

@Service
public class ResourceServiceImpl implements ResourceService{

	@Autowired
	ResourceDAO resourceRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	UserServiceImpl userService;
	
	@Override
	public boolean addResource(Resource resourceToAdd) {
		try{
		User owner = userRepo.findByUsername(resourceToAdd.getUsername());
		pl.bsp.model.Resource resourceToDb = new pl.bsp.model.Resource();
		resourceToDb.setName(resourceToAdd.getName());
		resourceToDb.setDescription(resourceToAdd.getDescription());
		resourceToDb.setLocalization(resourceToAdd.getLocalization());
		resourceToDb.setResourceType(resourceToAdd.getResourceType());
		resourceToDb.setUser(owner);
		if(resourceToAdd.getResourceType().equals(ResourceType.ACTIVE_OBJECT)){
			resourceToDb.setAction("GET/POST");
		}else{
			resourceToDb.setAction("GET");
		}
		Set <pl.bsp.model.Resource> resources = owner.getResources();
		resources.add(resourceToDb);
		resourceRepo.addResource(resourceToDb);
		userService.update(owner);
		return true;
		}catch(Exception e){
			return false;
		}
	}
}
