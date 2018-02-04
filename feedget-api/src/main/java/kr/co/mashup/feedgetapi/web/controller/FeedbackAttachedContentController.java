package kr.co.mashup.feedgetapi.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.mashup.feedgetapi.exception.ErrorResponse;
import kr.co.mashup.feedgetapi.service.ContentsService;
import kr.co.mashup.feedgetapi.web.dto.FeedbackDto;
import kr.co.mashup.feedgetapi.web.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 피드백의 첨부 컨텐츠 관련 request에 대한 처리
 * <p>
 * Created by ethan.kim on 2018. 2. 1..
 */
@RestController
@RequestMapping(value = "/creations/{creationId}/feedback/{feedbackId}/contents")
@Api(description = "피드백 첨부 컨텐츠", tags = {"feedback attached content"})
@Slf4j
@RequiredArgsConstructor
public class FeedbackAttachedContentController {

    private final ContentsService contentsService;

    @ApiOperation(value = "피드백의 첨부 컨텐츠 추가")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "추가 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping
    public ResponseEntity createFeedbackContents(@PathVariable(value = "creationId") long creationId,
                                                 @PathVariable(value = "feedbackId") long feedbackId,
                                                 @Valid FeedbackDto.AttachedContent dto,
                                                 BindingResult result) {
        log.info("createFeedbackContents - creationId : {}, feedbackId : {}, content : {}", creationId, feedbackId, dto);

        if (result.hasErrors()) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("잘못된 요청입니다");
            errorResponse.setCode("bad request");
            // Todo: BindingResult안에 들어 있는 에러 정보 사용
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        contentsService.addFeedbackAttachedContents(creationId, feedbackId, dto);
        return new ResponseEntity<>(Response.created(), HttpStatus.CREATED);
    }


    @ApiOperation(value = "피드백의 첨부 컨텐츠 삭제")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "삭제 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @DeleteMapping
    public ResponseEntity deleteFeedbackContents(@PathVariable(value = "creationId") long creationId,
                                                 @PathVariable(value = "feedbackId") long feedbackId,
                                                 @RequestParam(value = "contentId") List<Long> contentIds) {
        log.info("deleteFeedbackContents - creationId : {}, feedbackId : {}, contentIds : {}", creationId, feedbackId, contentIds);

        // Todo: implements
        return ResponseEntity.ok().build();
    }
}
