package net.sf.redis.ch7.visitcount;

import static org.junit.Assert.*;

import java.util.SortedMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * VisitCountOfDayV2 테스트 클래스
 * 
 * @author assu
 * @date 2016.04.16
 */
public class VisitCountOfDayV2Test {
	static JedisHelper helper;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();	// VisitCountOfDayV2 실행을 위한 제디스 헬퍼 객체 가져옴.
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
		
		VisitCountOfDayV2 visitCountOfDayV2 = new VisitCountOfDayV2(helper);
		
		// 날짜별 이벤트 페이지 방문횟수 증가
		assertTrue(visitCountOfDayV2.addVisit("52") > 0);
		assertTrue(visitCountOfDayV2.addVisit("180") > 0);
		assertTrue(visitCountOfDayV2.addVisit("554") > 0);
	}
	
	/**
	 * 방문횟수 조회 테스트
	 */
	@Test
	public void testGetVisitCountByDate() {
		VisitCountOfDayV2 visitCountOfDayV2 = new VisitCountOfDayV2(helper);
		
		// 이벤트 아이디 554번의 사방문횟수를 날짜별 목록으로 조회
		SortedMap<String, String> visitCount = visitCountOfDayV2.getVisitCountByDilay("554");
		
		assertTrue(visitCount.size() > 0);
		assertNotNull(visitCount);
		assertNotNull(visitCount.firstKey());	// 조회된 날짜별 목록에서 이벤트 시작일 가져오고 정상인지 테스트
		assertNotNull(visitCount.lastKey());
		
		System.out.println(visitCount);
		// {20160415=1, 20160416=1, 20160417=1}
		
		// 전체 이벤트에 대한 방뭇횟수를 날짜별로 조회
		SortedMap<String, String> totalVisit = visitCountOfDayV2.getVisitCountByDailyTotal();
		
		assertTrue(totalVisit.size() > 0);
		assertNotNull(totalVisit);
		assertNotNull(totalVisit.firstKey());
		assertNotNull(totalVisit.lastKey());
		
		System.out.println(totalVisit);
		// {20160415=3, 20160416=3, 20160417=3}
	}
}
