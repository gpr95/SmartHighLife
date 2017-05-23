package pl.bsp.arduino;

public class ArduinoReader {
	private static ArduinoReader instance = null;

	/** Exists only to defeat instantiation. (SINGLETON) */
	protected ArduinoReader() {
	}

	public static ArduinoReader getInstance() {
		if (instance == null) {
			instance = new ArduinoReader();
		}
		return instance;
	}
	
	public void startReadingDeviceValue() {
		NumberOfPeopleInRoomCounterThread runnable = new NumberOfPeopleInRoomCounterThread(8888);
		Thread thread = new Thread(runnable);
		thread.start();
	}
}
