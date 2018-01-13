package kr.co.mashup.feedgetcommon.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ethan.kim on 2018. 1. 14..
 */
@Slf4j
public class UniqueIdGeneratorTest {

    @Test
    public void getStringId_32자_고유값_생성_성공() throws Exception {
        // given :

        // when : 32자 고유값을 생성하면
        String uniqueId = UniqueIdGenerator.getStringId();

        // then : 32자 고유값이 생성된다
        assertEquals(uniqueId.length(), 32);
    }


    @Test
    public void getStringId_timestamp기반_고유값_생성_성공() throws Exception {
        // given : 특정 시간을 이용하여
        long timestamp = System.currentTimeMillis();

        // when : ID를 생성하면
        Optional<String> uniqueIdOp = UniqueIdGenerator.getStringId(timestamp);

        // then : ID에 해당 문자열이 포함되어 있다
        assertTrue(uniqueIdOp.isPresent());
        assertEquals(uniqueIdOp.get().substring(3, 16), String.valueOf(timestamp));
    }

    @Test
    public void toTime_ID의_생성_시간_추출_성공() throws Exception {
        // given : 특정 시간을 이용하여 생성된 ID를
        long timestamp = System.currentTimeMillis();
        Optional<String> idOp = UniqueIdGenerator.getStringId(timestamp);

        // when : 시간으로 변환하면
        long time = UniqueIdGenerator.toTime(idOp.get());

        // then : 만든 시간과 동일하다
        assertEquals(time, timestamp);
    }

    @Test
    public void toTime_ID의_생성_시간_추출_시간이_null이면_0_반환() throws Exception {
        // given : null인 ID를
        String id = null;

        // when : 시간으로 변환하면
        long time = UniqueIdGenerator.toTime(id);

        // then : 0이 반환된다
        assertEquals(time, 0L);
    }

    @Test
    public void toTime_ID의_생성_시간_추출_이상한_ID면_0_반환() throws Exception {
        // given : 이상한 ID를
        String id = "asodfas1234131123";

        // when : 시간으로 변환하면
        long time = UniqueIdGenerator.toTime(id);

        // then : 0이 반환된다
        assertEquals(time, 0L);
    }

    @Test
    public void test01() throws Exception {
        test(1);
    }

    @Test
    public void test02() throws Exception {
        test(2);
    }

    @Test
    public void test04() throws Exception {
        test(4);
    }

    @Test
    public void test08() throws Exception {
        test(8);
    }

    @Test
    public void test16() throws Exception {
        test(16);
    }

    @Test
    public void test32() throws Exception {
        test(32);
    }

    @Test
    public void test64() throws Exception {
        test(64);
    }

    /**
     * multi thread로 동작시키더라도, 모두 unique한 값이 생성된다
     *
     * @param threadCount
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void test(final int threadCount) throws InterruptedException, ExecutionException {
        Callable<String> task = () -> UniqueIdGenerator.getStringId();

        List<Callable<String>> tasks = Collections.nCopies(threadCount, task);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<String>> futures = executorService.invokeAll(tasks);
        Set<String> results = new HashSet<>(futures.size());

        // check for exceptions
        for (Future<String> future : futures) {
            // throws an exception if an exception was thrown by the task
            log.debug(future.get());
            results.add(future.get());
        }

        assertEquals(futures.size(), threadCount);
        assertEquals(results.size(), threadCount);
    }
}
