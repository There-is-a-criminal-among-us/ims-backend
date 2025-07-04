package kr.co.ksgk.ims.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );
        Server server = new Server().url("http://localhost:8080");
        return new OpenAPI()
                .info(apiInfo())
                .components(components)
                .addServersItem(server)
                .addSecurityItem(securityRequirement);
    }

    private Info apiInfo() {
        return new Info()
                .title("재고 관리 프로그램") // API의 제목
                .description("임시 배포 버전") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}
