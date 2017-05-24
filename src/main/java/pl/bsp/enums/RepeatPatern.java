package pl.bsp.enums;

public enum RepeatPatern {
	ONCE(0), DAILY(1), WEEKLY(2), MONTHLY(3), YEARLY(4), EVERY_MINUTE(5), EVERY_SECOND(6), EVERY_MILISECOND(7), HOURLY(8);
	int intValue;
	private RepeatPatern(int value) {
		intValue = value;
	}

}
