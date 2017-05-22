package pl.bsp.services;

import org.springframework.stereotype.Service;
import pl.bsp.model.ParentalControlPolicy;

import java.util.List;

/**
 * Created by Kamil on 2017-05-22.
 */
@Service
public class ParentalControlPolicyServiceImpl implements ParentalControlPolicyService {
    @Override
    public boolean add(ParentalControlPolicy parentalControlPolicy) {
        return false;
    }

    @Override
    public List<ParentalControlPolicy> findByUsername(String username) {
        return null;
    }

    @Override
    public List<ParentalControlPolicy> findByUserId(long id) {
        return null;
    }
}
