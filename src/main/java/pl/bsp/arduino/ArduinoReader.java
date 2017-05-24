package pl.bsp.arduino;

public class ArduinoReader {
	private static ArduinoReader instance = null;
	public static boolean iNeedToCheckDatabase = false;
	/** Exists only to defeat instantiation. (SINGLETON) */
	protected ArduinoReader() {
	}

	public static ArduinoReader getInstance() {
		if (instance == null) {
			instance = new ArduinoReader();
		}
		return instance;
	}
	
}
