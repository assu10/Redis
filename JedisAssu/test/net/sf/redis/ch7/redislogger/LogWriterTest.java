package net.sf.redis.ch7.redislogger;

import java.util.Random;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * LogWriter 테스트 클래스
 * @author assu
 * @date 2016.04.02
 */
public class LogWriterTest {
	static JedisHelper helper;
	static LogWriter logger;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();		// LogWriter 실행을 위한 제디스 헬퍼 객체 가져옴.
		logger = new LogWriter(helper);			// 제디스 헬퍼 객체를 인자로 하여 LogWriter 객체 생성
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool(); 	// 모든 테스트케이스의 실행이 완료되면 헬퍼 리소스 해제. 
	}
	
	@Test
	public void testLogger() {
		Random random = new Random(System.currentTimeMillis());
		for (int i=0; i<100; i++) {	// 레디스 서버에 100개의 로그 저장
			
			// 로그를 레디스 서버에 저장하고 저장된 문자열의 길이가 0보다 큰지 확인함.
			// 테스트 케이스에서는 결과가 양수이면 정상처리된 것으로 간주.
			assertTrue(logger.log(i + ",This is test log message") > 0);
			
			try {
				Thread.sleep(random.nextInt(50));
			} catch (Exception e) {
				// do nothing.
			}
		}
	}
}
