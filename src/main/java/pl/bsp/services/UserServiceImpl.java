package pl.bsp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.bsp.model.User;
import pl.bsp.model.UserDAO;
import pl.bsp.model.UserRepository;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserDAO userDAO;
	
	@Override
	public void save(pl.bsp.entities.User user) {
		User userToAdd = new User();
		userToAdd.setFirstName(user.getFirst_name());
		userToAdd.setLastName(user.getLast_name());
		userToAdd.setUsername(user.getUsername());
		userToAdd.setPassword(user.getPassword());
		userToAdd.setRole("ROLE_USER");
		userToAdd.setEnabled(1);
		userToAdd.setEmail(user.getEmail());
		userToAdd.setIpAddress(user.getIp_address());
		userDAO.addUser(userToAdd);
		System.out.println("USER ADDED");
	}

	@Override
	public pl.bsp.model.User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public pl.bsp.model.User findById(long id) {
		return userRepository.findById(id);
	}

	@Override
	public void update(User userToUpdate) {
		userDAO.updateUser(userToUpdate);
	}

	
}
