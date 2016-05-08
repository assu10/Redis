package net.sf.redis.ch8.replication;

import redis.clients.jedis.Jedis;

/**
 * ������ ��忡�� ������ �����͸� �����̺� ��忡�� ��ȸ�ϴ� Ŭ����
 * 
 * ��� ������ ��Ȯ�ϰ� �̷�������� Ȯ�ε��� ����
 * 
 * @author assu
 * @date 2016.05.08
 */
public class DataReader {
	private Jedis jedis;
	
	/**
	 * ������ ��ȸ�� ���� Reader Ŭ���� ������
	 * @param jedis �����͸� ��ȸ�� ������ ���� ���� ����
	 */
	public DataReader(Jedis jedis) {
		this.jedis = jedis;
	}
	
	/**
	 * �־��� Ű�� ����� ������ ��ȸ
	 * @param key ������ ��ȸ�� ���� Ű
	 * @return ��ȸ�� ������, Ű�� �������� ������ null ��ȯ
	 */
	public String get(String key) {
		return this.jedis.get(key);
	}
}
