package pl.bsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import pl.bsp.arduino.NumberOfPeopleInRoomCounterThread;

@SpringBootApplication
@EnableOAuth2Sso
public class SmartHighLifeApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SmartHighLifeApplication.class, args);
	}
}
