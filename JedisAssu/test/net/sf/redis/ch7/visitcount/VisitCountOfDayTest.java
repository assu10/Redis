package net.sf.redis.ch7.visitcount;

import static org.junit.Assert.*;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * VisitCount (��ü �����湮Ƚ�� ����/��ȸ��) �� VisitCountOfDay (��¥�� �����湮�ڼ� ����/��ȸ��) �׽�Ʈ Ŭ����
 * 
 * @author assu
 * @date 2016.04.16
 */
public class VisitCountOfDayTest {
	static JedisHelper helper;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();		// VisitCount, VisitCountOfDay ������ ���� ���� ���� ��ü ������.
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
		
		VisitCountOfDay visitCountOfDay = new VisitCountOfDay(helper);
		
		// ��¥�� �̺�Ʈ ������ �湮Ƚ�� ����
		assertTrue(visitCountOfDay.addVisit("52") > 0);
		assertTrue(visitCountOfDay.addVisit("180") > 0);
		assertTrue(visitCountOfDay.addVisit("554") > 0);
	}
	
	/**
	 * �湮Ƚ�� ��ȸ �׽�Ʈ
	 */
	@Test
	public void testGetVisitCountByDate() {
		String[] dateList = {"20160415", "20160416", "20160417", "20160418"};
		VisitCountOfDay visitCountOfDay = new VisitCountOfDay(helper);
		
		// 52 �̺�Ʈ�� ���� 20160420~20160423 �湮Ƚ�� ��ȸ
		List<String> result = visitCountOfDay.getVisitCountByDate("52", dateList);
		
		assertNotNull(result);
		
		System.out.println("��result : " + result);
		assertTrue(result.size() == 4);
	}
}
