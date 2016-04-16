package net.sf.redis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 레디스에 연결풀을 생성하고 관리하는 역할
 * 
 * 레디스에 연결 가능한 클라이언트의 개수는 제한적이기 때문에 제디스 연결풀을 사용해야함.
 * 
 * @author assu
 * @date 2016.03.31
 */
public class JedisHelper {
	protected static final String REDIS_HOST = "127.0.0.1";
	protected static final int REDIS_PORT = 6379;
	private final Set<Jedis> connectionList = new HashSet<Jedis>();
	private JedisPool pool;
	
	/**
	 * 제디스 연결풀 생성을 위한 도우미 클래스 내부 생성자.
	 * 싱글톤 패턴이므로 외부에서 호출불가.
	 * 
	 * ObjectPool의 최대갯수 : 20
	 * ObjedtPool에 등록된 연결이 설정한 최대 개수에 도달했을 때, 새로운 요청이 오면 가용연결이 생길때까지 대기
	 */
	private JedisHelper() {
		Config config = new Config();
		config.maxActive = 20;
		config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
	
		this.pool = new JedisPool(config, REDIS_HOST, REDIS_PORT, 5000);
	}
	
	/**
	 * 싱글톤 처리를 위한 홀더클래스.
	 * 제디스 연결풀이 포함된 헬퍼 객체 반환.
	 */
	private static class LazyHolder {
		private static final JedisHelper INSTANCE = new JedisHelper();
	}
	
	/**
	 * 싱글톤 객체를 가져옴
	 * @return 제디스 헬퍼 객체 
	 */
	public static JedisHelper getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	/**
	 * 연결된 생성풀에서 하나의 제디스 클라이언트 연결을 가져옴
	 * @return 제디스 객체
	 */
	public final Jedis getConnection() {
		Jedis jedis = this.pool.getResource();
		this.connectionList.add(jedis);
		
		return jedis;
	}
	
	/**
	 * 사용이 완료된 제디스 객체 회수 (사용이 완료된 제디스의 연결을 연결풀로 돌려줌)
	 * @param jedis 사용 완료된 제디스 객체
	 */
	public final void returnResource(Jedis jedis) {
		this.pool.returnResource(jedis);
	}
	
	/**
	 * 제디스 연결풀 제거
	 */
	public final void destroyPool() {
		Iterator<Jedis> jedisList = this.connectionList.iterator();
		while (jedisList.hasNext()) {
			Jedis jedis = jedisList.next();
			this.pool.returnResource(jedis);
		}
		this.pool.destroy();
	}
}
