package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.controller.StockController;
//import org.junit.Assert;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

//import javax.inject.Inject;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class StockServiceIntegrationTest {

    @Autowired
    private StockServiceImpl stockService;

    @Autowired
    private WebTestClient client;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    Flyway flyway;

    @Test
    public void registrationWorksThroughAllLayers() throws Exception {

        StockController stockController = new StockController();
        stockController.setStockService(stockService);

        webTestClient
                .get()
                .uri("/stocks/tsl")
                .exchange()
               .expectStatus().isOk();

        //        UserResource user = new UserResource("Zaphod", "zaphod@galaxy.net");

//        WebTestClient
//                .bindToServer().build()//create("http://localhost:8080");
////        Mono<StockPrize> stockMono =
//                .get()
//                .uri("/stocks")
////                .retrieve()
//                .exchange();
////                .bodyToFlux(StockPrize.class)
////                .last();
////        .expectBody().

//        StockPrize unblocked = stockMono.block();
//        client.get("/id")
//        client.get();
//
//        mockMvc.perform(post("/forums/{forumId}/register", 42L)
//                .contentType("application/json")
//                .param("sendWelcomeMail", "true")
//                .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isOk());
//
//        UserEntity userEntity = userRepository.findByName("Zaphod");
        Assertions.assertEquals ("", "");
    }

}
