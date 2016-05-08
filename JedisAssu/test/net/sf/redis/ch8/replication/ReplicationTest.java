package net.sf.redis.ch8.replication;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.ch7.redislogger.KeyMaker;
import redis.clients.jedis.Jedis;

/**
 * ReplicationKeyMaker, DataWriter, DataReader 테스트 클래스
 * 
 * 레디스 서버 복제 테스트
 * 
 * 10만개의 키를 마스터 노드에 기록하고 슬레이브 노드에서 데이터를 읽어서 두 값을 비교.
 * 두 데이터가 동일하다면 테스트가 정상. (=동기화가 실시간으로 이루어짐)
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
					// 나는 160번째에서 오류
					// 원인은 마스터 노드와 슬레이브 노드 간의 데이터 복제가 완료되기 전에 슬레이브 노드에서 데이터를 조회했기 때문
					fail("The value NOT match with a result. [" + value + "][" + result + "]");
				}
			}
		}
	}
}
