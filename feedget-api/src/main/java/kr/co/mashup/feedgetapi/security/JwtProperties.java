package kr.co.mashup.feedgetapi.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by ethan.kim on 2018. 1. 21..
 */
@Component
@Getter
public class JwtProperties {

    /**
     * http header key
     */
    @Value(value = "${jwt.http-header}")
    private String httpHeader;

    /**
     * access token 만료시간(s)
     */
    @Value(value = "${jwt.expiration-time}")
    private long expirationTime;

    /**
     * refersh token 만료시간(s)
     */
    @Value(value = "${jwt.refresh.expiration-time}")
    private long refreshExpirationTime;

    /**
     * 발급자
     */
    @Value(value = "${jwt.issuer}")
    private String issuer;

    /**
     * sign key
     */
    @Value(value = "${jwt.signature}")
    private String signature;
}
