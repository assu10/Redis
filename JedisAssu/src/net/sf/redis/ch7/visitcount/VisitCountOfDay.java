package net.sf.redis.ch7.visitcount;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * 이벤트 아이디를 인자로 받아서 방문 횟수 증가시키고 조회
 *  + 날짜별 방문횟수와 날짜랑 전체 이벤트 페이지 방문횟수 조회
 *  --> 특정 이벤트가 언제 시작했는지에 대한 정보가 존재하지 않기 때문에 시작날짜와 종료날짜 조회 불가능
 *  
 * 날짜별 이벤트 페이지 방문횟수 저장
 * 날짜별 모든 페이지 방문횟수 조회
 * 날짜별 개별 이벤트 페이지 방문횟수 조회
 * 
 * @author assu
 * @date 2016.04.16
 */
public class VisitCountOfDay {
	private JedisHelper jedisHelper;
	private Jedis jedis;
	private static final String KEY_EVENT_CLICK_DAILY_TOTAL = "event:click:daily:total:";
	private static final String KEY_EVENT_CLICK_DAILY = "event:click:daily:";
	
	/**
	 * 날짜별 방문횟수 처리를 위한 클래스 생성자
	 * @param jedisHelper 제디스 헬퍼 객체
	 */
	public VisitCountOfDay(JedisHelper jedisHelper) {
		this.jedisHelper = jedisHelper;
		this.jedis= this.jedisHelper.getConnection();
	}
	
	/**
	 * 이벤트 아이디에 해당하는 날짜의 방문횟수와 날짜별 전체 방문횟수 증가
	 * @param eventId 이벤트 아이디
	 * @return 이벤트 페이지 방문횟수
	 */
	public Long addVisit(String eventId) {
		System.out.println("addVisit() : " + eventId + " - " + this.jedis.get(KEY_EVENT_CLICK_DAILY + getToday() + ":" + eventId));
		
		this.jedis.incr(KEY_EVENT_CLICK_DAILY_TOTAL + getToday());
		return this.jedis.incr(KEY_EVENT_CLICK_DAILY + getToday() + ":" + eventId);
	}
	
	/**
	 * 요청된 날짜에 해당간의 전체 이벤트 페이지 방문횟수 조회
	 * @return 전체 이벤트 페이지 방문횟수
	 */
	public String getVisitTotal(String date) {
		return this.jedis.get(KEY_EVENT_CLICK_DAILY_TOTAL + date);
	}
	
	/**
	 * 이벤트 아이디에 해당하는 요청된 날짜들의 방문횟수 조회
	 * @param eventId 요청된 이벤트 아이디
	 * @param dateList 요청날짜 목록
	 * @return 날짜 목록에 대한 방문횟수 목록
	 */
	public List<String> getVisitCountByDate(String eventId, String[] dateList) {
		List<String> result = new ArrayList<String>();
		for(int i=0; i<dateList.length; i++) {
			result.add(jedis.get(KEY_EVENT_CLICK_DAILY + dateList[i] + ":" + eventId));
		}
		return result;
	}
	
	private String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
}
