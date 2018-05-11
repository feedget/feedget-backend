package kr.co.mashup.feedgetapi.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.mashup.feedgetapi.exception.ErrorResponse;
import kr.co.mashup.feedgetapi.service.CreationQueryService;
import kr.co.mashup.feedgetapi.service.CreationCommandService;
import kr.co.mashup.feedgetapi.web.CreationUpdateValidator;
import kr.co.mashup.feedgetapi.web.dto.CreationDto;
import kr.co.mashup.feedgetapi.web.dto.DataListResponse;
import kr.co.mashup.feedgetapi.web.dto.DataResponse;
import kr.co.mashup.feedgetapi.web.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 창작물 관련 request에 대한 처리
 * <p>
 * Created by ethan.kim on 2017. 12. 21..
 */
@RestController
@RequestMapping(value = "/creations")
@Api(description = "창작물", tags = {"creation"})
@Slf4j
public class CreationController {

    private final CreationCommandService creationCommandService;

    private final CreationQueryService creationQueryService;

    private final CreationUpdateValidator creationUpdateValidator;

    @Autowired
    public CreationController(CreationCommandService creationCommandService, CreationQueryService creationQueryService, CreationUpdateValidator creationUpdateValidator) {
        this.creationCommandService = creationCommandService;
        this.creationQueryService = creationQueryService;
        this.creationUpdateValidator = creationUpdateValidator;
    }

    // Todo: 정렬 추가 - 최신순, 마감빠른순, 포인트 많은 순, 피드백 많은 순, 피드백 적은
    @ApiOperation(value = "창작물 리스트 조회", notes = "창작물 리스트를 조회한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<DataListResponse> readCreations(@RequestAttribute long userId,
                                                          @RequestParam(value = "category", defaultValue = "ALL") String categoryName,
                                                          @RequestParam(value = "cursor", required = false) Long cursor,
                                                          @PageableDefault(page = 0, size = 20) Pageable pageable) {
        log.info("readCreations - userId : {}, category : {}, cursor : {}, pageable : {}", userId, categoryName, cursor, pageable);

        Page<CreationDto.Response> creationPage = creationQueryService.readCreations(userId, categoryName, pageable);

        // Todo: header에서 기타 정보를 내릴지 고려
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("pageSize", String.valueOf(creationPage.getSize()));
//        headers.add("pageNo", String.valueOf(creationPage.getNumber()));
//        headers.add("total", String.valueOf(creationPage.getTotalElements()));  // 검색된 전체 data 수
//        headers.add("pageTotal", String.valueOf(creationPage.getTotalPages()));  // 전체 페이지 수
//        return new ResponseEntity<>(new DataListResponse<>(creationPage), headers, HttpStatus.OK);
        return new ResponseEntity<>(new DataListResponse<>(creationPage), HttpStatus.OK);
    }

    @ApiOperation(value = "창작물 단건 조회", notes = "단건의 창작물을 조회한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping(value = "/{creationId}")
    public DataResponse<CreationDto.DetailResponse> readCreation(@RequestAttribute long userId,
                                                                 @PathVariable(value = "creationId") long creationId) {
        log.info("readCreation - userId : {}, creationId : {}", userId, creationId);

        CreationDto.DetailResponse detailResponse = creationQueryService.readCreation(userId, creationId);
        return new DataResponse<>(detailResponse);
    }

    @ApiOperation(value = "창작물 추가", notes = "창작물을 추가한다")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "추가 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping
    public ResponseEntity createCreation(@RequestAttribute long userId,
                                         @Valid @RequestBody CreationDto.Create create,
                                         BindingResult result) {
        log.info("createCreation - userId : {}, dto : {}", userId, create);

        if (result.hasErrors()) {
            ErrorResponse errorRepoonse = new ErrorResponse();
            errorRepoonse.setMessage("질못된 요청입니다");
            errorRepoonse.setCode("bad request");
            // Todo: BindingResult안에 들어 있는 에러 정보 사용
            return new ResponseEntity<>(errorRepoonse, HttpStatus.BAD_REQUEST);
        }

        long creationId = creationCommandService.addCreation(userId, create);
        return new ResponseEntity<>(new DataResponse<>(creationId), HttpStatus.CREATED);
    }

    @ApiOperation(value = "창작물 수정", notes = "창작물을 수정한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "수정 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PutMapping(value = "/{creationId}")
    public ResponseEntity updateCreation(@RequestAttribute long userId,
                                         @PathVariable(value = "creationId") long creationId,
                                         @RequestBody CreationDto.Update update,
                                         BindingResult result) {
        log.info("updateCreation - userId : {}, creationId : {}, dto : {}", userId, creationId, update);

        creationUpdateValidator.validate(update, result);
        if (result.hasErrors()) {
            ErrorResponse errorRepoonse = new ErrorResponse();
            errorRepoonse.setMessage("질못된 요청입니다");
            errorRepoonse.setCode("bad request");
            // Todo: BindingResult안에 들어 있는 에러 정보 사용
            return new ResponseEntity<>(errorRepoonse, HttpStatus.BAD_REQUEST);
        }

        creationCommandService.modifyCreation(userId, creationId, update);
        return new ResponseEntity<>(Response.ok(), HttpStatus.OK);
    }

    @ApiOperation(value = "창작물 삭제", notes = "창작물를 삭제한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "삭제 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @DeleteMapping(value = "/{creationId}")
    public ResponseEntity deleteCreation(@RequestAttribute long userId,
                                         @PathVariable(value = "creationId") long creationId) {
        log.info("deleteCreation - userId : {}, creationId : {}", userId, creationId);

        creationCommandService.removeCreation(userId, creationId);
        return ResponseEntity.ok().build();
    }

    // Todo: 서비스 단의 exception handler 추가
}
