package kr.co.mashup.feedgetapi.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.mashup.feedgetapi.web.ParameterUtil;
import kr.co.mashup.feedgetapi.web.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 컨텐츠 관련 request에 대한 처리
 * <p>
 * Created by ethan.kim on 2017. 12. 21..
 */
@RestController
@RequestMapping(value = "/contents")
@Api(description = "컨텐츠", tags = {"contents"})
@Slf4j
public class ContentsController {

    @ApiOperation(value = "컨텐츠 추가")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "추가 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청(필수 파라미터 누락)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Response createContents(@RequestParam("creationId") long creationId,
                                   @RequestParam("type") String contentsType,
                                   @RequestParam(name = "files") List<MultipartFile> files) {
        log.info("createContents - creationId : {}, type : {}, files : {}", creationId, contentsType, files);

        // 컨텐츠 - 이미지
        // 컨텐츠 이미지는 0 ~ 10개까지 게시할 수 있다

        ParameterUtil.checkParameterEmpty(creationId, contentsType, files);
//        contentsService.addContents(creationId, Contents.Type.fromString(contentsType), files);
        return Response.created();
    }
}
