package pl.bsp.services;

import pl.bsp.model.ParentalControlPolicy;

import java.util.List;

/**
 * Created by Kamil on 2017-05-22.
 */
public interface ParentalControlPolicyService {
    boolean add(ParentalControlPolicy parentalControlPolicy);
    List<pl.bsp.model.ParentalControlPolicy> findByUsername(String username);
    List<pl.bsp.model.ParentalControlPolicy> findByUserId(long id);
    public boolean deletePolicy(int resourceId, String ownerUsername);

}
