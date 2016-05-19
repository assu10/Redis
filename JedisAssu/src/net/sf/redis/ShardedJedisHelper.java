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
 * ������ ���� Ŭ������ ���ӵ� ���� ����Ǯ ����
 * 
 * @author assu
 * @date 2016.05.19
 */
public class ShardedJedisHelper {
	protected static final String SHARD1_HOST = "192.168.56.102";
	protected static final int SHARD1_PORT = 6380;
	protected static final String SHARD2_HOST = "192.168.56.102";
	protected static final int SHARD2_PORT = 6381;
	
	/** ������ ���� ���� ���ӵ� ���� ���� ���� */
	private final Set<ShardedJedis> connectionList = new HashSet<ShardedJedis>();
	/** ������ ���� Ǯ ��ü ���� */
	private ShardedJedisPool shardedPool;
	
	/**
	 * �̱��� ó���� ���� Ȧ�� Ŭ����
	 * ���� ����Ǯ�� ���Ե� ���� ��ü ��ȯ
	 */
	private static class LazyHolder {
		private static final ShardedJedisHelper INSTANCE = new ShardedJedisHelper();
	}
	
	/**
	 * ������ ���� ����Ǯ ������ ���� ���� Ŭ���� ���� ������
	 * �̱��� �����̹Ƿ� �ܺο��� ȣ�� �Ұ�
	 * 
	 * ObjectPool�� �ִ밹�� : 20
	 * ObjedtPool�� ��ϵ� ������ ������ �ִ� ������ �������� ��, ���ο� ��û�� ���� ���뿬���� ���涧���� ���
	 */
	private ShardedJedisHelper() {
		Config config = new Config();
		config.maxActive = 20;
		config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
		
		// ���� ������ ����� ����. ���⿡ ������ ���� ����� ������� ���� ����.
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		shards.add(new JedisShardInfo(SHARD1_HOST, SHARD1_PORT));
		shards.add(new JedisShardInfo(SHARD2_HOST, SHARD2_PORT));
		
		// Ŭ�������� ������ ���� �ؽ� �޼���� MurmurHash ���.
		// ���� �ؽ� �޼��尡 ����Ǹ� ������ ����� �����͸� ã�� ���ϴ� ��Ȳ�� �߻���.
		this.shardedPool = new ShardedJedisPool(config, shards, Hashing.MURMUR_HASH);
	}
	
	/**
	 * �̱��� ��ü ������
	 * @return ���� ���� ��ü
	 */
	public static ShardedJedisHelper getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	/**
	 * ���� Ŭ���̾�Ʈ ���� ������
	 * @return ���� ��ü
	 */
	final public ShardedJedis getConnection() {
		// ������ ���� ����Ǯ���� Ŀ�ؼ� ������.
		ShardedJedis jedis = this.shardedPool.getResource();
		this.connectionList.add(jedis);
		return jedis;
	}
	
	/**
	 * ����� �Ϸ�� ���� ��ü ȸ��
	 * @param jedis ���Ϸ�� ���� ��ü
	 */
	final public void returnResource(ShardedJedis jedis) {
		this.shardedPool.returnResource(jedis);
	}
	
	/**
	 * ���� ����Ǯ ����
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
