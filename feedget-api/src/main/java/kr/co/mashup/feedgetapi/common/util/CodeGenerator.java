package kr.co.mashup.feedgetapi.common.util;

import java.util.UUID;

/**
 * 고유값 생성 유틸
 * <p>
 * Created by ethankim on 2017. 11. 5..
 */
public class CodeGenerator {

    /**
     * 파일 고유이름 생성
     * uuid + unix timestamp
     * uuid - 랜덤한 32개의 '숫자' & '영어소문자' 와 구분자 '-' 로 된 36자리의 문자열 출력
     * unix timestamp - 10자리
     * Todo: 분산 시스템에서도 유니크한 값 유지 할 수 있는 방법 추가
     *
     * @param fileName
     * @return
     */
    public static String generateFileName(String fileName) {
        Long createdAtUnixTimestamp = System.currentTimeMillis() / 1000;
        String extension = fileName.substring(fileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + "_" + createdAtUnixTimestamp + extension;
    }
}
