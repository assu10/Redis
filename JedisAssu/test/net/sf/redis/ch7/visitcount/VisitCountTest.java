package net.sf.redis.ch7.visitcount;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * VisitCount 테스트 클래스
 * 
 * @author assu
 * @date 2016.04.16
 */
public class VisitCountTest {
	static JedisHelper helper;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();	// VisitCount 실행을 위한 제디스 헬퍼 객체 가져옴.
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool();		// 제디스 연결풀 제거
	}
	
	/**
	 * 방문횟수 증가 테스트
	 */
	@Test
	public void testAddVisit() {
		VisitCount visitCount = new VisitCount(helper);
		assertNotNull(visitCount);	// 생성된 객체가 정상인지 확인
		
		// 증가시키고 양수면 정상으로 간주
		assertTrue(visitCount.addVisit("52") > 0);
		assertTrue(visitCount.addVisit("180") > 0);
		assertTrue(visitCount.addVisit("554") > 0);
	}
	
	/**
	 * 방문횟수 조회 테스트
	 */
	@Test
	public void testGetVisitCount() {
		VisitCount visitCount = new VisitCount(helper);
		assertNotNull(visitCount);
		
		List<String> result = visitCount.getVisitCount("52", "180", "554");
		assertNotNull(result);
		assertTrue(result.size() == 3);	// 3개의 이벤트 아이디에 대한 결과가 조회되었는지 확인
		
		long sum = 0;
		for (String count : result) {
			sum += Long.parseLong(count);
		}
		String totalCount = visitCount.getVisitTotalCount();
		
		// 전체 이벤트 방문횟수와 각 이벤트 방문횟수의 합이 같은지 확인
		assertEquals(String.valueOf(sum), totalCount);	
	}
}
