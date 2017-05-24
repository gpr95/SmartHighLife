package pl.bsp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.bsp.entities.Resource;
import pl.bsp.entities.ResourceType;
import pl.bsp.model.ResourceDAO;
import pl.bsp.model.ResourceRepository;
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
	
	@Autowired
	ResourceRepository resourceRepo2;
	
	@Override
	public boolean addResource(Resource resourceToAdd) {
		try{
		User owner = userRepo.findByUsername(resourceToAdd.getUsername());
		pl.bsp.model.Resource resourceToDb = new pl.bsp.model.Resource();
		resourceToDb.setName(resourceToAdd.getName());
		resourceToDb.setDescription(resourceToAdd.getDescription());
		resourceToDb.setLocalization(resourceToAdd.getLocalization());
		resourceToDb.setResourceType(resourceToAdd.getResourceType());
		resourceToDb.setSerialId(resourceToAdd.getSerial_id());
		resourceToDb.setUser(owner);
		if(resourceToAdd.getResourceType().equals(ResourceType.ACTIVE_OBJECT)){
			resourceToDb.setAction("GET/POST");
		}else{
			resourceToDb.setAction("GET");
		}
		List<pl.bsp.model.Resource> resources = owner.getResources();
		resources.add(resourceToDb);
		ArduinoService ardServ = new ArduinoServiceImpl();
		ardServ.addNewResource(owner.getIpAddress(), resourceToDb.getSerialId());
		
		resourceRepo.addResource(resourceToDb);
		userService.update(owner);
		return true;
		}catch(Exception e){
			return false;
		}
	}
	
	

	@Override
	public pl.bsp.model.Resource findByName(String name) {
		return null;
	}



	@Override
	@Transactional
	public boolean deleteResource(int serialId, String ownerUsername) {
		User owner = userRepo.findByUsername(ownerUsername);
		pl.bsp.model.Resource resourceToDelete = resourceRepo2.findBySerialId(serialId);
		boolean status = owner.getResources().remove(resourceToDelete);
		userService.update(owner);
		List<pl.bsp.model.Resource> deletedResources = resourceRepo2.deleteBySerialId(serialId);
		if(status){
			return true;
		}else{
			return false;
		}
	}
}
