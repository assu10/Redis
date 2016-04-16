package net.sf.redis.ch5;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * RedisInsertTest에서 제디스를 이용하여 십만건의 데이터를 전송한 걸 파이프라인으로 처리
 * 
 * [단일전송 시]
 * 초당 처리건수 : 10810.812
 * 소요시간 : 9.25초
 *
 * [파이프라인으로 전송 시] --> 약 15배 향상
 * 초당 처리 건수 160000.0
 * 소요 시간 0.624초
 */
public class PipelineTest {
	private static final int TOTAL_OPERATIONS = 100000;
	
	public static void main(String[] args) {
		Jedis jedis = new Jedis(FinalConstant.HOST_IP, FinalConstant.PORT);
		jedis.connect();
		
		long start = System.currentTimeMillis();
		
		String key, value;
		Pipeline p = jedis.pipelined();		// 제디스가 제공하는 파이프라인 객체를 생성하고 초기화
		for (int i = 0; i < TOTAL_OPERATIONS; i++) {
			key = value = String.valueOf("key" + (100000+i));
			p.set(key, "-test-" + value);
		}
		p.sync();		// 파이프라인의 응답을 서버로부터 모두 수신하여 제디스의 응답 객체로 변환
		
		jedis.disconnect();
		long elapsed = now()-start;
		System.out.println("초당 처리 건수 " + TOTAL_OPERATIONS / elapsed * 1000f);
		System.out.println("소요 시간 " + elapsed / 1000f + "초");
	}
	
	private static long now() {
		return System.currentTimeMillis();
	}
}
