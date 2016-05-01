package net.sf.redis.ch7.uniquevisit;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * UniqueVisitV2 테스트 클래스
 * 
 * 천만명의 회원이 있다고 가정
 * 특정기간의 순방문자수 조회 테스트
 * 
 * @author assu
 * @date 2016.05.01
 */
public class UniqueVisitTestV2Test {
	static JedisHelper helper;
	
	private UniqueVisitV2 uniqueVisit;
	/** 테스트 대상이 되는 사용자번호의 최댓값 설정(천만명) */
	private static final int TOTAL_USER = 100000000;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool();
	}
	
	@Before
	public void setUp() {
		this.uniqueVisit = new UniqueVisitV2(helper);
		assertNotNull(this.uniqueVisit);
		
		this.uniqueVisit.visit(7, "20130510");
		this.uniqueVisit.visit(11, "20130510");
		this.uniqueVisit.visit(15, "20130510");
		this.uniqueVisit.visit(TOTAL_USER, "20130510");

		this.uniqueVisit.visit(3, "20130511");
		this.uniqueVisit.visit(7, "20130511");
		this.uniqueVisit.visit(9, "20130511");
		this.uniqueVisit.visit(11, "20130511");
		this.uniqueVisit.visit(15, "20130511");
		this.uniqueVisit.visit(TOTAL_USER, "20130511");

		this.uniqueVisit.visit(7, "20130512");
		this.uniqueVisit.visit(12, "20130512");
		this.uniqueVisit.visit(13, "20130512");
		this.uniqueVisit.visit(15, "20130512");
		this.uniqueVisit.visit(TOTAL_USER, "20130512");
	}
	
	/**
	 * 주어진 기간의 순방문자수조회 테스트
	 */
	@Test
	public void testUVSum() {
		String[] dateList1 = { "20130510", "20130511", "20130512" };
		assertEquals(new Long(3), this.uniqueVisit.getUVSum(dateList1));
		
		String[] dateList2 = { "20130510", "20130511", "20130512", "20110512" };
		assertEquals(new Long(0), this.uniqueVisit.getUVSum(dateList2));
	}
}
