package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.InvalidParameterException;
import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.CreationDto;
import kr.co.mashup.feedgetcommon.domain.Category;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.CategoryRepository;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by ethan.kim on 2017. 12. 23..
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CreationService {

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final CreationRepository creationRepository;

    @Transactional
    public long addCreation(long userId, CreationDto.Create dto) {
        User writer = userRepository.findOne(userId);
        if (writer == null) {
            throw new NotFoundException("not found writer");
        }

        // Todo: implement grade change logic
        boolean result = writer.changePoint(-dto.getRewardPoint());
        if (!result) {
            throw new InvalidParameterException("exceed current point");
        }
        userRepository.save(writer);

        Optional<Category> categoryOp = categoryRepository.findByName(dto.getCategory());
        Category category = categoryOp.orElseThrow(() -> new NotFoundException("not found category"));

        Creation creation = new Creation();
        creation.setTitle(dto.getTitle());
        creation.setDescription(dto.getDescription());
        creation.setCategory(category);
        creation.setDueDate(LocalDateTime.now().plusDays(14));
        creation.setRewardPoint(dto.getRewardPoint());
        creation.setStatus(Creation.Status.PROCEEDING);
        creation.setWriter(writer);
        creation.setAnonymity(dto.isAnonymity());
        creation.setFeedbackCount(0L);
        creation = creationRepository.save(creation);

        return creation.getCreationId();
    }

    @Transactional
    public void modifyCreation(long userId, long creationId, CreationDto.Update dto) {
        User writer = userRepository.findOne(userId);
        if (writer == null) {
            throw new NotFoundException("not found writer");
        }

        Optional<Creation> creationOp = creationRepository.findByCreationId(creationId);
        Creation creation = creationOp.orElseThrow(() -> new NotFoundException("not found creation"));

        if (!creation.isWritedBy(writer)) {
            // Todo: exception class 수정
            throw new InvalidParameterException("not match writer");
        }

        if (creation.isDeadline()) {
            // Todo: exception class 수정
            throw new InvalidParameterException("creation is deadline");
        }

        if (creation.hasFeedback()) {
            // Todo: exception class 수정
            throw new InvalidParameterException("forbidden modify");
        }

        // 보상 포인트 수정시 차액은 반환된다
        // Todo: implement grade change logic
        boolean result = writer.changePoint(creation.getRewardPoint() - dto.getRewardPoint());
        if (!result) {
            throw new InvalidParameterException("exceed current point");
        }
        userRepository.save(writer);

        Optional<Category> categoryOp = categoryRepository.findByName(dto.getCategory());
        Category category = categoryOp.orElseThrow(() -> new NotFoundException("not found category"));

        creation.setTitle(dto.getTitle());
        creation.setDescription(dto.getDescription());
        creation.setCategory(category);
        creation.setRewardPoint(dto.getRewardPoint());
        creation.setAnonymity(dto.isAnonymity());
        creationRepository.save(creation);
    }
}
