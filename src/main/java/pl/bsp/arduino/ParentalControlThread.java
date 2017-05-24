package pl.bsp.arduino;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.bsp.enums.RepeatPatern;
import pl.bsp.model.ParentalControlPolicy;
import pl.bsp.services.ArduinoService;
import pl.bsp.services.ArduinoServiceImpl;
import pl.bsp.services.ResourceServiceImpl;
import pl.bsp.services.UserServiceImpl;

import static java.lang.Math.abs;

/**
 * Created by Kamil on 2017-05-22.
 */

public class ParentalControlThread {
	private final static String TURN_OFF = "turn off";
	private final static String TURN_ON = "turn on";
    
    @Autowired
    private ResourceServiceImpl resourceService;
    @Autowired
    private UserServiceImpl userService;
    private ArduinoService arduinoService = new ArduinoServiceImpl();
    private String username;

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
        (new Thread(new QueueCheckingThread())).start();

    }

    public void addPolicy(ParentalControlPolicy policy) {
        System.out.println("add policy " + policy.getDescription());
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
                            resourceService.findByNameAndUser(policy.getResourceName(), policy.getUser()).getId(),
                            policy.getAction(), RepeatPatern.values()[policy.getRepeatPatern()],
                            userService.findByUsername(policy.getUser().getUsername()).getIpAddress())
            );
            if(startDate != endDate) {
                if (policy.getAction().equals("turn on")) {
                    policyQueue.add(new Event(startDate.getTime(),
                            resourceService.findByNameAndUser(policy.getResourceName(), policy.getUser()).getId(),
                            TURN_OFF, RepeatPatern.values()[policy.getRepeatPatern()],
                            userService.findByUsername(policy.getUser().getUsername()).getIpAddress())
                    );
                } else {
                    policyQueue.add(new Event(startDate.getTime(),
                            resourceService.findByNameAndUser(policy.getResourceName(), policy.getUser()).getId(),
                            TURN_ON,RepeatPatern.values()[policy.getRepeatPatern()],
                            userService.findByUsername(policy.getUser().getUsername()).getIpAddress())
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
            while (true) {
                synchronized (policyQueue) {
                    if ( abs(policyQueue.peek().getTime() - new Date().getTime()) < 1000) {
                        Event event = policyQueue.poll();
                        if (event.getAction().equals(TURN_ON)) {
                            arduinoService.turnOnTheLight(event.getArduinoIp(), (int) event.getResourceId());
                        } else if (event.getAction().equals(TURN_OFF)) {
                            arduinoService.turnOffTheLight(event.getArduinoIp(), (int) event.getResourceId());
                        }
                        /**TODO: wysyłanie odpowiedniego żądania do arduino uno
                         * resource id jest w evencie
                         * akcja ( wlaczenie/wylaczenie zasobu) jest zdefiniowane w polu action
                         * KAMIL ZROBI: jesli repeatAction nie once to ponownie dodac event do kolejki ze zwiekszonym czasem
                         *
                         * */

                        if (event.getRepeatPatern() != RepeatPatern.ONCE) {
                            switch (event.getRepeatPatern()) {
                                case EVERY_MILISECOND:
                                    event.setTime(event.getTime() + 1);
                                    break;
                                case EVERY_SECOND:
                                    event.setTime(event.getTime() + 1000);
                                    break;
                                case EVERY_MINUTE:
                                    event.setTime(event.getTime() + 60 * 1000);
                                    break;
                                case DAILY:
                                    event.setTime(event.getTime() + 3600 * 1000);
                                    break;
                                case WEEKLY:
                                    event.setTime(event.getTime() + 7 * 3600 * 1000);
                                    break;
                                case MONTHLY:
                                    event.setTime(event.getTime() + 30 * 7 * 3600 * 1000);
                                    break;
                            }
                            policyQueue.add(event);
                        }
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
