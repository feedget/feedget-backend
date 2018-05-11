package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.CreationDto;
import kr.co.mashup.feedgetcommon.domain.Category;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.Feedback;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.CategoryRepository;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import kr.co.mashup.feedgetcommon.repository.FeedbackRepository;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 창작물 정보 조회 담당
 * <p>
 * Created by ethan.kim on 2018. 5. 12..
 */
@Service
public class CreationQueryService {

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final CreationRepository creationRepository;

    private final FeedbackRepository feedbackRepository;

    @Autowired
    public CreationQueryService(UserRepository userRepository, CategoryRepository categoryRepository, CreationRepository creationRepository, FeedbackRepository feedbackRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.creationRepository = creationRepository;
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * 창작물 리스트 조회
     *
     * @param userId       유저 ID
     * @param categoryName 카테고리 이름
     * @param pageable     페이지 정보
     * @return
     */
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
                .map(creation -> CreationDto.Response.newResponse(creation))
                .collect(Collectors.toList());

        Pageable resultPageable = new PageRequest(creationPage.getNumber(), creationPage.getSize());
        return new PageImpl<>(content, resultPageable, creationPage.getTotalElements());
    }

    /**
     * 창작물 단건 조회
     *
     * @param userId     유저 ID
     * @param creationId 창작물 ID
     * @return
     */
    @Transactional(readOnly = true)
    public CreationDto.DetailResponse readCreation(long userId, long creationId) {
        Optional<User> userOp = userRepository.findByUserId(userId);
        User user = userOp.orElseThrow(() -> new NotFoundException("not found user"));

        Optional<Creation> creationOp = creationRepository.findByCreationId(creationId);
        Creation creation = creationOp.orElseThrow(() -> new NotFoundException("not found creation"));

        Optional<Feedback> feedbackOp = feedbackRepository.findByCreationIdAndWriterId(creationId, userId);

        return CreationDto.DetailResponse.newDetailResponse(creation, feedbackOp);
    }
}
