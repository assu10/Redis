package net.sf.redis.ch7.visitcount;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * 이벤트 아이디를 인자로 받아서 방문 횟수 증가시키고 조회
 *  + 날짜별 방문횟수와 날짜랑 전체 이벤트 페이지 방문횟수 조회
 *  + 지정된 이벤트의 시작일자 정보 저장 (해시데이터로 처리)
 *  
 * 날짜별 이벤트 페이지 방문횟수 저장
 * 날짜별 모든 페이지 방문횟수 조회
 * 날짜별 개별 이벤트 페이지 방문횟수 조회
 * 모든 날짜의 방문횟수 조회
 * 
 * @author assu
 * @date 2016.04.16
 */
public class VisitCountOfDayV2 {
	private JedisHelper jedisHelper;
	private Jedis jedis;
	private static final String KEY_EVENT_DAILY_CLICK_TOTAL = "event:daily:click:total:hash";
	private static final String KEY_EVENT_DAILY_CLICK = "event:daily:click:hash:";
	
	/**
	 * 방문횟수 처리를 위한 클래스 생성자 
	 * @param jedisHelper 제디스 헬퍼 객체
	 */
	public VisitCountOfDayV2(JedisHelper jedisHelper) {
		this.jedisHelper = jedisHelper;
		this.jedis = this.jedisHelper.getConnection();
	}
	
	/**
	 * 이벤트 아이디에 해당하는 날짜별 방문횟수와 날짜별 전체 방문횟수 증가
	 * @param eventId 이벤트 아이디
	 * @return 이벤트 페이지 방문횟수
	 */
	public Long addVisit(String eventId) {
		this.jedis.hincrBy(KEY_EVENT_DAILY_CLICK_TOTAL, getToday(), 1);
		return this.jedis.hincrBy(KEY_EVENT_DAILY_CLICK + eventId, getToday(), 1);
	}
	
	/**
	 * 이벤트 페이지에 대한 모든 날짜별 방문횟수 조회
	 * @return 전체 이벤트 페이지 방문횟수
	 */
	public SortedMap<String, String> getVisitCountByDailyTotal() {
		// 전체 이벤트 페이지의 방문횟수를 정렬된 상태로 조회하기 위함
		SortedMap<String, String> result = new TreeMap<String, String>();
		
		// 조회된 날짜별 전체 이벤트 페이지 방문 횟수를 필드명으로 정렬하기 위해 SortedMap 객체에 저장
		result.putAll(this.jedis.hgetAll(KEY_EVENT_DAILY_CLICK_TOTAL));
		return result;
	}
	
	/**
	 * 요청 이벤트 페이지에 해당하는모든 날짜의 방문횟수 조회 
	 * @param eventId 이벤트 아이디
	 * @return 날짜로 정렬된 방문횟수 목록
	 */
	public SortedMap<String, String> getVisitCountByDilay(String eventId) {
		SortedMap<String, String> result = new TreeMap<String, String>();
		result.putAll(this.jedis.hgetAll(KEY_EVENT_DAILY_CLICK + eventId));
		return result;
	}
	
	private String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
}
