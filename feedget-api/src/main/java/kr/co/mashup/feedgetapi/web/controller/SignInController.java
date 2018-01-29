package kr.co.mashup.feedgetapi.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.mashup.feedgetapi.exception.ErrorResponse;
import kr.co.mashup.feedgetapi.service.UserService;
import kr.co.mashup.feedgetapi.web.dto.DataResponse;
import kr.co.mashup.feedgetapi.web.dto.SignInDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * sign In 관련 request에 대한 처리
 * <p>
 * Created by ethan.kim on 2018. 1. 28..
 */
@RestController
@Api(description = "Sign In", tags = {"sign in"})
@Slf4j
public class SignInController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "Sign In", notes = "User Sign In")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "user sign in success"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping(value = "/sign-in")
    public ResponseEntity signInUser(@Valid @RequestBody SignInDto.Create create,
                                     BindingResult result) {
        log.info("signInUser - dto : {}", create);

        if (result.hasErrors()) {
            ErrorResponse errorRepoonse = new ErrorResponse();
            errorRepoonse.setMessage("질못된 요청입니다");
            errorRepoonse.setCode("bad request");
            // Todo: BindingResult안에 들어 있는 에러 정보 사용
            return new ResponseEntity<>(errorRepoonse, HttpStatus.BAD_REQUEST);
        }

        SignInDto.Response response = userService.signInUser(create);
        return new ResponseEntity<>(new DataResponse<>(response), HttpStatus.OK);
    }
}
