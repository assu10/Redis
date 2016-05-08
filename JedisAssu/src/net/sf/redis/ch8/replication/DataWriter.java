package net.sf.redis.ch8.replication;

import redis.clients.jedis.Jedis;

/**
 * ������ ��忡 �����͸� ����ϴ� Ŭ����
 * 
 * ��� ������ ��Ȯ�ϰ� �̷�������� Ȯ�ε��� ����
 * 
 * @author assu
 * @date 2016.05.08
 */
public class DataWriter {
	private Jedis jedis;
	
	/**
	 * ������ ������ ���� Writer Ŭ���� ������
	 * @param jedis �����͸� ������ ������ ���� ���� ����
	 */
	public DataWriter(Jedis jedis) {
		this.jedis = jedis;
	}
	
	/**
	 * �־��� Ű�� ������ ����
	 * @param key ������ ������ ���� ���� Ű
	 * @param value ����� ������
	 */
	public void set(String key, String value) {
		this.jedis.set(key, value);
	}
}
