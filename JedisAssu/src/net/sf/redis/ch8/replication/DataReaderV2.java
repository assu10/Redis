package net.sf.redis.ch8.replication;

import java.util.List;

import redis.clients.jedis.Jedis;

/**
 * 마스터 노드에서 복제된 데이터를 슬레이브 노드에서 조회하는 클래스 (blpop 사용)
 * 
 * 모든 복제가 정확하게 이루어지는지 확인되지 않음
 *   -> blpop, lpush 사용
 *   -> 복제 시간을 확인하기 위해 데이터를 저장하고 조회할때까지 걸리는 시간측정도 추가
 * 
 * blpop(대기시간,키[키...]) : 주어진 키들에 저장된 요소 중에서 왼쪽 끝의 요소를 조회하고 해당요소 제거
 *                           저장된 데이터가 없거나 키가 존재하지 않으면 만료시간만큼 대기하며 대기시간이 0일때에는 요소가 입력될때까지 대기.
 *                           주어진 키들에 모두 요소가 존재할때는 명령에 입력된 첫 번째 키에서 요소 조회
 *                           대기시간은 초단위이며 대기시간동안 데이터가 입력되지 않으면 nil 반환
 *                           조회된 요소 반환
 * 
 * @author assu
 * @date 2016.05.08
 */
public class DataReaderV2 {
	private Jedis jedis;
	private static final int TIMEOUT = 1000;
	
	/**
	 * 데이터 조회를 위한 ReaderV2 클래스 생성자
	 * @param jedis 데이터를 조회할 서버에 대한 제디스 연결
	 */
	public DataReaderV2(Jedis jedis) {
		this.jedis = jedis;
	}
	
	/**
	 * 주어진 키에 저장된 데이터를 조회한다
	 * @param key 데이터 조회를 위한 키
	 * @return 조회된 데이터. 만약 키가 존재하지 않으면 데이터가 입력될 때까지 대기한다.
	 */
	public List<String> get(String key) {
		return jedis.brpop(TIMEOUT, key);
	}
}
