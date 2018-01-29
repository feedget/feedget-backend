package kr.co.mashup.feedgetapi.web.dto;

import kr.co.mashup.feedgetcommon.domain.User;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * SignIn의 데이터 전달을 담당한다
 * <p>
 * Created by ethan.kim on 2018. 1. 28..
 */
public class SignInDto {

    @Data
    public static class Create {

        // 실명
        @NotBlank
        private String realName;

        // 닉네임
        @NotBlank
        @Size(min = 2, max = 10)
        private String nickname;

        // 이메일
        @NotBlank
        @Email
        private String email;

        // OAuth Access Token(카톡, FB)
        @NotBlank
        private String oAuthToken;

        // OAuth Type
        @NotNull
        private User.OAuthType oAuthType;
    }

    @Data
    public static class Response {

        // Access Token
        private String accessToken;

        // Refresh Token
        private String refreshToken;
    }
}
