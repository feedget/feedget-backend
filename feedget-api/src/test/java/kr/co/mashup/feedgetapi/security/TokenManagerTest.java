package kr.co.mashup.feedgetapi.security;

import io.jsonwebtoken.*;
import kr.co.mashup.feedgetapi.exception.InvalidTokenException;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.util.UniqueIdGenerator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static kr.co.mashup.feedgetapi.security.TokenManager.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by ethan.kim on 2018. 1. 25..
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenManagerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private TokenManager sut;

    private static final String UUID = UniqueIdGenerator.getStringId();

    private static JwtBuilder jwtBuilder;

    private static Map<String, Object> claims;

    @BeforeClass
    public static void setUpClass() {
        jwtBuilder = Jwts.builder();
        claims = new HashMap<>();
    }

    @Before
    public void setUp() {
        when(jwtProperties.getHttpHeader()).thenReturn("Authorization");
        when(jwtProperties.getExpirationTime()).thenReturn(10000000L);
        when(jwtProperties.getRefreshExpirationTime()).thenReturn(30000000L);
        when(jwtProperties.getIssuer()).thenReturn("http://mash-up.co.kr/feedget");
        when(jwtProperties.getSignature()).thenReturn("testSignature");

        claims.put(jwtProperties.getIssuer() + "/jwt_claims", true);
        claims.put(CLAIM_KEY_USER_ID, UUID);
    }

    @Test
    public void validateToken_토큰_검증_성공() {
        // given : 정상적인 토큰으로
        String accessToken = generateAccessToken();

        // when : 토큰을 검증하면
        boolean result = sut.validateToken(accessToken);

        // then : 검증에 성공한다
        assertTrue(result);
    }

    @Test
    public void validateToken_만료된_access_token이라_검증_실패() {
        expectedException.expect(InvalidTokenException.class);
        expectedException.expectMessage("invalid token");

        // given : 만료된 토큰으로
        String expiredAccessToken = generateExpiredAccessToken();

        // when : 토큰을 검증하면
        sut.validateToken(expiredAccessToken);

        // then : 만료된 토큰이라 실패
    }

    @Test
    public void validateToken_signature가_다른_토큰이라_검증_실패() {
        expectedException.expect(InvalidTokenException.class);
        expectedException.expectMessage("invalid token");

        // given : signature가_다른 토큰으로
        String invalidSignatureAccessToken = jwtBuilder
                .setClaims(claims)
                .setSubject(SUB_ACCESS_TOKEN)
                .setAudience(AUDIENCE_MOBILE)
                .setId(UniqueIdGenerator.getStringId())
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000))
                .signWith(SignatureAlgorithm.HS512, "signature")
                .compact();

        // when : 토큰을 검증하면
        sut.validateToken(invalidSignatureAccessToken);

        // then : signature가 다른 토큰이라 실패
    }

    @Test
    public void validateToken_토큰_검증_issuer가_다른_토큰이라_실패() {
        // Todo: 로직 구현
        expectedException.expect(InvalidTokenException.class);
        expectedException.expectMessage("invalid token");

        // given : issuer가 다른 token으로
        String invalidIssuerAccessToken = jwtBuilder
                .setClaims(claims)
                .setSubject(SUB_ACCESS_TOKEN)
                .setAudience(AUDIENCE_MOBILE)
                .setId(UniqueIdGenerator.getStringId())
                .setIssuer("hacker")
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000))
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSignature())
                .compact();

        // when : 토큰을 검증하면
        sut.validateToken(invalidIssuerAccessToken);

        // then : issuer가 다른 토큰이라 실패
    }

    @Test
    public void generateAccessToken_access_token_생성_성공() {
        // given : 유저 정보로
        String uuid = UniqueIdGenerator.getStringId();
        User user = new User();
        user.setUuid(uuid);

        // when : access token을 생성하면
        String token = sut.generateAccessToken(user);

        // then : 생성된다
        assertThat(token)
                .isNotEmpty();

        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSignature())
                .parseClaimsJws(token)
                .getBody();

        assertEquals(uuid, claims.get(CLAIM_KEY_USER_ID));
        assertEquals(SUB_ACCESS_TOKEN, claims.getSubject());
        assertEquals(AUDIENCE_MOBILE, claims.getAudience());
        assertEquals(jwtProperties.getIssuer(), claims.getIssuer());
    }

    @Test
    public void generateRefreshToken_refresh_token_생성_성공() {
        // given : 유저 정보로
        String uuid = UniqueIdGenerator.getStringId();
        User user = new User();
        user.setUuid(uuid);

        // when : refresh token을 생성하면
        String token = sut.generateRefreshToken(user);

        // then : 생성된다
        assertThat(token)
                .isNotEmpty();

        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSignature())
                .parseClaimsJws(token)
                .getBody();

        assertEquals(uuid, claims.get(CLAIM_KEY_USER_ID));
        assertEquals(SUB_REFRESH_TOKEN, claims.getSubject());
        assertEquals(AUDIENCE_MOBILE, claims.getAudience());
        assertEquals(jwtProperties.getIssuer(), claims.getIssuer());
    }

    @Test
    public void refreshAccessToken_access_token_갱신_성공() {
        // given : 정상 access token으로
        String accessToken = generateAccessToken();

        // when : access token을 갱신하면
        String refreshedAccessToken = sut.refreshAccessToken(accessToken);

        // then : expiredAt이 연장된다
        assertThat(refreshedAccessToken)
                .isNotEmpty();

        JwtParser jwtParser = Jwts.parser()
                .setSigningKey(jwtProperties.getSignature());

        Date expiredAt = jwtParser
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();

        Date refreshedExpiredAt = jwtParser
                .parseClaimsJws(refreshedAccessToken)
                .getBody()
                .getExpiration();

        assertTrue(expiredAt.before(refreshedExpiredAt));
    }

    @Test
    public void refreshAccessToken_만료된_access_token이라_갱신_실패() {
        expectedException.expect(InvalidTokenException.class);
        expectedException.expectMessage("invalid token");

        // given : 만료된 access token으로
        String expiredAccessToken = generateExpiredAccessToken();

        // when : access token을 갱신하면
        String refreshedAccessToken = sut.refreshAccessToken(expiredAccessToken);

        // then : 만료된 토큰이라 실패한다
    }

    @Test
    public void refreshRefreshToken_refresh_token_갱신_성공() {
        // given : 정상 refresh token으로
        String refreshToken = generateRefreshToken();

        // when : refresh token을 갱신하면
        String refreshedRefreshToken = sut.refreshRefreshToken(refreshToken);

        // then : expiredAt이 연장된다
        assertThat(refreshedRefreshToken)
                .isNotEmpty();

        JwtParser jwtParser = Jwts.parser()
                .setSigningKey(jwtProperties.getSignature());

        Date expiredAt = jwtParser
                .parseClaimsJws(refreshToken)
                .getBody()
                .getExpiration();

        Date refreshedExpiredAt = jwtParser
                .parseClaimsJws(refreshedRefreshToken)
                .getBody()
                .getExpiration();

        assertTrue(expiredAt.before(refreshedExpiredAt));
    }

    @Test
    public void refreshRefreshToken_만료된_refresh_token이라_갱신_실패() {
        expectedException.expect(InvalidTokenException.class);
        expectedException.expectMessage("invalid token");

        // given : 만료된 refresh token으로
        String token = jwtBuilder
                .setClaims(claims)
                .setSubject(SUB_REFRESH_TOKEN)
                .setAudience(AUDIENCE_MOBILE)
                .setId(UniqueIdGenerator.getStringId())
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSignature())
                .compact();

        // when : refresh token을 갱신하면
        String refreshedRefreshToken = sut.refreshRefreshToken(token);

        // then : 만료된 토큰이라 실패한다
    }

    @Test
    public void getUserUuid_access_token에서_uuid_조회_성공() {
        // given : 정상 access token으로
        String accessToken = generateAccessToken();

        // when : uuid를 조회하면
        String resultUuid = sut.getUserUuid(accessToken);

        // then : 조회된다
        assertEquals(resultUuid, UUID);
    }

    @Test
    public void getUserUuid_만료된_access_token이라_uuid_조회_실패() {
        expectedException.expect(InvalidTokenException.class);
        expectedException.expectMessage("invalid token");

        // given : 만료된 access token으로
        String expiredAccessToken = generateExpiredAccessToken();

        // when : uuid를 조회하면
        String resultUuid = sut.getUserUuid(expiredAccessToken);

        // then : 만료된 토큰이라 실패한다
    }

    /**
     * 정상 access token 생성
     *
     * @return
     */
    private String generateAccessToken() {
        return jwtBuilder
                .setClaims(claims)
                .setSubject(SUB_ACCESS_TOKEN)
                .setAudience(AUDIENCE_MOBILE)
                .setId(UniqueIdGenerator.getStringId())
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000))
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSignature())
                .compact();
    }

    /**
     * 정상 refresh token 생성
     *
     * @return
     */
    private String generateRefreshToken() {
        return jwtBuilder
                .setClaims(claims)
                .setSubject(SUB_REFRESH_TOKEN)
                .setAudience(AUDIENCE_MOBILE)
                .setId(UniqueIdGenerator.getStringId())
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000))
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSignature())
                .compact();
    }

    /**
     * 만료된 access token 생성
     *
     * @return
     */
    private String generateExpiredAccessToken() {
        return jwtBuilder
                .setClaims(claims)
                .setSubject(SUB_ACCESS_TOKEN)
                .setAudience(AUDIENCE_MOBILE)
                .setId(UniqueIdGenerator.getStringId())
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSignature())
                .compact();
    }
}
