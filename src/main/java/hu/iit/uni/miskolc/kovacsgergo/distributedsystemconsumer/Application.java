package hu.iit.uni.miskolc.kovacsgergo.distributedsystemconsumer;

import hu.iit.uni.miskolc.kovacsgergo.distributedsystemconsumer.model.generated.ExchangeRate;
import hu.iit.uni.miskolc.kovacsgergo.distributedsystemconsumer.model.generated.ObjectFactory;
import hu.iit.uni.miskolc.kovacsgergo.distributedsystemconsumer.util.Util;
import hu.iit.uni.miskolc.kovacsgergo.distributedsystemconsumer.util.exception.ApiError;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.List;

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

        //Setting up commang line arguments
        options = new Options();
        options.addOption("a", false, "request all exchange rate data");
        options.addOption("t", false, "request test action from the service");
        options.addOption("s", false, "request single rate data from the service");
        options.addOption("rm", false, "request to remove single rate data from the service");
        options.addOption("c", false, "request to create single rate data from the service");

        // parsing arguments
        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Arguments are null");
            return;
        }

        // starting client
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
                listRequest(restTemplate, GET_ALL_RATE);

            } else if (cmd.hasOption("t")) {
                listRequest(restTemplate, TEST_RATE);

            } else if (cmd.hasOption("s")) {
                try {
                    listRequest(restTemplate, GET_RATE + cmd.getArgs()[0]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    logger.error("Argument is null");
                }

            } else if (cmd.hasOption("rm")) {
                try {
                    listRequest(restTemplate, DELETE_RATE + cmd.getArgs()[0]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    logger.error("Argument is null");
                }

            } else if (cmd.hasOption("c")) {
                try {
                    createRequest(restTemplate, CREATE_RATE, cmd.getArgs());
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    logger.error(e.getMessage());
                }
            }
            else {
                listRequest(restTemplate, GET_ALL_RATE);
            }
        };
    }

    private void listRequest(RestTemplate restTemplate, final String REQUEST_MAPPING) {
        try {
            ResponseEntity<List<Object>> rateResponse =
                    restTemplate.exchange(SERVICE_URL + REQUEST_MAPPING,
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<Object>>() {});



            if (rateResponse.getBody() != null) {
                try {
                    List<Object> rates = rateResponse.getBody();
                    ObjectFactory objectFactory = new ObjectFactory();
                    for (Object rate : rates) {
                        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) rate;
                       if(map.keySet().contains("rate")){
                           ExchangeRate exchangeRate = objectFactory.createExchangeRate();
                           exchangeRate.setRate((double)(map.get("rate")));
                           exchangeRate.setName(map.get("name").toString());
                           exchangeRate.setCode(map.get("code").toString());
                           logger.info(Util.toString(exchangeRate));
                        }else{
                           logger.error(map.get("statusCode").toString());
                           logger.error(map.get("statusCodeValue").toString());
                           logger.error(map.get("body").toString());
                        }
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }


            } else {
                logger.info("Response is null");
            }

        } catch (Exception e) {
            logger.error("Failed to connect to the server!");
        }
    }

    private void createRequest(RestTemplate restTemplate, final String REQUEST_MAPPING, String[] args) throws
            NumberFormatException, ArrayIndexOutOfBoundsException {

        ObjectFactory objectFactory = new ObjectFactory();
        ExchangeRate exchangeRate = objectFactory.createExchangeRate();

        // 1st argument testing and setting
        try {
            exchangeRate.setName(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("1st parameter is null");
        }

        // 2nd argument testing and setting
        try {
            exchangeRate.setCode(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("2nd parameter is null");
        }

        // 3rd argument testing and setting
        try {
            double rateArgument = Double.parseDouble(args[2]);
            exchangeRate.setRate(rateArgument);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("3rd parameter is null");
        } catch (NumberFormatException e) {
            throw new NumberFormatException("3rd argument is not a number");
        }

        try {
            logger.info("Sending: " + Util.toString(exchangeRate));
            restTemplate.put(SERVICE_URL + REQUEST_MAPPING, exchangeRate);

        } catch (Exception e) {
            logger.error("Failed to connect to the server!");
        }
    }
}