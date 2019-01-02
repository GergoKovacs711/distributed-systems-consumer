package consumer;

import org.apache.commons.cli.*;
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
    static private Options options;
    static private CommandLine cmd;

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static final String SERVICE_URL = "http://localhost:8095";
    private static final String GET_ALL_RATE = "/rates/all";
    private static final String GET_RATE = "/rates/single/";
    private static final String CREATE_RATE = "/rates/create";
    private static final String DELETE_RATE = "/rates/delete/";
    private static final String TEST_RATE = "/rates/test";

    public static void main(String args[]) {
        options = new Options();
        options.addOption("a", false, "request all rates data");
        options.addOption("t", false, "request test action from the service");
        options.addOption("s", false, "request single rate data from the service");

        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        SpringApplication.run(Application.class);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
        return args -> {
            if (cmd.hasOption("a")) {
                basicRequest(restTemplate, GET_ALL_RATE);
            }
            else if(cmd.hasOption("t")){
                basicRequest(restTemplate, TEST_RATE);
            }
        };
    }

    private void basicRequest(RestTemplate restTemplate, final String requestMapping) {
        try {
            ResponseEntity<List<Rate>> rateResponse =
                    restTemplate.exchange(SERVICE_URL + requestMapping,
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<Rate>>() {
                            });
            if(rateResponse.getBody() != null) {
                List<Rate> rates = rateResponse.getBody();
                Iterator iterator = rates.iterator();

                while (iterator.hasNext()) {
                    logger.info(iterator.next().toString());
                }
            }

        } catch (Exception e) {
            logger.error("Failed to connect to the server!");
        }
    }
}