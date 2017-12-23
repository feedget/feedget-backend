package kr.co.mashup.feedgetapi.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;

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

    @ApiOperation(value = "유저 로그인", notes = "유저를 로그인한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "로그인 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping(value = "/register")
    public void registerUser() {
        // Todo: login에 필요한 정보들...

        // Todo: return access token
    }

    // 닉네임 수정
    @ApiOperation(value = "유저 닉네임 수정", notes = "유저의 닉네임을 수정하다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "수정 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PatchMapping(value = "/nickname")
    public void updateUserNickname(@RequestHeader long userId,
                                   @RequestBody @Valid @NotBlank @Size(min = 1) String nickname) {  // Todo: userDto로 분리

    }

    // Todo: token 갱신 API 추가
}
