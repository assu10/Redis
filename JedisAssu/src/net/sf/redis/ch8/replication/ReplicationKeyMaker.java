package net.sf.redis.ch8.replication;

import net.sf.redis.ch7.redislogger.KeyMaker;

/**
 * 복제 확인 테스트를 위한 키 메이커
 * 
 * @author assu
 * @date 2016.05.08
 */
public class ReplicationKeyMaker implements KeyMaker {
	private static final String KEY_PREFIX = "Replication-";
	
	private int index;
	
	/**
	 * 키 메이커 클래스를 위한 생성자
	 * @param index 키 생성을 위한 인덱스값
	 */
	public ReplicationKeyMaker(int index) {
		this.index = index;
	}
	
	@Override
	public String getKey() {
		return KEY_PREFIX + this.index;
	}
}
