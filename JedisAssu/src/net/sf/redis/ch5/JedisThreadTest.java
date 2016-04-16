package net.sf.redis.ch5;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * �ʸ� ���� �����͸� ���� ����Ǯ�� ���� �����带 ����Ͽ� ����
 * 
 * ������ ���� : 5.0 ��
 * �ʴ� ó�� �Ǽ� : 18218.254
 * �ҿ� �ð� : 5.489 ��
 * 
 * @author juhyun10
 * @date 2016.03.10
 */
public class JedisThreadTest {
	private static final float TOTAL_OP = 100000f;
	private static final float THREAD = 5;	// ���α׷� ���� �� ������ ������ ���� ����
	
	public static void main(String[] args) {
		Config config = new Config();
		config.maxActive = 500;		// ObjectPool�� �ִ밹��
		// ObjectPool�� ��ϵ� ������ ������ �ִ� ������ �������� ��, ���ο� ��û�� ���� ���뿬���� ���涧���� ���
		config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
		final JedisPool pool = new JedisPool(config, FinalConstant.HOST_IP, FinalConstant.PORT, 10000);
		
		final long start = now();
		
		
		// �ڹ� ���ø����̼��� ���������ϸ� JVM�� �����带 ���������Ű�� ������ �������� �����۾��� ����� �� ����,
		// �Ҵ��� �ڿ��̳� ���� �������� ����ó���� �ȵ�
		// shutdownhook : JVM�� �˴ٿ�� �� �˴ٿ� �̺�Ʈ�� ����ä�� � �ڵ带 ������ �� ���������� �˴ٿ�ǵ��� ��.
		//                �˴ٿ���ũ�� ���� ���۵��� ���� Thread ��ü�� ���
		Runtime.getRuntime().addShutdownHook(new Thread() {		// �ڹ� ���α׷��� ����� �� ����Ǵ� �̺�Ʈ ������ ���
			@Override
			public void run() {
				long elapsed = now() - start;
				
				System.out.println("������ ���� : " + THREAD + " ��");
				System.out.println("�ʴ� ó�� �Ǽ� : " + TOTAL_OP / elapsed * 1000f); 	// ��ü ����ð��� �ʴ� ���� ��
				System.out.println("�ҿ� �ð� : " + elapsed / 1000f + " ��");
			}
		});
	
		JedisThreadTest test = new JedisThreadTest();
		for (int i = 0; i < THREAD; i++) {
			test.makeWorker(pool, i).start();	// ������ ������ ������ŭ ������ ����, �����ϴ� �����忡 ����Ǯ�� �ε����� ���ڷ� ����
		}
		//pool.destroy();
	}
	
	/**
	 * Thread�� �����ϱ� ���� Runnable �������̽� ����.
	 * 
	 * @param pool
	 * @param idx
	 * @return
	 */
	private Thread makeWorker(final JedisPool pool, final int idx) {
		Thread thread = new Thread(new Runnable() {		// ���� �����͸� ������ ������ ����
			
			@Override
			public void run() {
				String key, value;
				Jedis jedis = pool.getResource();
				
				for (int i = 0; i < TOTAL_OP; i++) {
					// ������ ������ �ε����� �ش��ϴ� Ű�� �����ϰ� �ƴϸ� �������� �ʴ´�.
					// �� �κ��� �� �����尡 õ������ ��û�� �����ϰ� ������ �����ϰ� ��.
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
	 * ����ð�
	 * @return
	 */
	private static long now() {
		return System.currentTimeMillis();
	}
	
}
