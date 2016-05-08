package net.sf.redis.ch8.replication;

import redis.clients.jedis.Jedis;

/**
 * 마스터 노드에 데이터를 기록하는 클래스 (lpush 사용)
 * 
 * 모든 복제가 정확하게 이루어지는지 확인되지 않음
 *   -> blpop, lpush 사용
 *   -> 복제 시간을 확인하기 위해 데이터를 저장하고 조회할때까지 걸리는 시간측정도 추가
 * 
 * blpop(키[키...] 대기시간) : 주어진 키들에 저장된 요소 중에서 왼쪽 끝의 요소를 조회하고 해당요소 제거
 *                           단, 대기시간이 0일때에는 요소가 입력될때까지 대기.
 *                           주어진 키들에 모두 요소가 존재할때는 명령에 입력된 첫 번째 키에서 요소 조회
 *                           대기시간은 초단위이며 대기시간동안 데이터가 입력되지 않으면 nil 반환
 *                           조회된 요소 반환
 * 
 * @author assu
 * @date 2016.05.08
 */
public class DataWriterV2 {
	private Jedis jedis;
	
	/**
	 * 데이터 저장을 위한 Writer 클래스 생성자
	 * @param jedis 데이터를 저장할 서버에 대한 제디스 연결
	 */
	public DataWriterV2(Jedis jedis) {
		this.jedis = jedis;
	}
	
	/**
	 * 주어진 키에 데이터 저장
	 * @param key 데이터 저장을 위한 레디스 키
	 * @param value 저장될 데이터
	 */
	public void set(String key, String value) {
		jedis.lpush(key, value);
	}
}
