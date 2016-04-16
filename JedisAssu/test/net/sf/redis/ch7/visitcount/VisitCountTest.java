package net.sf.redis.ch7.visitcount;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * VisitCount �׽�Ʈ Ŭ����
 * 
 * @author assu
 * @date 2016.04.16
 */
public class VisitCountTest {
	static JedisHelper helper;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();	// VisitCount ������ ���� ���� ���� ��ü ������.
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool();		// ���� ����Ǯ ����
	}
	
	/**
	 * �湮Ƚ�� ���� �׽�Ʈ
	 */
	@Test
	public void testAddVisit() {
		VisitCount visitCount = new VisitCount(helper);
		assertNotNull(visitCount);	// ������ ��ü�� �������� Ȯ��
		
		// ������Ű�� ����� �������� ����
		assertTrue(visitCount.addVisit("52") > 0);
		assertTrue(visitCount.addVisit("180") > 0);
		assertTrue(visitCount.addVisit("554") > 0);
	}
	
	/**
	 * �湮Ƚ�� ��ȸ �׽�Ʈ
	 */
	@Test
	public void testGetVisitCount() {
		VisitCount visitCount = new VisitCount(helper);
		assertNotNull(visitCount);
		
		List<String> result = visitCount.getVisitCount("52", "180", "554");
		assertNotNull(result);
		assertTrue(result.size() == 3);	// 3���� �̺�Ʈ ���̵� ���� ����� ��ȸ�Ǿ����� Ȯ��
		
		long sum = 0;
		for (String count : result) {
			sum += Long.parseLong(count);
		}
		String totalCount = visitCount.getVisitTotalCount();
		
		// ��ü �̺�Ʈ �湮Ƚ���� �� �̺�Ʈ �湮Ƚ���� ���� ������ Ȯ��
		assertEquals(String.valueOf(sum), totalCount);	
	}
}
