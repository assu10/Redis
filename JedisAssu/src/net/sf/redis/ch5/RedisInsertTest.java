package net.sf.redis.ch5;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * ���𽺸� �̿��Ͽ� �ʸ����� �����͸� ����
 * 
 * �ʴ� ó���Ǽ� : 10810.812
 * �ҿ�ð� : 9.25��

 * @author juhyun10
 * @date 2016.03.10
 */
public class RedisInsertTest {
	private static final float TOTAL_OP = 100000F;	// �Է��� ���� ��ü ������ �Ǽ�
	
	public static void main(String[] args) {

		JedisPool pool = new JedisPool("10.10.41.39", 6379);	// ���� ������ ���� ����Ǯ ����
		Jedis jedis = pool.getResource();	// ������ ���� Ǯ���� ù��° Ŀ�ؼ� ������
		String key, value;
		long start = now();
		
		for (int i=1; i<=TOTAL_OP; i++) {
			key = value = String.valueOf("key" + (100000000+i));	// ���𽺿� ������ Ű�� ���� 12�ڸ��� �����ϱ� ���� �ڵ�
			jedis.set(key, value);
		}
		
		long elapsed = now() - start;
		System.out.println("�ʴ� ó���Ǽ� : "+  TOTAL_OP /elapsed * 1000f);
		System.out.println("�ҿ�ð� : " + elapsed / 1000f + "��");
		
		jedis.disconnect();
		pool.destroy();
	}
	
	private static long now() {
		return System.currentTimeMillis();
	}
}
