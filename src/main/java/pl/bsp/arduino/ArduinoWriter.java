package pl.bsp.arduino;

public class ArduinoWriter {
	private static ArduinoWriter instance = null;

	/** Exists only to defeat instantiation. (SINGLETON) */
	protected ArduinoWriter() {
	}

	public static ArduinoWriter getInstance() {
		if (instance == null) {
			instance = new ArduinoWriter();
		}
		return instance;
	}

}
