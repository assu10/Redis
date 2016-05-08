package net.sf.redis.ch8.replication;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.ch7.redislogger.KeyMaker;
import redis.clients.jedis.Jedis;

/**
 * ReplicationKeyMakerV2, DataWriterV2, DataReaderV2 �׽�Ʈ Ŭ����
 * 
 * ���� ���� ���� �׽�Ʈ
 * 
 * 10������ Ű�� ������ ��忡 ����ϰ� �����̺� ��忡�� �����͸� �о �� ���� ��.
 * �� �����Ͱ� �����ϴٸ� �׽�Ʈ�� ����. (=����ȭ�� �ǽð����� �̷����)
 *   + ������ ���� ���� Ȯ�� 
 * 
 * @author assu
 * @date 2016.05.08
 */
public class ReplicationTestV2 {
	private static final int TEST_COUNT = 10000;
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
		DataWriterV2 writer = new DataWriterV2(master);
		DataReaderV2 reader = new DataReaderV2(slave);
		
		long current = 0;
		
		for (int i=0; i<TEST_COUNT; i++) {
			KeyMaker keyMaker = new ReplicationKeyMakerV2(i);
			String value = "test value " + i;
			writer.set(keyMaker.getKey(), value);
			
			current = System.currentTimeMillis();
			List<String> result = reader.get(keyMaker.getKey());
			long elapse = System.currentTimeMillis() - current;
			
			if (value.equals(result.get(1))) {
				// ���������͸� ��ȸ�ϴ� �ð��� 1�и��� �̻� �ҿ�Ǹ� �α� ���, 99% �̻��� �����Ͱ� 1�и��� �ȿ� ������
				if (elapse > 1) {
					System.out.println("elapse : " + elapse);
				}
			} else {
				fail("The value NOT match with a result. [" + value + "][" + result + "]");
			}
					
		}
	}
}
