package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.FeedbackDto;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.Feedback;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import kr.co.mashup.feedgetcommon.repository.FeedbackRepository;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by ethan.kim on 2018. 1. 17..
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    private final UserRepository userRepository;

    private final CreationRepository creationRepository;

    /**
     * 피드백 리스트 조회
     *
     * @param userId     유저 ID
     * @param creationId 창작물 ID
     * @param pageable   페이지 정보
     * @return
     */
    @Transactional(readOnly = true)
    public List<FeedbackDto.Response> readFeedbacks(long userId, long creationId, Pageable pageable, Long cursor) {
        Optional<User> userOp = userRepository.findByUserId(userId);
        User user = userOp.orElseThrow(() -> new NotFoundException("not found user"));

        Optional<Creation> creationOp = creationRepository.findByCreationId(creationId);
        Creation creation = creationOp.orElseThrow(() -> new NotFoundException("not found creation"));

        // 창작물에 대한 `피드백을 작성해야` 다른 유저의 피드백 리스트를 볼 수 있다
        Optional<Feedback> feedbackOp = feedbackRepository.findByCreationIdAndWriterId(creationId, userId);
        Feedback userFeedback = feedbackOp.orElseThrow(() -> new NotFoundException("not found feedback"));

        Page<Feedback> feedbackPage = feedbackRepository.findByCreationIdAndSelectionIsFalse(creationId, pageable);
        List<FeedbackDto.Response> content = feedbackPage.getContent().stream()
                .filter(feedback -> !feedback.isWritedBy(user))
                .map(FeedbackDto.Response::newResponse)
                .collect(Collectors.toList());

        // 자신의 피드백이 최상단에 위치한다
        // 채택된 피드백이 있을 경우
        //    채택된 피드백이 최상단에 위치하고, 그다음에 자신의 피드백이 위치
        //    자신의 피드백이 채택된 경우, 자신의 피드백만 최상단에 위치
        if (pageable.getPageNumber() == 0) {
            Optional<Feedback> selectionFeedbackOp = feedbackRepository.findByCreationIdAndSelectionIsTrue(creationId);

            if (selectionFeedbackOp.isPresent()) {
                Feedback selectionFeedback = selectionFeedbackOp.get();

                if (!selectionFeedback.isWritedBy(user)) {
                    content.add(0, FeedbackDto.Response.newResponse(userFeedback));
                }

                content.add(0, FeedbackDto.Response.newResponse(selectionFeedback));
            } else {
                content.add(0, FeedbackDto.Response.newResponse(userFeedback));
            }
        }

        return content;
    }
}