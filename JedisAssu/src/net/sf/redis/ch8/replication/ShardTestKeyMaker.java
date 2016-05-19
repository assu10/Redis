package net.sf.redis.ch8.replication;

import net.sf.redis.ch7.redislogger.KeyMaker;

/**
 * 샤드 테스트를 위한 키 메이커
 * 
 * @author assu
 * @date 016.05.19
 */
public class ShardTestKeyMaker implements KeyMaker {
	private static final String keyPrefix = "Sharding Test-";
	private int index;
	
	/**
	 * 키 메이커 클래스를 위한 생성자
	 * @param index 키 생성을 위한 인덱스 값
	 */
	public  ShardTestKeyMaker(int index) {
		this.index = index;
	}
	
	@Override
	public String getKey() {
		return keyPrefix + this.index;
	}

}
