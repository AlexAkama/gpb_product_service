package org.example.productservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI appOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GPB Product Service")
                        .version("1.0.0")
                        .contact(
                                new Contact()
                                        .email("gremcox@bk.ru")
                                        .url("https://github.com/AlexAkama/gpb_product_service")
                                        .name("Алексей Маслов")
                        )
                );
    }


}
