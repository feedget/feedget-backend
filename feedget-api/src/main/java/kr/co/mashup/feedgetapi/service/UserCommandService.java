package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.security.TokenManager;
import kr.co.mashup.feedgetapi.web.dto.SignInDto;
import kr.co.mashup.feedgetapi.web.dto.UserDto;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import kr.co.mashup.feedgetcommon.util.UniqueIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 유저 관련 비즈니스 로직 처리
 * <p>
 * Created by ethan.kim on 2018. 1. 18..
 */
@Service
@Slf4j
public class UserCommandService {

    private final UserRepository userRepository;

    private final TokenManager tokenManager;

    @Autowired
    public UserCommandService(UserRepository userRepository, TokenManager tokenManager) {
        this.userRepository = userRepository;
        this.tokenManager = tokenManager;
    }

    /**
     * sign in
     * 1. validate oauth token
     * 2. find user by email
     * 2-1. not exist user -> save user
     * 4. generate access token, refresh token
     *
     * @param dto
     * @return
     */
    @Transactional
    public SignInDto.Response signInUser(SignInDto.Create dto) {
        validateUser(dto);

        Optional<User> userOp = userRepository.findByEmail(dto.getEmail());
        User createdUser = userOp.orElseGet(() -> signUpUser(dto));

        // Todo: add signIn history

        SignInDto.Response response = new SignInDto.Response();
        response.setAccessToken(tokenManager.generateAccessToken(createdUser));
        response.setRefreshToken(tokenManager.generateRefreshToken(createdUser));

        return response;
    }

    /**
     * Sign Up
     * User 데이터를 저장한다
     *
     * @param dto
     * @return
     */
    private User signUpUser(SignInDto.Create dto) {
        // Todo: add signUp history
        User user = User.builder()
                .realName(dto.getRealName())
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .uuid(UniqueIdGenerator.getStringId())
                .oAuthToken(dto.getOAuthToken())
                .oAuthType(dto.getOAuthType())
                .build();

        return userRepository.save(user);
    }

    private void validateUser(SignInDto.Create dto) {
        // Todo: validate oauth token in dto
        // 1. oauth provider로 request
        // 2. email 등 정보랑 같은지 확인
    }

    /**
     * 유저의 닉네임 수정
     *
     * @param userId 유저 ID
     * @param dto    수정할 닉네임 데이터
     */
    @Transactional
    public void modifyUserNickname(long userId, UserDto.UpdateNickname dto) {
        Optional<User> userOp = userRepository.findByUserId(userId);
        User user = userOp.orElseThrow(() -> new NotFoundException("not found user"));

        user.changeNickname(dto.getNickname());
        userRepository.save(user);
    }
}
