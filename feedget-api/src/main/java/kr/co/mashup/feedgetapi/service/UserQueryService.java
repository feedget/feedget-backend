package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.UserDto;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 유저 정보 조회 담당
 * <p>
 * Created by ethan.kim on 2018. 5. 12..
 */
@Service
public class UserQueryService {

    private final UserRepository userRepository;

    @Autowired
    public UserQueryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 유저 정보 조회
     *
     * @param userId
     * @param uuid
     * @return
     */
    @Transactional(readOnly = true)
    public UserDto.DetailResponse readUserInfo(long userId, String uuid) {
        Optional<User> userOp;

        if (StringUtils.equals(uuid, "me")) {  // 내정보 조회
            userOp = userRepository.findByUserId(userId);
        } else {  // 다른 유저 정보 조회
            userOp = userRepository.findByUuid(uuid);
        }

        User user = userOp.orElseThrow(() -> new NotFoundException("not found user"));
        return UserDto.DetailResponse.newDetailResponse(user);
    }
}
