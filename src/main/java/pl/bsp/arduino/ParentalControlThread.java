package pl.bsp.arduino;

import pl.bsp.model.ParentalControlPolicy;
import pl.bsp.services.UserService;

import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Kamil on 2017-05-22.
 */

public class ParentalControlThread {
    String username;
    UserService userService;
    PriorityQueue<ParentalControlPolicy> policyQueue;
    //przerobić to na kolejke eventow
    ParentalControlThread(String username) {
        DateComparator dateComparator = new DateComparator();
        List<ParentalControlPolicy> policies = userService.findByUsername(username).getPolicies();
        policyQueue = new PriorityQueue<ParentalControlPolicy>(policies.size(),dateComparator);

        this.username = username;
        for (ParentalControlPolicy policy : policies) {
            policyQueue.add(policy);
        }

    }

    public void addPolicy(ParentalControlPolicy policy) {
        policyQueue.add(policy);
    }

    public void deletePolicy(ParentalControlPolicy policy) {
        policyQueue.remove(policy);
    }


}
