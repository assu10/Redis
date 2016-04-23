package net.sf.redis.ch7.uniquevisit;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * 사이트 순방문자수와 누적방문자수 저장 및 조회(setBit)
 * 
 * 각 사용자는 유일한 사용자번호를 갖고 있으며 방문 횟수는 순수방문횟수와 누적방문횟수로 나뉨.
 * 일일방문횟수는 한 명의 사용자당 한번의 방문 횟수만 기록
 * 누적방문횟수는 한 명의 사용자가 여러번 방문하면 방문한 횟수만큼 기록
 * 저장된 순방문횟수와 누적방문횟수는 즉시 조회가 가능해야 함.
 * 
 * - 사용자가 접속한 날짜의 누적방문횟수 저장
 * - 사용자가 접속한 날짜의 순방문횟수 저장
 * - 지정된 날짜의 누적방문횟수 조회
 * - 지정된 날짜의 순방문횟수 조회
 * 
 * setbit(key, offset, value) - 숫자응답(이전에 저장되었던 비트값)
 * 
 * @author assu
 * @date 2016.04.23
 */
public class UniqueVisit {
	private Jedis jedis;
	/** 누적방문자수(page:view:20160423) */
	private static final String KEY_PAGE_VIEW = "page:view:";
	/** 순방문자수(unique:visitors:20160423) */
	private static final String KEY_UNIQUE_VISITOR = "unique:visitors:";
	
	public UniqueVisit(JedisHelper helper) {
		this.jedis = helper.getConnection();
	}
	
	/**
	 * 특정 사용자의 순방문횟수와 누적방문횟수 증가
	 * @param userNo 사용자번호
	 */
	public void visit(int userNo) {
		// 2개의 레디스 명령을 한번에 전송하기 위해 파이프라인 사용
		Pipeline p = this.jedis.pipelined();
		p.incr(KEY_PAGE_VIEW + getToday());
		
		// 사용자번호에 해당하는 오프셋 위치의 비트값을 1로 설정
		p.setbit(KEY_UNIQUE_VISITOR + getToday(), userNo, true);
		p.sync();
	}
	
	/**
	 * 요청된 날짜의 누적방문자수(PageView) 조회
	 * @param date 조회대상 날짜
	 * @return 누적방문자수
	 */
	public int getPVCount(String date) {
		int result = 0;
		try {
			result = Integer.parseInt(jedis.get(KEY_PAGE_VIEW + date));
		} catch (Exception e) {
			result = 0;
		}
		System.out.println("요청된 날짜의 누적방문자수(PageView) 조회, getPVCount - " + result);
		return result;
	}
	
	/**
	 * 요청된 날짜의 순방문자수(UniqueView) 조회
	 * @param date 조회대상 날짜
	 * @return 순방문자수
	 * @deprecated bitcount는 2.6.0부터 사용가능한데 나는 2.4.5라 테스트 불가
	 */
	public Long getUVCount(String date) {
		// bitcount(key, [start index],[end index]) - 숫자응답(지정된 범위에 저장된 비트 중 1의 갯수)
		// 2.6.0부터 사용가능한데 나는 2.4.5라 테스트 불가
		return jedis.bitcount(KEY_UNIQUE_VISITOR + date);
	}
	
	private String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
}
