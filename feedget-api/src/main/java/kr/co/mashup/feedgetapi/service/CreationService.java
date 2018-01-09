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
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /**
     * 창작물 추가
     *
     * @param userId 작성자 ID
     * @param dto    추가할 창작물 데이터
     * @return 추가된 창작물 ID
     */
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

    /**
     * 창작물 수정
     *
     * @param userId     작성자 ID
     * @param creationId 창작물 ID
     * @param dto        수정할 창작물 데이터
     */
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

    /**
     * 창작물 삭제
     *
     * @param userId     작성자 ID
     * @param creationId 창작물 ID
     */
    @Transactional
    public void removeCreation(long userId, long creationId) {
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

        // Todo: implement grade change logic
        writer.changePoint(creation.getRewardPoint());
        userRepository.save(writer);

        creationRepository.delete(creationId);
    }

    @Transactional(readOnly = true)
    public Page<CreationDto.Response> readCreations(long userId, String categoryName, Pageable pageable) {
        Optional<User> userOp = userRepository.findByUserId(userId);
        User user = userOp.orElseThrow(() -> new NotFoundException("not found user"));

        Page<Creation> creationPage;
        if (StringUtils.equals(categoryName, "ALL")) {
            creationPage = creationRepository.findAll(pageable);
        } else {
            Optional<Category> categoryOp = categoryRepository.findByName(categoryName);
            Category category = categoryOp.orElseThrow(() -> new NotFoundException("not found category"));

            creationPage = creationRepository.findByCategory(category, pageable);
        }

        List<CreationDto.Response> content = creationPage.getContent().stream()
                .map(creation -> CreationDto.Response.fromCreation(creation, user))
                .collect(Collectors.toList());

        Pageable resultPageable = new PageRequest(creationPage.getNumber(), creationPage.getSize());
        return new PageImpl<>(content, resultPageable, creationPage.getTotalElements());
    }
}
