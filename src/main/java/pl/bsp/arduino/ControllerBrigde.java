package pl.bsp.arduino;

import org.springframework.beans.factory.annotation.Autowired;
import pl.bsp.model.ParentalControlPolicy;
import pl.bsp.model.Resource;
import pl.bsp.services.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kamil on 24.05.2017.
 */
public class ControllerBrigde {
    static Map<String, ParentalControlThread> parentalControlThreads = new HashMap<>();
    static Map<String, ObserveMotion> observeMotionMap = new HashMap<>();


    public static void addUser(String username) {
        if (!parentalControlThreads.containsKey(username)) {
            parentalControlThreads.put(username, new ParentalControlThread(username));
        }
    }

    public static void addObserveMotion(String resourceName, String username, String arduinoIp) {
        if (!observeMotionMap.containsKey(resourceName)) {
            observeMotionMap.put(resourceName, new ObserveMotion(arduinoIp, username));
        }
    }

    public static ParentalControlThread getParentalControlThread(String username) {
        return parentalControlThreads.get(username);
    }

    public static void addPolicyToThread(ParentalControlPolicy policy) {
        parentalControlThreads.get(policy.getUser().getUsername()).addPolicy(policy);
    }

    public static void deletePolicyFromThread(ParentalControlPolicy policy) {
        parentalControlThreads.get(policy.getUser().getUsername()).deletePolicy(policy);
    }

    public static void addResource(Resource resource, String ipAddress) {
        if(resource.getResourceType().equals("Sensor")) {
            addObserveMotion(resource.getName(), resource.getUser().getUsername(),
                   ipAddress);
        } else {
            for (Map.Entry<String, ObserveMotion> entry : observeMotionMap.entrySet()) {
                if (entry.getValue().getUserName().equals(resource.getUser().getUsername())) {
                    entry.getValue().addResource(resource);
                }
            }
        }
    }
}
