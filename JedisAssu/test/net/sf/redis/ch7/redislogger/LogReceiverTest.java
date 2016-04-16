package net.sf.redis.ch7.redislogger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * LogReceiver 테스트 클래스
 * @author assu
 * @date 2016.04.02
 */
public class LogReceiverTest {
	static JedisHelper helper;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool();
	} 
	
	@Test
	public void testLogger() {
		LogReceiver receiver = new LogReceiver();
		receiver.start();
	}
}
