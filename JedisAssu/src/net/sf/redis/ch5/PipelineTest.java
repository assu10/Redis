package net.sf.redis.ch5;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * RedisInsertTest���� ���𽺸� �̿��Ͽ� �ʸ����� �����͸� ������ �� �������������� ó��
 * 
 * [�������� ��]
 * �ʴ� ó���Ǽ� : 10810.812
 * �ҿ�ð� : 9.25��
 *
 * [�������������� ���� ��] --> �� 15�� ���
 * �ʴ� ó�� �Ǽ� 160000.0
 * �ҿ� �ð� 0.624��
 */
public class PipelineTest {
	private static final int TOTAL_OPERATIONS = 100000;
	
	public static void main(String[] args) {
		Jedis jedis = new Jedis(FinalConstant.HOST_IP, FinalConstant.PORT);
		jedis.connect();
		
		long start = System.currentTimeMillis();
		
		String key, value;
		Pipeline p = jedis.pipelined();		// ���𽺰� �����ϴ� ���������� ��ü�� �����ϰ� �ʱ�ȭ
		for (int i = 0; i < TOTAL_OPERATIONS; i++) {
			key = value = String.valueOf("key" + (100000+i));
			p.set(key, "-test-" + value);
		}
		p.sync();		// ������������ ������ �����κ��� ��� �����Ͽ� ������ ���� ��ü�� ��ȯ
		
		jedis.disconnect();
		long elapsed = now()-start;
		System.out.println("�ʴ� ó�� �Ǽ� " + TOTAL_OPERATIONS / elapsed * 1000f);
		System.out.println("�ҿ� �ð� " + elapsed / 1000f + "��");
	}
	
	private static long now() {
		return System.currentTimeMillis();
	}
}
