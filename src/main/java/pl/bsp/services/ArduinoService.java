package pl.bsp.services;

public interface ArduinoService {
	String findArduinoInNetwork();
	void turnOnTheLight(String arduinoIp, int resourceId);
	void turnOffTheLight(String arduinoIp, int resourceId);
	String getResourceValue(String arduinoIp, int resourceId);
	void addNewResource(String arduinoIp, int resourceId);
	void syncHumanCounter(String arduinoIp, int resourceId);
}
