package net.sf.redis.ch8.replication;

import redis.clients.jedis.Jedis;

/**
 * 마스터 노드에서 복제된 데이터를 슬레이브 노드에서 조회하는 클래스
 * 
 * 모든 복제가 정확하게 이루어지는지 확인되지 않음
 * 
 * @author assu
 * @date 2016.05.08
 */
public class DataReader {
	private Jedis jedis;
	
	/**
	 * 데이터 조회를 위한 Reader 클래스 생성자
	 * @param jedis 데이터를 조회할 서버에 대한 제디스 연결
	 */
	public DataReader(Jedis jedis) {
		this.jedis = jedis;
	}
	
	/**
	 * 주어진 키에 저장된 데이터 조회
	 * @param key 데이터 조회를 위한 키
	 * @return 조회된 데이터, 키가 존재하지 않으면 null 반환
	 */
	public String get(String key) {
		return this.jedis.get(key);
	}
}
