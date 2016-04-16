package net.sf.redis.ch5;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 십만 건의 데이터를 제디스 연결풀과 다중 스레드를 사용하여 전송
 * 
 * 스레드 갯수 : 5.0 개
 * 초당 처리 건수 : 18218.254
 * 소요 시간 : 5.489 초
 * 
 * @author juhyun10
 * @date 2016.03.10
 */
public class JedisThreadTest {
	private static final float TOTAL_OP = 100000f;
	private static final float THREAD = 5;	// 프로그램 실행 시 시작할 스레드 개수 지정
	
	public static void main(String[] args) {
		Config config = new Config();
		config.maxActive = 500;		// ObjectPool의 최대갯수
		// ObjectPool에 등록된 연결이 설정한 최대 개수에 도달했을 때, 새로운 요청이 오면 가용연결이 생길때까지 대기
		config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
		final JedisPool pool = new JedisPool(config, FinalConstant.HOST_IP, FinalConstant.PORT, 10000);
		
		final long start = now();
		
		
		// 자바 어플리케이션을 강제종료하면 JVM은 스레드를 강제종료시키기 때문에 안정적인 종료작업이 진행될 수 없고,
		// 할당한 자원이나 상태 정보들의 정상처리가 안됨
		// shutdownhook : JVM이 셧다운될 때 셧다운 이벤트를 가로채서 어떤 코드를 수행한 후 최종적으로 셧다운되도록 함.
		//                셧다운후크는 아직 시작되지 않은 Thread 객체를 사용
		Runtime.getRuntime().addShutdownHook(new Thread() {		// 자바 프로그램이 종료될 때 실행되는 이벤트 스레드 등록
			@Override
			public void run() {
				long elapsed = now() - start;
				
				System.out.println("스레드 갯수 : " + THREAD + " 개");
				System.out.println("초당 처리 건수 : " + TOTAL_OP / elapsed * 1000f); 	// 전체 실행시간과 초당 전송 수
				System.out.println("소요 시간 : " + elapsed / 1000f + " 초");
			}
		});
	
		JedisThreadTest test = new JedisThreadTest();
		for (int i = 0; i < THREAD; i++) {
			test.makeWorker(pool, i).start();	// 지정된 스레스 개수만큼 스레드 생성, 생성하는 스레드에 연결풀과 인덱스를 인자로 지정
		}
		//pool.destroy();
	}
	
	/**
	 * Thread를 구현하기 위해 Runnable 인터페이스 구현.
	 * 
	 * @param pool
	 * @param idx
	 * @return
	 */
	private Thread makeWorker(final JedisPool pool, final int idx) {
		Thread thread = new Thread(new Runnable() {		// 실제 데이터를 전송할 스레드 생성
			
			@Override
			public void run() {
				String key, value;
				Jedis jedis = pool.getResource();
				
				for (int i = 0; i < TOTAL_OP; i++) {
					// 지정된 스레드 인덱스에 해당하는 키를 생성하고 아니면 실행하지 않는다.
					// 이 부분은 각 스레드가 천만개의 요청을 공평하게 나누어 전송하게 함.
					if (i % THREAD == idx) {
						key = value = String.valueOf("key-test" + (100000000+i));
						jedis.set(key, value);
					}
				}
				pool.returnResource(jedis);
			}
		});
		return thread;
	}
	
	/**
	 * 현재시간
	 * @return
	 */
	private static long now() {
		return System.currentTimeMillis();
	}
	
}
