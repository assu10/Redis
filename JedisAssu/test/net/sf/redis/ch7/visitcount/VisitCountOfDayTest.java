package net.sf.redis.ch7.visitcount;

import static org.junit.Assert.*;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * VisitCount (전체 누적방문횟수 저장/조회용) 과 VisitCountOfDay (날짜별 누적방문자수 저장/조회용) 테스트 클래스
 * 
 * @author assu
 * @date 2016.04.16
 */
public class VisitCountOfDayTest {
	static JedisHelper helper;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();		// VisitCount, VisitCountOfDay 실행을 위한 제디스 헬퍼 객체 가져옴.
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool();
	}
	
	/**
	 * 방문횟수 증가 테스트
	 */
	@Test
	public void testAddVisit() {
		VisitCount visitCount = new VisitCount(helper);
		
		// 이벤트 페이지의 전체 방문 횟수 증가
		assertTrue(visitCount.addVisit("52") > 0);
		assertTrue(visitCount.addVisit("180") > 0);
		assertTrue(visitCount.addVisit("554") > 0);
		
		VisitCountOfDay visitCountOfDay = new VisitCountOfDay(helper);
		
		// 날짜별 이벤트 페이지 방문횟수 증가
		assertTrue(visitCountOfDay.addVisit("52") > 0);
		assertTrue(visitCountOfDay.addVisit("180") > 0);
		assertTrue(visitCountOfDay.addVisit("554") > 0);
	}
	
	/**
	 * 방문횟수 조회 테스트
	 */
	@Test
	public void testGetVisitCountByDate() {
		String[] dateList = {"20160415", "20160416", "20160417", "20160418"};
		VisitCountOfDay visitCountOfDay = new VisitCountOfDay(helper);
		
		// 52 이벤트에 대해 20160420~20160423 방문횟수 조회
		List<String> result = visitCountOfDay.getVisitCountByDate("52", dateList);
		
		assertNotNull(result);
		
		System.out.println("★result : " + result);
		assertTrue(result.size() == 4);
	}
}
