package pl.bsp.entities;

/**
 * Created by Kamil on 27.04.2017.
 */
public class ParentalControlPolicy
{
    String name;
    String description;
    String resourceName;
    String action;
    String username;
    String startTime;
    String endTime;
    int repeatPatern;

    public int getRepeatPatern() {
        return repeatPatern;
    }

    public void setRepeatPatern(int repeatPatern) {
        this.repeatPatern = repeatPatern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
