package net.sf.redis.ch8.replication;

import redis.clients.jedis.Jedis;

/**
 * 마스터 노드에 데이터를 기록하는 클래스
 * 
 * 모든 복제가 정확하게 이루어지는지 확인되지 않음
 * 
 * @author assu
 * @date 2016.05.08
 */
public class DataWriter {
	private Jedis jedis;
	
	/**
	 * 데이터 저장을 위한 Writer 클래스 생성자
	 * @param jedis 데이터를 저장할 서버에 대한 제디스 연결
	 */
	public DataWriter(Jedis jedis) {
		this.jedis = jedis;
	}
	
	/**
	 * 주어진 키에 데이터 저장
	 * @param key 데이터 저장을 위한 레디스 키
	 * @param value 저장될 데이터
	 */
	public void set(String key, String value) {
		this.jedis.set(key, value);
	}
}
