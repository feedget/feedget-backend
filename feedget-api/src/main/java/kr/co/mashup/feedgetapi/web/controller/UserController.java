package kr.co.mashup.feedgetapi.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.mashup.feedgetapi.exception.ErrorResponse;
import kr.co.mashup.feedgetapi.service.UserService;
import kr.co.mashup.feedgetapi.web.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 유저 관련 request에 대한 처리
 * <p>
 * Created by ethan.kim on 2017. 12. 22..
 */
@RestController
@RequestMapping(value = "/users")
@Api(description = "유저", tags = {"user"})
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "유저 닉네임 수정", notes = "유저의 닉네임을 수정한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "수정 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PatchMapping(value = "/nickname")
    public ResponseEntity updateUserNickname(@RequestAttribute long userId,
                                             @RequestBody @Valid UserDto.UpdateNickname updateNickname,
                                             BindingResult result) {
        log.info("updateUserNickname - dto : {}", updateNickname);

        if (result.hasErrors()) {
            ErrorResponse errorRepoonse = new ErrorResponse();
            errorRepoonse.setMessage("질못된 요청입니다");
            errorRepoonse.setCode("bad request");
            // Todo: BindingResult안에 들어 있는 에러 정보 사용
            return new ResponseEntity<>(errorRepoonse, HttpStatus.BAD_REQUEST);
        }

        userService.modifyUserNickname(userId, updateNickname);
        return new ResponseEntity(HttpStatus.OK);
    }

    // Todo: token 갱신 API 추가
}
