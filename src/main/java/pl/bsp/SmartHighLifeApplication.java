package pl.bsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import pl.bsp.arduino.UdpClientThread;

@SpringBootApplication
public class SmartHighLifeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartHighLifeApplication.class, args);
		UdpClientThread runnable = new UdpClientThread(8888);
		Thread thread = new Thread(runnable);
		thread.start();
	}
}
