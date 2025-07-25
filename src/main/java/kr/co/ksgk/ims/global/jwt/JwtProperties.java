package kr.co.ksgk.ims.global.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {

    private String secretKey = "";
    private long accessExpirationTime;
    private long refreshExpirationTime;
}
