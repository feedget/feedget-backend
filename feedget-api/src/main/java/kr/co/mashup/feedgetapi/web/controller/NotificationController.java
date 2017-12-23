package kr.co.mashup.feedgetapi.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.mashup.feedgetapi.web.dto.DataListResponse;
import kr.co.mashup.feedgetapi.web.dto.NotificationDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * 알림 관련 request에 대한 처리
 * <p>
 * Created by ethan.kim on 2017. 12. 23..
 */
@RestController
@RequestMapping(value = "/notifications")
@Api(description = "알림", tags = {"notification"})
public class NotificationController {


    @ApiOperation(value = "알림 리스트 조회", notes = "알림 리스트를 조회한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "조회 성공", response = NotificationDto.Response.class),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping
    public DataListResponse<NotificationDto.Response> getNotifications(@RequestHeader long userId) {

        // Todo: implement read notifications logic

        return new DataListResponse<>(Collections.emptyList());
    }
}
