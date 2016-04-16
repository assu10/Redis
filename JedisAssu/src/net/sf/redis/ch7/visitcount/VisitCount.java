package net.sf.redis.ch7.visitcount;

import java.util.ArrayList;
import java.util.List;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * 이벤트 아이디를 인자로 받아서 방문 횟수 증가시키고 조회
 * --> 날짜별 조회가 안됨
 * 
 * 이벤트 페이지 방문횟수 저장
 * 모든 페이지 방문횟수 조회
 * 개별 이벤트 페이지 방문횟수 조회
 * 
 * @author assu
 * @date 2016.04.16
 */
public class VisitCount {
	private JedisHelper jedisHelper;
	private Jedis jedis;
	private static final String KEY_EVENT_CLICK_TOTAL = "event:click:total";
	private static final String KEY_EVENT_CLICK = "event:click:";
	
	/**
	 * 방문횟수 처리를 위한 클래스 생성자.
	 * @param jedisHelper 제디스 헬퍼 객체
	 */
	public VisitCount(JedisHelper jedisHelper) {
		this.jedisHelper = jedisHelper;
		this.jedis = this.jedisHelper.getConnection();
	}
	
	/**
	 * 요청된 이벤트 페이지의 방문횟수와 전체 이벤트 페이지의 방문횟수 증가
	 * @param eventId 이벤트아이디
	 * @return 요청된 이벤트 페이지의 총 방문횟수
	 */
	public Long addVisit(String eventId) {
		this.jedis.incr(KEY_EVENT_CLICK_TOTAL);
		return this.jedis.incr(KEY_EVENT_CLICK + eventId);
	}
	
	/**
	 * 전체 이벤트 페이지 방문횟수 조회
	 * @return 전체 이벤트 페이지 방문횟수
	 */
	public String getVisitTotalCount() {
		return this.jedis.get(KEY_EVENT_CLICK_TOTAL);
	}
	
	/**
	 * 요청된 이벤트 아이디들에 대한 방문횟수 조회
	 * @param strings
	 * @return
	 */
	public List<String> getVisitCount(String... eventList) {
		List<String> result = new ArrayList<String>();
		for (int i=0; i<eventList.length; i++) {
			result.add(this.jedis.get(KEY_EVENT_CLICK + eventList[i]));
		}
		return result;
	}
}
