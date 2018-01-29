package kr.co.mashup.feedgetapi.security;

import io.jsonwebtoken.*;
import kr.co.mashup.feedgetapi.exception.InvalidTokenException;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.util.UniqueIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * token 관련 비즈니스 로직 담당
 *
 * Created by ethan.kim on 2018. 1. 21..
 */
@Service
@Slf4j
public class TokenManager {

    /*** REGISTERED CLAIM ***/
    public static final String SUB_ACCESS_TOKEN = "access_token";
    public static final String SUB_REFRESH_TOKEN = "refresh_token";

    public static final String AUDIENCE_UNKNOWN = "unknown";
    public static final String AUDIENCE_WEB = "web";
    public static final String AUDIENCE_MOBILE = "mobile";
    public static final String AUDIENCE_TABLET = "tablet";

    /*** private CLAIM ***/
    public static final String CLAIM_KEY_USER_ID = "user_id";

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * token 검증
     * 1. signature가 같은지
     * 2. 만료된 토큰인지
     * 3. issuer가 같은지
     *
     * @param token
     * @return 정상 토큰이면 true
     * @throws InvalidTokenException 비정상 토큰일 때(만료, signature가 다르다, issuer가 다르다)
     */
    public boolean validateToken(String token) throws InvalidTokenException {
        Claims claims = getClaims(token);

        if (!StringUtils.equals(claims.getIssuer(), jwtProperties.getIssuer())) {
            throw new InvalidTokenException("invalid token");
        }

        return true;
    }

    /**
     * token에서 claims 추출
     *
     * @param token token
     * @return claims
     * @throws InvalidTokenException 만료된 토큰이거나 signature가 다른 토큰일 때
     */
    private Claims getClaims(String token) throws InvalidTokenException {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtProperties.getSignature())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException | ExpiredJwtException e) {
            throw new InvalidTokenException("invalid token");
        } catch (Exception e) {
            log.error("invalid token {}", token, e);
            throw new InvalidTokenException("invalid token");
        }
    }

    /**
     * claims로 token 생성
     *
     * @param claims
     * @return
     */
    private String generateToken(Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .setExpiration(generateExpriationDate(expiration))
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSignature())
                .compact();
    }

    /**
     * claims로 token 생성
     *
     * @param claims
     * @param subject    제목
     * @param expiration 만료시간
     * @return
     */
    private String generateToken(Map<String, Object> claims, String subject, Date expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)  // sub - 제목
                .setAudience(AUDIENCE_MOBILE)  // aud - 대상자
                .setId(UniqueIdGenerator.getStringId())  // jti - ID, 주로 중복적인 처리 방지를 위해 사용(1회용 토큰에 유용)
                .setIssuer(jwtProperties.getIssuer())  // iss - 발급자
                .setIssuedAt(new Date())  // iat - 발급된 시간
                .setNotBefore(new Date())  // nbf - 활성 날짜, 날짜가 지나기 전까지 토큰이 처리되지 않는다
                .setExpiration(expiration)  // exp - 만료시간
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSignature())
                .compact();
    }

    /**
     * User Entity로 token 생성
     * 노출시킬 정보만 추가한다
     *
     * @param user
     * @return
     */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        // public claim
        claims.put(jwtProperties.getIssuer() + "/jwt_claims", true);

        // private claim
        claims.put(CLAIM_KEY_USER_ID, user.getUuid());

        return generateToken(claims, SUB_ACCESS_TOKEN, generateExpriationDate(jwtProperties.getExpirationTime()));
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        // public claim
        claims.put(jwtProperties.getIssuer() + "/jwt_claims", true);

        // private claim
        claims.put(CLAIM_KEY_USER_ID, user.getUuid());

        return generateToken(claims, SUB_REFRESH_TOKEN, generateExpriationDate(jwtProperties.getRefreshExpirationTime()));
    }

    /**
     * access token 갱신
     * 만료일이 지나지 않은 토큰을 갱신한다
     *
     * @param token
     * @return
     */
    public String refreshAccessToken(String token) {
        Claims claims = getClaims(token);
        return generateToken(claims, jwtProperties.getExpirationTime());
    }

    /**
     * refresh token 갱신
     *
     * @param refreshToken
     * @return
     */
    public String refreshRefreshToken(String refreshToken) {
        Claims claims = getClaims(refreshToken);
        return generateToken(claims, jwtProperties.getRefreshExpirationTime());
    }

    /**
     * 만료일 생성
     *
     * @return
     */
    private Date generateExpriationDate(long expiration) {
        long expirationTime = expiration * 1000;
        return new Date(System.currentTimeMillis() + expirationTime);
    }

    /******* private claim *******/
    /**
     * token에서 유저의 UUID 조회
     *
     * @param token
     * @return
     */
    public String getUserUuid(String token) {
        Claims claims = getClaims(token);
        return claims.get(CLAIM_KEY_USER_ID, String.class);
    }
}
