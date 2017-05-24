package pl.bsp.arduino;

import pl.bsp.enums.RepeatPatern;

/**
 * Created by Kamil on 2017-05-22.
 */
public class Event {

    long time;
    long resourceId;;
    String action;
    RepeatPatern repeatPatern;
    String arduinoIp;

    public Event(long time, long resourceId, String action, RepeatPatern repeatPatern, String arduinoIp) {
        this.time = time;
        this.resourceId = resourceId;
        this.action = action;
        this.repeatPatern = repeatPatern;
        this.arduinoIp = arduinoIp;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public RepeatPatern getRepeatPatern() {
        return repeatPatern;
    }

    public void setRepeatPatern(RepeatPatern repeatPatern) {
        this.repeatPatern = repeatPatern;
    }

    public String getArduinoIp() {
        return arduinoIp;
    }

    public void setArduinoIp(String arduinoIp) {
        this.arduinoIp = arduinoIp;
    }
}
