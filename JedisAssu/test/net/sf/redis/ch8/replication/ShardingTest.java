package net.sf.redis.ch8.replication;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.ShardedJedisHelper;
import redis.clients.jedis.ShardedJedis;

/**
 * ShardTestKeyMaker �׽�Ʈ Ŭ����
 * 
 * ���� ������ �����͸� �����ϱ� ���� �׽�Ʈ ���̽�
 * 
 * 500���� �����͸� ������ Ŭ�����Ϳ� �����ϰ�,
 * ����� ���� �о ��.
 * ������ ���� ��ȸ�� ���� �ٸ��� ���� �߻���Ŵ
 * 
 * @author assu
 * @date 2016.05.019
 */
public class ShardingTest {
	private static final int TEST_COUNT = 500;
	static ShardedJedisHelper helper;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = ShardedJedisHelper.getInstance();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool();
	}
	
	@Test
	public void testSharding() {
		ShardedJedis jedis = helper.getConnection();
		
		for (int i=0; i<TEST_COUNT; i++) {
			String testValue = "Test Value " + i;
			ShardTestKeyMaker keyMaker = new ShardTestKeyMaker(i);
			jedis.set(keyMaker.getKey(), testValue);
			assertEquals(testValue, jedis.get(keyMaker.getKey()));
		}
	}
}
