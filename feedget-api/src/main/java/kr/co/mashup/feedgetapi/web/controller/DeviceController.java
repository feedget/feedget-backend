package kr.co.mashup.feedgetapi.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 디바이스 관련 request에 대한 처리
 * <p>
 * Created by ethan.kim on 2017. 12. 22..
 */
@RestController
@RequestMapping(value = "/devices")
@Api(description = "디바이스", tags = {"device"})
@Slf4j
public class DeviceController {

    // Todo: users 밑으로 들어가야할듯...?
    @ApiOperation(value = "cloud message token 추가/갱신", notes = "cloud message token을 추가 및 갱신한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "추가 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PatchMapping(value = "/cloud-messaging")
    public void updateCloudMsgRegId(@RequestBody String cloudMsgRegToken) {

        // Todo: implement update logic
    }
}
