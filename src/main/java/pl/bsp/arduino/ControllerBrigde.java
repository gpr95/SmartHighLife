package pl.bsp.arduino;

import java.util.HashMap;
import java.util.Map;

import pl.bsp.entities.Resource;
import pl.bsp.model.ParentalControlPolicy;
import pl.bsp.model.User;

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

    public static void addObserveMotion(String resourceName, User user, String arduinoIp) {
        if (!observeMotionMap.containsKey(resourceName)) {
            observeMotionMap.put(resourceName, new ObserveMotion(arduinoIp, user));
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

    public static void addResource(Resource resource, String ipAddress, User user) {
        if(resource.getResourceType().equals("Sensor")) {
            addObserveMotion(resource.getName(), user,
                   ipAddress);
        } else {
            for (Map.Entry<String, ObserveMotion> entry : observeMotionMap.entrySet()) {
                if (entry.getValue().getUserName().equals( resource.getUsername())) {
                    entry.getValue().addResource(resource);
                }
            }
        }
    }
}
