package net.sf.redis.ch8.replication;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.ch7.redislogger.KeyMaker;
import redis.clients.jedis.Jedis;

/**
 * ReplicationKeyMaker, DataWriter, DataReader �׽�Ʈ Ŭ����
 * 
 * ���� ���� ���� �׽�Ʈ
 * 
 * 10������ Ű�� ������ ��忡 ����ϰ� �����̺� ��忡�� �����͸� �о �� ���� ��.
 * �� �����Ͱ� �����ϴٸ� �׽�Ʈ�� ����. (=����ȭ�� �ǽð����� �̷����)
 * 
 * @author assu
 * @date 2016.05.08
 */
public class ReplicationTest {
	private static final int TEST_COUNT = 100000;
	static Jedis master;
	static Jedis slave;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		master = new Jedis("192.168.56.102", 6300);
		slave = new Jedis("192.168.56.102", 6301);
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		master.disconnect();
		slave.disconnect();
	}
	
	@Test
	public void replicationTest() {
		DataWriter writer = new DataWriter(master);
		DataReader reader = new DataReader(slave);
		
		for (int i=0; i<TEST_COUNT; i++) {
			KeyMaker keyMaker = new ReplicationKeyMaker(i);
			String value = "test value : " + i;
			writer.set(keyMaker.getKey(), value);
			
			for (int j=0; j<3; j++) {
				String result = reader.get(keyMaker.getKey());
				
				if (value.equals(result)) {
					System.out.println("success case!");
				} else {
					// ���� 160��°���� ����
					// ������ ������ ���� �����̺� ��� ���� ������ ������ �Ϸ�Ǳ� ���� �����̺� ��忡�� �����͸� ��ȸ�߱� ����
					fail("The value NOT match with a result. [" + value + "][" + result + "]");
				}
			}
		}
	}
}
