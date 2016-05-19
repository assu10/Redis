package net.sf.redis.ch8.replication;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.ShardedJedisHelper;
import redis.clients.jedis.ShardedJedis;

/**
 * ShardTestKeyMaker 테스트 클래스
 * 
 * 샤드 서버에 데이터를 저장하기 위한 테스트 케이스
 * 
 * 500개의 데이터를 샤딩된 클러스터에 저장하고,
 * 저장된 값을 읽어서 비교.
 * 저장한 값과 조회한 값이 다르면 오류 발생시킴
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
