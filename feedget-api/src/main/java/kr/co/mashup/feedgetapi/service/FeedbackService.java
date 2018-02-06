package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.InvalidParameterException;
import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.FeedbackDto;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.Feedback;
import kr.co.mashup.feedgetcommon.domain.PointHistory;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import kr.co.mashup.feedgetcommon.repository.FeedbackRepository;
import kr.co.mashup.feedgetcommon.repository.PointHistoryRepository;
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

    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 피드백 리스트 조회
     *
     * @param userId     유저 ID
     * @param creationId 창작물 ID
     * @param pageable   페이지 정보
     * @return
     */
    @Transactional(readOnly = true)
    public List<FeedbackDto.Response> readFeedbackList(long userId, long creationId, Pageable pageable, Long cursor) {
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

    /**
     * 창작물에 피드백 추가
     *
     * @param userId     작성자 ID
     * @param creationId 창작물 ID
     * @param dto        추가할 피드백 데이터
     */
    @Transactional
    public void addFeedback(long userId, long creationId, FeedbackDto.Create dto) {
        Optional<User> writerOp = userRepository.findByUserId(userId);
        User writer = writerOp.orElseThrow(() -> new NotFoundException("not found writer"));

        Optional<Creation> creationOp = creationRepository.findByCreationId(creationId);
        Creation creation = creationOp.orElseThrow(() -> new NotFoundException("not found creation"));

        // 자신이 게시한 창작물일 경우 피드백을 작성할 수 없다
        if (creation.isWritedBy(writer)) {
            // Todo: exception 수정
            throw new InvalidParameterException("forbidden write feedback");
        }

        // 창작물당 피드백은 1개만 작성할 수 있다
        Optional<Feedback> feedbackOp = feedbackRepository.findByCreationIdAndWriterId(creationId, userId);
        if (feedbackOp.isPresent()) {
            // Todo: exception 수정
            throw new InvalidParameterException("exceed write feedback");
        }

        Feedback feedback = new Feedback();
        feedback.setContent(dto.getContent());
        feedback.setAnonymity(dto.isAnonymity());
        feedback.setSelection(false);
        feedback.setWriter(writer);
        feedback.setCreation(creation);
        feedbackRepository.save(feedback);

        // Todo: feedback 작성시 기본 포인트 지급
        // Todo: 피드백이 작성되면 창작물 게시자는 push로 알림을 받는다
    }

    /**
     * 창작물의 피드백 제거
     *
     * @param userId     작성자 ID
     * @param creationId 창작물 ID
     * @param feedbackId 피드백 ID
     */
    @Transactional
    public void removeFeedback(long userId, long creationId, long feedbackId) {
        Optional<Feedback> feedbackOp = feedbackRepository.findByCreationIdAndWriterId(creationId, userId);
        Feedback feedback = feedbackOp.orElseThrow(() -> new NotFoundException("not found feedback"));

        Optional<Creation> creationOp = creationRepository.findByCreationId(creationId);
        Creation creation = creationOp.orElseThrow(() -> new NotFoundException("not found creation"));

        // 창작물 마감 후 삭제 불가
        if (creation.isDeadline()) {
            // Todo: exception 수정
            throw new InvalidParameterException("forbidden remove feedback");
        }

        feedbackRepository.delete(feedbackId);
    }


    /*
     * 창작물을 게시한 유저는 채택된 피드백에 대해 감사인사를 작성할 수 있다
     * 채택된 피드백을 작성한 유저는 push로 보상 포인트 지급 알림을 받는다
     */
    @Transactional
    public void selectFeedback(long userId, long creationId, long feedbackId) {
        Optional<Feedback> feedbackOp = feedbackRepository.findByFeedbackId(feedbackId);
        Feedback feedback = feedbackOp.orElseThrow(() -> new NotFoundException("not found feedback"));

        if(!feedback.fromCreation(creationId)) {
            // Todo: exception 수정
            throw new InvalidParameterException("forbidden request");
        }

        Optional<User> creationWriterOp = userRepository.findByUserId(userId);
        User creationWriter = creationWriterOp.orElseThrow(() -> new NotFoundException("not found writer"));

        // 채택하는 사람이 창작물 게시자인지 확인
        Creation creation = feedback.getCreation();
        if (!creation.isWritedBy(creationWriter)) {
            // Todo: exception 수정
            throw new InvalidParameterException("forbidden request");
        }

        feedback.setSelection(true);
        feedbackRepository.save(feedback);

        // 피드백 작성자에게 보상 포인트 지급
        User feedbackWriter = feedback.getWriter();
        feedbackWriter.changePoint(creation.getRewardPoint());
        userRepository.save(feedbackWriter);

        // save history
        PointHistory pointHistory = new PointHistory();
        pointHistory.setGiverId(creationWriter.getUserId());
        pointHistory.setReceiverId(feedbackWriter.getUserId());
        pointHistory.setPoint(creation.getRewardPoint());
        pointHistory.setType(PointHistory.Type.FEEDBACK_REWARD);
        pointHistoryRepository.save(pointHistory);

        // Todo: 피드백 작성자에게 푸시로 보상지급 알림

        // Todo: 감사인사 저장
    }
}
