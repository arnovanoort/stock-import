package nl.arnovanoort.stockreader.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HealthController {

    Logger logger = LoggerFactory.getLogger(HealthController.class);

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    private Mono<String> getHealth() {
        logger.debug("executing health check.");
        return Mono.empty();
    }
}
