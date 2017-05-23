package pl.bsp.arduino;

import pl.bsp.enums.RepeatPatern;
import pl.bsp.model.ParentalControlPolicy;
import pl.bsp.services.ResourceService;
import pl.bsp.services.UserService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Kamil on 2017-05-22.
 */

public class ParentalControlThread {
    String username;
    UserService userService;
    ResourceService resourceService;
    PriorityBlockingQueue<Event> policyQueue;
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
                            "turn off", RepeatPatern.values()[policy.getRepeatPatern()])
                    );
                } else {
                    policyQueue.add(new Event(startDate.getTime(),
                            resourceService.findByName(policy.getResourceName()).getId(),
                            "turn on",RepeatPatern.values()[policy.getRepeatPatern()])
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
