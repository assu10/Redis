package net.sf.redis.ch8.replication;

import net.sf.redis.ch7.redislogger.KeyMaker;

/**
 * ���� Ȯ�� �׽�Ʈ�� ���� Ű ����Ŀ
 * 
 * @author assu
 * @date 2016.05.08
 */
public class ReplicationKeyMaker implements KeyMaker {
	private static final String KEY_PREFIX = "Replication-";
	
	private int index;
	
	/**
	 * Ű ����Ŀ Ŭ������ ���� ������
	 * @param index Ű ������ ���� �ε�����
	 */
	public ReplicationKeyMaker(int index) {
		this.index = index;
	}
	
	@Override
	public String getKey() {
		return KEY_PREFIX + this.index;
	}
}
