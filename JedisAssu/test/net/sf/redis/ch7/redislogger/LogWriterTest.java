package net.sf.redis.ch7.redislogger;

import java.util.Random;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * LogWriter �׽�Ʈ Ŭ����
 * @author assu
 * @date 2016.04.02
 */
public class LogWriterTest {
	static JedisHelper helper;
	static LogWriter logger;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();		// LogWriter ������ ���� ���� ���� ��ü ������.
		logger = new LogWriter(helper);			// ���� ���� ��ü�� ���ڷ� �Ͽ� LogWriter ��ü ����
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool(); 	// ��� �׽�Ʈ���̽��� ������ �Ϸ�Ǹ� ���� ���ҽ� ����. 
	}
	
	@Test
	public void testLogger() {
		Random random = new Random(System.currentTimeMillis());
		for (int i=0; i<100; i++) {	// ���� ������ 100���� �α� ����
			
			// �α׸� ���� ������ �����ϰ� ����� ���ڿ��� ���̰� 0���� ū�� Ȯ����.
			// �׽�Ʈ ���̽������� ����� ����̸� ����ó���� ������ ����.
			assertTrue(logger.log(i + ",This is test log message") > 0);
			
			try {
				Thread.sleep(random.nextInt(50));
			} catch (Exception e) {
				// do nothing.
			}
		}
	}
}
