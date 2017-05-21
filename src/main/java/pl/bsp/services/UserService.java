package pl.bsp.services;

import pl.bsp.entities.User;

public interface UserService {

	public void save(User user);
	
	public pl.bsp.model.User findByUsername(String username);
	
	public pl.bsp.model.User findById(long id);
}
