package net.sf.redis.ch7.redislogger;

import java.util.Random;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * LogWriterV2, LogReceiverV2 �׽�Ʈ Ŭ����
 * 
 * @author assu
 * @date 2016.04.02
 */
public class LoggerTest {
	static JedisHelper helper;
	
	private static final int WAITING_TREM = 5000;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();		// LogWriter ������ ���� ���� ���� ��ü ������.
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool();	// ��� �׽�Ʈ���̽��� ������ �Ϸ�Ǹ� ���� ���ҽ� ����. 
	}
	
	@Test
	public void testWrite() {
		Random random = new Random(System.currentTimeMillis());
		LogWriterV2 logWriter = new LogWriterV2(helper);
		for (int i=0; i<100; i++) {
			assertTrue(logWriter.log(i + ", This is new test log message.") > 0);
			
			try {
				Thread.sleep(random.nextInt(50));
			} catch (Exception e) {
				// do nothing.
			}
		}
	}
	
	@Test
	public void testReceiver() {
		LogReceiverV2 logReceiver = new LogReceiverV2();
		//for(int i=0;i<5; i++) {
			logReceiver.start();
			try {
				Thread.sleep(WAITING_TREM);
			} catch (Exception e) {
				// do nothing.
			}
		//}
	}
}
