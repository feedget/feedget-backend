package kr.co.mashup.feedgetapi.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.mashup.feedgetapi.exception.ErrorResponse;
import kr.co.mashup.feedgetapi.service.DeviceService;
import kr.co.mashup.feedgetapi.web.dto.DeviceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @ApiOperation(value = "cloud message token 등록/갱신", notes = "cloud message token을 등록/갱신한다")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "등록 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PatchMapping(value = "/cloud-messaging")
    public ResponseEntity registerCloudMessagingDevice(@RequestAttribute long userId,
                                                       @Valid @RequestBody DeviceDto.UpdateCloudMsgRegId dto,
                                                       BindingResult result) {
        log.info("registerCloudMessagingDevice - userId : {}, dto : {}", userId, dto);

        if (result.hasErrors()) {
            ErrorResponse errorRepoonse = new ErrorResponse();
            errorRepoonse.setMessage("질못된 요청입니다");
            errorRepoonse.setCode("bad request");
            // Todo: BindingResult안에 들어 있는 에러 정보 사용
            return new ResponseEntity<>(errorRepoonse, HttpStatus.BAD_REQUEST);
        }

        deviceService.registerCloudMessagingDevice(userId, dto);
        return ResponseEntity.ok().build();
    }
}
