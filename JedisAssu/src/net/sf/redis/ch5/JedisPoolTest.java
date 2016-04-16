package net.sf.redis.ch5;

import java.util.Map;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * JedisPool이라는 연결 풀 객체를 사용하여 커넥션 재사용
 * 연결 풀을 사용하여 해시 데이터에 정보 저장 후 조회
 * 제디스는 아파치의 ObjectPool 라이브러리를 사용하여 연결 풀을 제공함.
 * @author juhyun10
 * @date 2016.03.07
 */
public class JedisPoolTest {
	public static void main(String[] args) {
		Config config = new Config();
		config.maxActive = 20;	// ObjectPool의 최대갯수 지정, 디폴드 8, 마이너스면 제한 없음.
		// ObjectPool에 등록된 연결이 설정한 최대 개수에 도달했을 때, 새로운 요청에 대한 처리방법 지정.
		// WHEN_EXHAUSTED_BLOCK : 가용연결이 생길때까지 대기
		config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
		
		// 위의 설정값을 기초로 하여 10.10~ 6379 포트에서 동작하는 레디스 서버의 제디스 연결풀을 생성.
		// 통상적으로 WAS에서 할때는 연결 풀을 전역상수(static final 상수) 에 등록해서 사용함.
		JedisPool pool = new JedisPool(config, "127.0.0.1", 6379);
		
		// 생성된 제디스 풀에서 첫 번째 커넥션을 가져옴.
		Jedis firstClient = pool.getResource();
		
		// 첫번째 커넥션을 이용하여 info:재무개발팀 키에 이주현 정보 입력
		firstClient.hset("info:재무개발팀", "이름", "이주현");
		firstClient.hset("info:재무개발팀", "생일", "840509");
		
		// 생성된 제디스 풀에서 두 번째 커넥션을 가져옴.
		Jedis secondClient = pool.getResource();
		
		// info:재무개발팀 키에 저장된 정보를 조회
		Map<String, String> result = secondClient.hgetAll("info:재무개발팀");
		System.out.println("이름 : " + result.get("이름"));
		System.out.println("생일 : " + result.get("생일"));
		
		// 첫번째 커넥션을 풀로 돌려줌
		pool.returnResource(firstClient);
		
		pool.returnResource(secondClient);
		
		// 생성된 제디스 연결 풀 제거
		pool.destroy();
	}
}
