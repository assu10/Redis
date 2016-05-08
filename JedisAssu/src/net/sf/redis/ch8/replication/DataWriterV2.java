package net.sf.redis.ch8.replication;

import redis.clients.jedis.Jedis;

/**
 * ������ ��忡 �����͸� ����ϴ� Ŭ���� (lpush ���)
 * 
 * ��� ������ ��Ȯ�ϰ� �̷�������� Ȯ�ε��� ����
 *   -> blpop, lpush ���
 *   -> ���� �ð��� Ȯ���ϱ� ���� �����͸� �����ϰ� ��ȸ�Ҷ����� �ɸ��� �ð������� �߰�
 * 
 * blpop(Ű[Ű...] ���ð�) : �־��� Ű�鿡 ����� ��� �߿��� ���� ���� ��Ҹ� ��ȸ�ϰ� �ش��� ����
 *                           ��, ���ð��� 0�϶����� ��Ұ� �Էµɶ����� ���.
 *                           �־��� Ű�鿡 ��� ��Ұ� �����Ҷ��� ��ɿ� �Էµ� ù ��° Ű���� ��� ��ȸ
 *                           ���ð��� �ʴ����̸� ���ð����� �����Ͱ� �Էµ��� ������ nil ��ȯ
 *                           ��ȸ�� ��� ��ȯ
 * 
 * @author assu
 * @date 2016.05.08
 */
public class DataWriterV2 {
	private Jedis jedis;
	
	/**
	 * ������ ������ ���� Writer Ŭ���� ������
	 * @param jedis �����͸� ������ ������ ���� ���� ����
	 */
	public DataWriterV2(Jedis jedis) {
		this.jedis = jedis;
	}
	
	/**
	 * �־��� Ű�� ������ ����
	 * @param key ������ ������ ���� ���� Ű
	 * @param value ����� ������
	 */
	public void set(String key, String value) {
		jedis.lpush(key, value);
	}
}
