package net.sf.redis.ch7.uniquevisit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * EventUserList 테스트 클래스
 * 
 * 루아스크립트 실행
 * 주간 순 방문자를 임의로 생성한 후 일주일동안 풀로그인한 사용자 조회
 * 
 * @author assu
 * @date 2016.05.05
 */
public class EventUserListTest {
	static JedisHelper helper;
	private static EventUserList eventUserList;
	private static UniqueVisitV2 uniqueVisitV2;
	private static final int TOTAL_USER = 100000000;
	private static final int DEST_USER = 10000;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();
		EventUserListTest.eventUserList = new EventUserList(helper);
		uniqueVisitV2 = new UniqueVisitV2(helper);
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool();
	}
	
	@Before
	public void setUp() {
		assertNotNull(EventUserListTest.eventUserList);
	}
	
	/**
	 * 주간 순 방문자를 임의로 생성한 후 일주일동안 풀로그인하도록 만듬
	 */
	@Test
	public void testSetUp() {
		Random rand = new Random();
		for (int i=0; i<DEST_USER; i++) {
			int tempuser= rand.nextInt(TOTAL_USER);
			EventUserListTest.uniqueVisitV2.visit(tempuser, "20110508");
			EventUserListTest.uniqueVisitV2.visit(tempuser, "20110509");
			EventUserListTest.uniqueVisitV2.visit(tempuser, "20110510");
			EventUserListTest.uniqueVisitV2.visit(tempuser, "20110511");
			EventUserListTest.uniqueVisitV2.visit(tempuser, "20110512");
			EventUserListTest.uniqueVisitV2.visit(tempuser, "20110513");
			EventUserListTest.uniqueVisitV2.visit(tempuser, "20110514");
		}
	}
	
	/**
	 * 루아스크립트 초기화
	 */
	@Test
	public void initLuaScript() {
		assertEquals("0a978c7b189edad3abc0fdd9b9fac03b15b77a4b", eventUserList.initLuaScript());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void runLuaScript() {
		String[] dateList1 = { "20110508", "20110509", "20110510", "20110511", "20110512", "20110513", "20110514" };
		
		// 주어진 기간의 순방문자수를 계산
		Long count = EventUserListTest.uniqueVisitV2.getUVSum(dateList1);
		
		// 레디스 서버에 등록된 루아 스크립트 실행
		String sha = eventUserList.initLuaScript();
		List<String> list = (List<String>)EventUserListTest.eventUserList.getEventUserList(sha);
		
		assertEquals(count, new Long(list.size()));
		
		for (String item : list) {
			System.out.println(item);
		}
		
		System.out.println("count - " + count);
		
	}
}
