package net.sf.redis.ch8.replication;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.ch7.redislogger.KeyMaker;
import redis.clients.jedis.Jedis;

/**
 * ReplicationKeyMakerV2, DataWriterV2, DataReaderV2 테스트 클래스
 * 
 * 레디스 서버 복제 테스트
 * 
 * 10만개의 키를 마스터 노드에 기록하고 슬레이브 노드에서 데이터를 읽어서 두 값을 비교.
 * 두 데이터가 동일하다면 테스트가 정상. (=동기화가 실시간으로 이루어짐)
 *   + 데이터 복제 상태 확인 
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
				// 복제데이터를 조회하는 시간이 1밀리초 이상 소요되면 로그 출력, 99% 이상의 데이터가 1밀리초 안에 복제됨
				if (elapse > 1) {
					System.out.println("elapse : " + elapse);
				}
			} else {
				fail("The value NOT match with a result. [" + value + "][" + result + "]");
			}
					
		}
	}
}
