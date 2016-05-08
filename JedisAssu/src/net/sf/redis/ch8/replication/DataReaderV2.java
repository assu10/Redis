package net.sf.redis.ch8.replication;

import java.util.List;

import redis.clients.jedis.Jedis;

/**
 * ������ ��忡�� ������ �����͸� �����̺� ��忡�� ��ȸ�ϴ� Ŭ���� (blpop ���)
 * 
 * ��� ������ ��Ȯ�ϰ� �̷�������� Ȯ�ε��� ����
 *   -> blpop, lpush ���
 *   -> ���� �ð��� Ȯ���ϱ� ���� �����͸� �����ϰ� ��ȸ�Ҷ����� �ɸ��� �ð������� �߰�
 * 
 * blpop(���ð�,Ű[Ű...]) : �־��� Ű�鿡 ����� ��� �߿��� ���� ���� ��Ҹ� ��ȸ�ϰ� �ش��� ����
 *                           ����� �����Ͱ� ���ų� Ű�� �������� ������ ����ð���ŭ ����ϸ� ���ð��� 0�϶����� ��Ұ� �Էµɶ����� ���.
 *                           �־��� Ű�鿡 ��� ��Ұ� �����Ҷ��� ��ɿ� �Էµ� ù ��° Ű���� ��� ��ȸ
 *                           ���ð��� �ʴ����̸� ���ð����� �����Ͱ� �Էµ��� ������ nil ��ȯ
 *                           ��ȸ�� ��� ��ȯ
 * 
 * @author assu
 * @date 2016.05.08
 */
public class DataReaderV2 {
	private Jedis jedis;
	private static final int TIMEOUT = 1000;
	
	/**
	 * ������ ��ȸ�� ���� ReaderV2 Ŭ���� ������
	 * @param jedis �����͸� ��ȸ�� ������ ���� ���� ����
	 */
	public DataReaderV2(Jedis jedis) {
		this.jedis = jedis;
	}
	
	/**
	 * �־��� Ű�� ����� �����͸� ��ȸ�Ѵ�
	 * @param key ������ ��ȸ�� ���� Ű
	 * @return ��ȸ�� ������. ���� Ű�� �������� ������ �����Ͱ� �Էµ� ������ ����Ѵ�.
	 */
	public List<String> get(String key) {
		return jedis.brpop(TIMEOUT, key);
	}
}
