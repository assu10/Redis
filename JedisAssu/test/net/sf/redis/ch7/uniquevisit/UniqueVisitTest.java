package net.sf.redis.ch7.uniquevisit;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * UniqueVisit 테스트 클래스
 * 
 * 천망명의 회원이 있다고 가정하고, 
 * 비정상적인 상태와 정상 상태에 대한 테스트 
 * 
 * @author assu
 * @date 2016.04.23
 */
public class UniqueVisitTest {
	static JedisHelper helper;
	private UniqueVisit uniqueVisit;
	/** 임의의 방문테스트 횟수 지정 */
	private static final int VISIT_COUNT = 1000; 
	/** 테스트 대상이 되는 사용자번호의 최댓값 설정(천만명) */
	private static final int TOTAL_USER = 10000000;
	/** 데이터가 존재하지 않는 임의의 날짜 지정*/
	private static final String TEST_DATE = "19500101";
	static Random rand = new Random();
	
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
		this.uniqueVisit = new UniqueVisit(helper);
		assertNotNull(this.uniqueVisit);
	}
	
	/**
	 * 누적방문자수 조회 테스트
	 */
	@Test
	public void testRandomPV() {
		int pv = this.uniqueVisit.getPVCount(getToday());
		
		System.out.println("testRandomPV pv - " + pv);
		
		// 임의의 사용자에 대한 1000번의 방문 시뮬레이션
		for (int i=0; i<VISIT_COUNT; i++) {
			this.uniqueVisit.visit(rand.nextInt(TOTAL_USER));
		}
		
		System.out.println(this.uniqueVisit.getPVCount(getToday()));
		
		// 누적방문횟수가 1000번 증가했는지 확인
		assertEquals(pv + VISIT_COUNT, this.uniqueVisit.getPVCount(getToday()));
	}
	
	/**
	 * 데이터가 존재하지 않는 날짜의 방문횟수가 0인지 확인
	 */
	@Test
	public void testInvalidPV() {
		assertEquals(0, this.uniqueVisit.getPVCount(TEST_DATE));
		assertEquals(new Long(0), this.uniqueVisit.getUVCount(TEST_DATE));
	}
	
	/**
	 * 한 명이 방문했을 때 누적방문횟수가 1일 증가했는지 확인
	 */
	@Test
	public void testPV() {
		int result = this.uniqueVisit.getPVCount(getToday());	// 오늘 날짜의 누적방문횟수
		this.uniqueVisit.visit(65487);
		
		assertEquals(result + 1, this.uniqueVisit.getPVCount(getToday()));
	}
	
	/**
	 * 동일 사용자가 두 번 호출되어도 순방문횟수가 한 번만 증가하는지 확인
	 */
	@Test
	public void testUV() {
		this.uniqueVisit.visit(65488);
		Long result = this.uniqueVisit.getUVCount(getToday());
		this.uniqueVisit.visit(65488);
		
		assertEquals(result, this.uniqueVisit.getUVCount(getToday()));
	}
	
	private String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
}
