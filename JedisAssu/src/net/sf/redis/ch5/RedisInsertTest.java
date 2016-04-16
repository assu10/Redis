package net.sf.redis.ch5;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 제디스를 이용하여 십만건의 데이터를 전송
 * 
 * 초당 처리건수 : 10810.812
 * 소요시간 : 9.25초

 * @author juhyun10
 * @date 2016.03.10
 */
public class RedisInsertTest {
	private static final float TOTAL_OP = 100000F;	// 입력을 위한 전체 데이터 건수
	
	public static void main(String[] args) {

		JedisPool pool = new JedisPool("10.10.41.39", 6379);	// 레디스 서버의 제디스 연결풀 생성
		Jedis jedis = pool.getResource();	// 생성된 제디스 풀에서 첫번째 커넥션 가져옴
		String key, value;
		long start = now();
		
		for (int i=1; i<=TOTAL_OP; i++) {
			key = value = String.valueOf("key" + (100000000+i));	// 레디스에 저장할 키와 값을 12자리로 고정하기 위한 코드
			jedis.set(key, value);
		}
		
		long elapsed = now() - start;
		System.out.println("초당 처리건수 : "+  TOTAL_OP /elapsed * 1000f);
		System.out.println("소요시간 : " + elapsed / 1000f + "초");
		
		jedis.disconnect();
		pool.destroy();
	}
	
	private static long now() {
		return System.currentTimeMillis();
	}
}
