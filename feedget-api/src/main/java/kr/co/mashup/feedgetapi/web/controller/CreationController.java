package kr.co.mashup.feedgetapi.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.mashup.feedgetapi.exception.ErrorResponse;
import kr.co.mashup.feedgetapi.service.CreationService;
import kr.co.mashup.feedgetapi.web.dto.CreationDto;
import kr.co.mashup.feedgetapi.web.dto.DataListResponse;
import kr.co.mashup.feedgetapi.web.dto.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 창작물 관련 request에 대한 처리
 * <p>
 * Created by ethan.kim on 2017. 12. 21..
 */
@RestController
@RequestMapping(value = "/creations")
@Api(description = "창작물", tags = {"creation"})
public class CreationController {

    @Autowired
    private CreationService creationService;

    @ApiOperation(value = "창작물 리스트 조회", notes = "창작물 리스트를 조회한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping
    public DataListResponse<CreationDto.Response> getCreations(@RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "size", defaultValue = "20") int size) {

        List<CreationDto.Response> creations = new ArrayList<>();
        creations.add(new CreationDto.Response());

        return new DataListResponse<>(creations);
    }

    @ApiOperation(value = "창작물 단건 조회", notes = "단건의 창작물을 조회한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping(value = "/{creationId}")
    public DataResponse<CreationDto.DetailResponse> getCreation(@RequestHeader long userId,
                                                                @PathVariable(value = "creationId") long creationId) {

        return new DataResponse<>(new CreationDto.DetailResponse());
    }

    @ApiOperation(value = "창작물 추가", notes = "창작물을 추가한다")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "추가 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping
    public ResponseEntity createCreation(@RequestHeader long userId,  // Todo: 유저 ID 셋팅
                                         @Valid @RequestBody CreationDto.Create create,
                                         BindingResult result) {

        if (result.hasErrors()) {
            ErrorResponse errorRepoonse = new ErrorResponse();
            errorRepoonse.setMessage("질못된 요청입니다");
            errorRepoonse.setCode("bad request");
            // Todo: BindingResult안에 들어 있는 에러 정보 사용
            return new ResponseEntity<>(errorRepoonse, HttpStatus.BAD_REQUEST);
        }

//        ParameterUtil.checkParameterEmpty();
        long creationId = creationService.addCreation(userId, create);
        return new ResponseEntity<>(new DataResponse<>(creationId), HttpStatus.CREATED);
    }

    @ApiOperation(value = "창작물 수정", notes = "창작물을 수정한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "수정 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PutMapping(value = "/{creationId}")
    public void updateCreation(@RequestHeader long userId,
                               @Valid @RequestBody CreationDto.Update update) {
        // Todo: implement modify logic
    }

    @ApiOperation(value = "창작물 삭제", notes = "창작물를 삭제한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "삭제 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @DeleteMapping(value = "/{creationId}")
    public void deleteCreation() {
        // Todo: implement remove logic
    }

    // Todo: 서비스 단의 exception handler 추
}
