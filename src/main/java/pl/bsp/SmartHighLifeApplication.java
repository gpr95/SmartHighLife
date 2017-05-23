package pl.bsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import pl.bsp.arduino.NumberOfPeopleInRoomCounterThread;

@SpringBootApplication
public class SmartHighLifeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartHighLifeApplication.class, args);
	}
}
