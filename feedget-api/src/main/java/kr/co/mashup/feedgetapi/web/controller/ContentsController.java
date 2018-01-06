package kr.co.mashup.feedgetapi.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.mashup.feedgetapi.exception.ErrorResponse;
import kr.co.mashup.feedgetapi.service.ContentsService;
import kr.co.mashup.feedgetapi.web.dto.ContentsDto;
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
 * 창작물의 컨텐츠 관련 request에 대한 처리
 * <p>
 * Created by ethan.kim on 2017. 12. 21..
 */
@RestController
@RequestMapping(value = "/creations/{creationId}/contents")
@Api(description = "창작물 컨텐츠", tags = {"creation content"})
@Slf4j
@RequiredArgsConstructor
public class ContentsController {

    private final ContentsService contentsService;

    @ApiOperation(value = "창작물의 컨텐츠 추가")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "추가 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping
    public ResponseEntity createContents(@PathVariable(value = "creationId") long creationId,
                                         @Valid ContentsDto dto,
                                         BindingResult result) {
        log.info("createContents - creationId : {}, content : {}", creationId, dto);

        if (result.hasErrors()) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("잘못된 요청입니다");
            errorResponse.setCode("bad request");
            // Todo: BindingResult안에 들어 있는 에러 정보 사용
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        contentsService.addContents(creationId, dto);
        return new ResponseEntity<>(Response.created(), HttpStatus.CREATED);
    }

    @ApiOperation(value = "창작물의 컨텐츠 삭제")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "삭제 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @DeleteMapping
    public ResponseEntity deleteContents(@PathVariable(value = "creationId") long creationId,
                                         @RequestParam(value = "contentId") List<Long> contentIds) {
        log.info("deleteContents - creationId : {}, contentIds : {}", creationId, contentIds);

        contentsService.removeContents(creationId, contentIds);
        return ResponseEntity.ok().build();
    }
}
