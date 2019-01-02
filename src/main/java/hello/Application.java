package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String args[]) {
		SpringApplication.run(Application.class);
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			int count = 0;
			boolean messageRead = false;
			while (count < 10) {
				try {
					ResponseEntity<List<Rate>> rateResponse =
							restTemplate.exchange("http://localhost:8095/rates",
									HttpMethod.GET, null, new ParameterizedTypeReference<List<Rate>>() {
									});
					List<Rate> rates = rateResponse.getBody();
					Iterator iterator = rates.iterator();

					while (iterator.hasNext()) {
						log.info(iterator.next().toString());
					}

					messageRead = true;
				} catch (Exception e) {
					log.error("Failed to connect to the server!");
				}
				if(!messageRead) {
					TimeUnit.SECONDS.sleep(1);
					count++;
				}
				else
					return;
			}
/*			if(!messageRead){
					ResponseEntity<List<Rate>> rateResponse =
							restTemplate.exchange("https://bitpay.com/api/rates",
									HttpMethod.GET, null, new ParameterizedTypeReference<List<Rate>>() {
								});
				List<Rate> rates = rateResponse.getBody();
				Iterator iterator = rates.iterator();

				while (iterator.hasNext()) {
					log.info(iterator.next().toString());
				}
			}*/
		};
	}
}