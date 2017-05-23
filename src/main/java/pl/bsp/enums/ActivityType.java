package pl.bsp.enums;

import javax.persistence.Entity;

public enum ActivityType {
	LEAVE(0), ENTER(1);
	int intValue;

	private ActivityType(int value) {
		intValue = value;
	}

	public String toString() {
		if (intValue == 0)
			return "LEAVE";
		else if (intValue == 1)
			return "ENTER";
		else
			return "NOT DEFINED";
	}
}
