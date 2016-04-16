package net.sf.redis.ch7.visitcount;

import static org.junit.Assert.*;

import java.util.SortedMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * VisitCountOfDayV2 �׽�Ʈ Ŭ����
 * 
 * @author assu
 * @date 2016.04.16
 */
public class VisitCountOfDayV2Test {
	static JedisHelper helper;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();	// VisitCountOfDayV2 ������ ���� ���� ���� ��ü ������.
	}
	
	@AfterClass
	public static void tearDownAfterClass() { 
		helper.destroyPool();
	}
	
	/**
	 * �湮Ƚ�� ���� �׽�Ʈ
	 */
	@Test
	public void testAddVisit() {
		VisitCount visitCount = new VisitCount(helper);
		
		// �̺�Ʈ �������� ��ü �湮 Ƚ�� ����
		assertTrue(visitCount.addVisit("52") > 0);
		assertTrue(visitCount.addVisit("180") > 0);
		assertTrue(visitCount.addVisit("554") > 0);
		
		VisitCountOfDayV2 visitCountOfDayV2 = new VisitCountOfDayV2(helper);
		
		// ��¥�� �̺�Ʈ ������ �湮Ƚ�� ����
		assertTrue(visitCountOfDayV2.addVisit("52") > 0);
		assertTrue(visitCountOfDayV2.addVisit("180") > 0);
		assertTrue(visitCountOfDayV2.addVisit("554") > 0);
	}
	
	/**
	 * �湮Ƚ�� ��ȸ �׽�Ʈ
	 */
	@Test
	public void testGetVisitCountByDate() {
		VisitCountOfDayV2 visitCountOfDayV2 = new VisitCountOfDayV2(helper);
		
		// �̺�Ʈ ���̵� 554���� ��湮Ƚ���� ��¥�� ������� ��ȸ
		SortedMap<String, String> visitCount = visitCountOfDayV2.getVisitCountByDilay("554");
		
		assertTrue(visitCount.size() > 0);
		assertNotNull(visitCount);
		assertNotNull(visitCount.firstKey());	// ��ȸ�� ��¥�� ��Ͽ��� �̺�Ʈ ������ �������� �������� �׽�Ʈ
		assertNotNull(visitCount.lastKey());
		
		System.out.println(visitCount);
		// {20160415=1, 20160416=1, 20160417=1}
		
		// ��ü �̺�Ʈ�� ���� �湵Ƚ���� ��¥���� ��ȸ
		SortedMap<String, String> totalVisit = visitCountOfDayV2.getVisitCountByDailyTotal();
		
		assertTrue(totalVisit.size() > 0);
		assertNotNull(totalVisit);
		assertNotNull(totalVisit.firstKey());
		assertNotNull(totalVisit.lastKey());
		
		System.out.println(totalVisit);
		// {20160415=3, 20160416=3, 20160417=3}
	}
}
