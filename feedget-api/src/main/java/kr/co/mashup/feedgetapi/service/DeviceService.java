package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.DeviceDto;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by ethan.kim on 2018. 3. 16..
 */
@Service
public class DeviceService {

    private final UserRepository userRepository;

    @Autowired
    public DeviceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * cloud messaging device를 등록한다
     *
     * @param userId
     * @param dto
     */
    @Transactional
    public void registerCloudMessagingDevice(long userId, DeviceDto.UpdateCloudMsgRegId dto) {
        Optional<User> userOp = userRepository.findByUserId(userId);
        User user = userOp.orElseThrow(() -> new NotFoundException("not found user"));

        if (!user.isSameCloudMsgRegId(dto.getCloudMsgRegToken())) {
            user.changeCloudMsgRegId(dto.getCloudMsgRegToken());
            userRepository.save(user);

            // TODO: 2018. 3. 18. save history
        }
    }
}
