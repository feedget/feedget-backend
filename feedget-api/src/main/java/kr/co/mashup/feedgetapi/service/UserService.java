package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.web.dto.UserDto;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import kr.co.mashup.feedgetcommon.util.UniqueIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by ethan.kim on 2018. 1. 18..
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * sign in
     * 1. validate oauth token
     * 2. find user by email
     * 3. not exist user -> save user
     * 4. generate access token
     *
     * @param dto
     * @return
     */
    @Transactional
    public String signInUser(UserDto.SignIn dto) {
        validateUser(dto);

        Optional<User> userOp = userRepository.findByEmail(dto.getEmail());
        User createdUser = userOp.orElseGet(() -> signUpUser(dto));

        // Todo: add signIn history

        return generateAccessToken(createdUser);
    }

    private User signUpUser(UserDto.SignIn dto) {
        // Todo: add signUp history
        User user = new User(dto.getRealName(), dto.getNickname(), dto.getEmail(), UniqueIdGenerator.getStringId(), dto.getOAuthToken(), dto.getOAuthType());
        return userRepository.save(user);
    }

    private void validateUser(UserDto.SignIn dto) {
        // Todo: validate oauth token in dto
        // 1. oauth provider로 request
        // 2. email 등 정보랑 같은지 확인
    }

    private String generateAccessToken(User user) {
        // Todo: access token 발급

        return "accessToken";
    }
}
