package pl.bsp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bsp.model.ParentalControlPolicy;
import pl.bsp.model.ParentalControlPolicyDAO;
import pl.bsp.model.ParentalControlPolicyRepository;
import pl.bsp.model.User;

import java.util.List;

/**
 * Created by Kamil on 2017-05-22.
 */
@Service
public class ParentalControlPolicyServiceImpl implements ParentalControlPolicyService {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    ParentalControlPolicyDAO parentalControlPolicyRepository;
    
    @Autowired
    ParentalControlPolicyRepository parentalControlPolicyRepositoryInterface;

    @Override
    public boolean add(ParentalControlPolicy parentalControlPolicy) {
        try {
            parentalControlPolicyRepository.addParentalControlPolicy(parentalControlPolicy);
            User owner = userService.findByUsername(parentalControlPolicy.getUser().getUsername());
            owner.getPolicies().add(parentalControlPolicy);
            userService.update(owner);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    @Override
    public List<ParentalControlPolicy> findByUsername(String username) {
        return userService.findByUsername(username).getPolicies();
    }

    @Override
    public List<ParentalControlPolicy> findByUserId(long id) {
        return userService.findById(id).getPolicies();
    }

	@Override
	public boolean deletePolicy(int resourceId, String ownerUsername) {
		User owner = userService.findByUsername(ownerUsername);
		ParentalControlPolicy policyToDelete = parentalControlPolicyRepositoryInterface.findById(resourceId);
		boolean status = owner.getPolicies().remove(policyToDelete);
		userService.update(owner);
		List<ParentalControlPolicy> deletedPolicies = parentalControlPolicyRepositoryInterface.deleteById((long)resourceId);
		if(status){
			return true;
		}else{
			return false;
		}
	}
}
