package pl.bsp.arduino;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;

import pl.bsp.enums.RepeatPatern;
import pl.bsp.model.ParentalControlPolicy;
import pl.bsp.services.ResourceService;
import pl.bsp.services.UserService;

/**
 * Created by Kamil on 2017-05-22.
 */

public class ParentalControlThread {
	private final static String TURN_OFF = "turn off";
	private final static String TURN_ON = "turn on";
    
    
    @Autowired
    private UserService userService;
    private String username;
    private ResourceService resourceService;
    private PriorityBlockingQueue<Event> policyQueue;
    
    //przerobić to na kolejke eventow
    ParentalControlThread(String username) {
        DateComparator dateComparator = new DateComparator();
        List<ParentalControlPolicy> policies = userService.findByUsername(username).getPolicies();
        policyQueue = new PriorityBlockingQueue<>(policies.size(),dateComparator);
        this.username = username;

        for (ParentalControlPolicy policy : policies) {
            addPolicy(policy);
        }

    }

    public void addPolicy(ParentalControlPolicy policy) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date startDate = null;
        java.util.Date endDate = null;
        try {
            startDate = format.parse(policy.getStartTime());
            endDate = format.parse(policy.getEndTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        synchronized (policyQueue) {
            policyQueue.add(
                    new Event(startDate.getTime(),
                            resourceService.findByName(policy.getResourceName()).getId(),
                            policy.getAction(), RepeatPatern.values()[policy.getRepeatPatern()])
            );
            if(startDate != endDate) {
                if (policy.getAction().equals("turn on")) {
                    policyQueue.add(new Event(startDate.getTime(),
                            resourceService.findByName(policy.getResourceName()).getId(),
                            TURN_OFF, RepeatPatern.values()[policy.getRepeatPatern()])
                    );
                } else {
                    policyQueue.add(new Event(startDate.getTime(),
                            resourceService.findByName(policy.getResourceName()).getId(),
                            TURN_ON,RepeatPatern.values()[policy.getRepeatPatern()])
                    );
                }

            }
        }
    }

    public void deletePolicy(ParentalControlPolicy policy) {

        synchronized (policyQueue) {
            policyQueue.remove(policy);
        }
    }

    public class QueueCheckingThread implements Runnable {

        public void run() {
            synchronized (policyQueue) {
                if ((policyQueue.peek().getTime() - new Date().getTime()) < 1000) {
                	Event event = policyQueue.poll();
                	if(event.getAction().equals(TURN_ON)) {
                		
                	} else if (event.getAction().equals(TURN_OFF)) {
                		
                	}
                    /**TODO: wysyłanie odpowiedniego żądania do arduino uno
                     * resource id jest w evencie
                     * akcja ( wlaczenie/wylaczenie zasobu) jest zdefiniowane w polu action
                     * KAMIL ZROBI: jesli repeatAction nie once to ponownie dodac event do kolejki ze zwiekszonym czasem
                     *
                     * */
                }
            }
        }

    }


}
