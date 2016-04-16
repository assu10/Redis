package net.sf.redis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * ���𽺿� ����Ǯ�� �����ϰ� �����ϴ� ����
 * 
 * ���𽺿� ���� ������ Ŭ���̾�Ʈ�� ������ �������̱� ������ ���� ����Ǯ�� ����ؾ���.
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
	 * ���� ����Ǯ ������ ���� ����� Ŭ���� ���� ������.
	 * �̱��� �����̹Ƿ� �ܺο��� ȣ��Ұ�.
	 * 
	 * ObjectPool�� �ִ밹�� : 20
	 * ObjedtPool�� ��ϵ� ������ ������ �ִ� ������ �������� ��, ���ο� ��û�� ���� ���뿬���� ���涧���� ���
	 */
	private JedisHelper() {
		Config config = new Config();
		config.maxActive = 20;
		config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
	
		this.pool = new JedisPool(config, REDIS_HOST, REDIS_PORT, 5000);
	}
	
	/**
	 * �̱��� ó���� ���� Ȧ��Ŭ����.
	 * ���� ����Ǯ�� ���Ե� ���� ��ü ��ȯ.
	 */
	private static class LazyHolder {
		private static final JedisHelper INSTANCE = new JedisHelper();
	}
	
	/**
	 * �̱��� ��ü�� ������
	 * @return ���� ���� ��ü 
	 */
	public static JedisHelper getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	/**
	 * ����� ����Ǯ���� �ϳ��� ���� Ŭ���̾�Ʈ ������ ������
	 * @return ���� ��ü
	 */
	public final Jedis getConnection() {
		Jedis jedis = this.pool.getResource();
		this.connectionList.add(jedis);
		
		return jedis;
	}
	
	/**
	 * ����� �Ϸ�� ���� ��ü ȸ�� (����� �Ϸ�� ������ ������ ����Ǯ�� ������)
	 * @param jedis ��� �Ϸ�� ���� ��ü
	 */
	public final void returnResource(Jedis jedis) {
		this.pool.returnResource(jedis);
	}
	
	/**
	 * ���� ����Ǯ ����
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
