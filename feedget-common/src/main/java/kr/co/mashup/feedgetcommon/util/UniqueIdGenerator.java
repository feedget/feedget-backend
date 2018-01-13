package kr.co.mashup.feedgetcommon.util;

import com.eaio.uuid.UUIDGen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

/**
 * Created by ethan.kim on 2018. 1. 14..
 */
@Slf4j
public final class UniqueIdGenerator {

    private static final String FORMAT_STRING_ID = "%016d%s%04d";
    private static final String MAC_ADDRESS;

    private static AtomicInteger seq = new AtomicInteger();
    private static IntUnaryOperator increaseOperator = operand -> operand == 9999 ? 0 : operand + 1;

    private UniqueIdGenerator() {
    }

    static {
        String mac = UUIDGen.getMACAddress();
        log.info("MAC : {}", mac);
        mac = mac.replace(":", "").toLowerCase().substring(0, 12);
        log.info("MAC : {}", mac);
        MAC_ADDRESS = mac;
    }

    /**
     * 현재 시스템 시간을 이용하여 ID(32자 고유값) 생성
     *
     * @return 32자 uniqueId - timestamp(16) + macaddress(12) + sequence(4)
     */
    public static String getStringId() {
        return String.format(FORMAT_STRING_ID, System.currentTimeMillis(), MAC_ADDRESS, seq.updateAndGet(increaseOperator));
    }

    /**
     * 지정한 시간을 이용하여 ID(32자 고유값) 생성
     * 주의1) 과거시간을 입력할 경우, 이미 생성되어 사용하고 있는 ID와 중복이 발생할 수 있다
     * 주의2) 미래시간을 입력할 경우, 나중에 동일 ID가 다시 생성될 수 있다
     *
     * @param timestamp timestamp
     * @return 32자 uniqueId - timestamp(16) + macaddress(12) + sequence(4)
     */
    public static Optional<String> getStringId(long timestamp) {
        try {
            String stringId = String.format(FORMAT_STRING_ID, timestamp, MAC_ADDRESS, SecureRandom.getInstance("SHA1PRNG").nextInt(9999));
            return Optional.of(stringId);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA1PRNG not found", e);
            return Optional.empty();
        }
    }

    /**
     * ID의 생성시간을 반환한다
     *
     * @param uniqueId 32자 uniqueId
     * @return timestamp
     */
    public static long toTime(String uniqueId) {
        if (uniqueId == null) {
            return 0L;
        }
        return NumberUtils.toLong(uniqueId.substring(3, 16), 0L);
    }
}
