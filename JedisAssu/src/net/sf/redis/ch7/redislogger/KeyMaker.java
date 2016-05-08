package net.sf.redis.ch7.redislogger;

/**
 * 키 메이커
 * 
 * 키를 생성하는 로직의 중복 제거하기 위함
 * 
 * @author assu
 * @date 2016.05.08
 */
public interface KeyMaker {
	/** 키 생성기로부터 만들어진 키를 가져옴 */
	public String getKey();
}
