package net.sf.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;

/**
 * 샤딩된 레디스 클러스텅 접속된 제디스 연결풀 관리
 * 
 * @author assu
 * @date 2016.05.19
 */
public class ShardedJedisHelper {
	protected static final String SHARD1_HOST = "192.168.56.102";
	protected static final int SHARD1_PORT = 6380;
	protected static final String SHARD2_HOST = "192.168.56.102";
	protected static final int SHARD2_PORT = 6381;
	
	/** 샤딩된 레디스 서벙 접속된 제디스 연결 저장 */
	private final Set<ShardedJedis> connectionList = new HashSet<ShardedJedis>();
	/** 샤딩된 제디스 풀 객체 선언 */
	private ShardedJedisPool shardedPool;
	
	/**
	 * 싱글콘 처리를 위한 홀더 클래스
	 * 제디스 연결풀이 포함된 헬퍼 객체 반환
	 */
	private static class LazyHolder {
		private static final ShardedJedisHelper INSTANCE = new ShardedJedisHelper();
	}
	
	/**
	 * 샤딩된 제디스 연결풀 생성을 위한 헬퍼 클래스 내부 생성자
	 * 싱글톤 패턴이므로 외부에서 호출 불가
	 * 
	 * ObjectPool의 최대갯수 : 20
	 * ObjedtPool에 등록된 연결이 설정한 최대 개수에 도달했을 때, 새로운 요청이 오면 가용연결이 생길때까지 대기
	 */
	private ShardedJedisHelper() {
		Config config = new Config();
		config.maxActive = 20;
		config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
		
		// 샤드 서버의 목록을 지정. 여기에 지정된 서버 목록을 대상으로 샤딩 수행.
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		shards.add(new JedisShardInfo(SHARD1_HOST, SHARD1_PORT));
		shards.add(new JedisShardInfo(SHARD2_HOST, SHARD2_PORT));
		
		// 클러스터의 샤딩을 위한 해시 메서드로 MurmurHash 사용.
		// 만약 해시 메서드가 변경되면 이전에 저장된 데이터를 찾지 못하는 상황이 발생함.
		this.shardedPool = new ShardedJedisPool(config, shards, Hashing.MURMUR_HASH);
	}
	
	/**
	 * 싱글톤 객체 가져옴
	 * @return 제디스 헬퍼 객체
	 */
	public static ShardedJedisHelper getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	/**
	 * 제디스 클라이언트 연결 가져옴
	 * @return 제디스 객체
	 */
	final public ShardedJedis getConnection() {
		// 샤딩된 제디스 연결풀에서 커넥션 가져옴.
		ShardedJedis jedis = this.shardedPool.getResource();
		this.connectionList.add(jedis);
		return jedis;
	}
	
	/**
	 * 사용이 완료된 제디스 객체 회수
	 * @param jedis 사용완료된 제디스 객체
	 */
	final public void returnResource(ShardedJedis jedis) {
		this.shardedPool.returnResource(jedis);
	}
	
	/**
	 * 제디스 연결풀 제거
	 */
	final public void destroyPool() {
		Iterator<ShardedJedis> jedisList = this.connectionList.iterator();
		while (jedisList.hasNext()) {
			ShardedJedis jedis = jedisList.next();
			this.shardedPool.returnResource(jedis);
		}
		this.shardedPool.destroy();
	}
}
