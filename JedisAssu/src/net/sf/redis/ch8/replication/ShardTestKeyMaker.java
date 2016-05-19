package net.sf.redis.ch8.replication;

import net.sf.redis.ch7.redislogger.KeyMaker;

/**
 * ���� �׽�Ʈ�� ���� Ű ����Ŀ
 * 
 * @author assu
 * @date 016.05.19
 */
public class ShardTestKeyMaker implements KeyMaker {
	private static final String keyPrefix = "Sharding Test-";
	private int index;
	
	/**
	 * Ű ����Ŀ Ŭ������ ���� ������
	 * @param index Ű ������ ���� �ε��� ��
	 */
	public  ShardTestKeyMaker(int index) {
		this.index = index;
	}
	
	@Override
	public String getKey() {
		return keyPrefix + this.index;
	}

}
