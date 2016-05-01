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
 * UniqueVisit �׽�Ʈ Ŭ����
 * 
 * õ������ ȸ���� �ִٰ� �����ϰ�, 
 * ���������� ���¿� ���� ���¿� ���� �׽�Ʈ 
 * 
 * @author assu
 * @date 2016.04.23
 */
public class UniqueVisitTest {
	static JedisHelper helper;
	private UniqueVisit uniqueVisit;
	/** ������ �湮�׽�Ʈ Ƚ�� ���� */
	private static final int VISIT_COUNT = 1000; 
	/** �׽�Ʈ ����� �Ǵ� ����ڹ�ȣ�� �ִ� ����(õ����) */
	private static final int TOTAL_USER = 10000000;
	/** �����Ͱ� �������� �ʴ� ������ ��¥ ����*/
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
	 * �����湮�ڼ� ��ȸ �׽�Ʈ
	 */
	@Test
	public void testRandomPV() {
		int pv = this.uniqueVisit.getPVCount(getToday());
		
		System.out.println("testRandomPV pv - " + pv);
		
		// ������ ����ڿ� ���� 1000���� �湮 �ùķ��̼�
		for (int i=0; i<VISIT_COUNT; i++) {
			this.uniqueVisit.visit(rand.nextInt(TOTAL_USER));
		}
		
		System.out.println(this.uniqueVisit.getPVCount(getToday()));
		
		// �����湮Ƚ���� 1000�� �����ߴ��� Ȯ��
		assertEquals(pv + VISIT_COUNT, this.uniqueVisit.getPVCount(getToday()));
	}
	
	/**
	 * �����Ͱ� �������� �ʴ� ��¥�� �湮Ƚ���� 0���� Ȯ��
	 */
	@Test
	public void testInvalidPV() {
		assertEquals(0, this.uniqueVisit.getPVCount(TEST_DATE));
		assertEquals(new Long(0), this.uniqueVisit.getUVCount(TEST_DATE));
	}
	
	/**
	 * �� ���� �湮���� �� �����湮Ƚ���� 1�� �����ߴ��� Ȯ��
	 */
	@Test
	public void testPV() {
		int result = this.uniqueVisit.getPVCount(getToday());	// ���� ��¥�� �����湮Ƚ��
		this.uniqueVisit.visit(65487);
		
		assertEquals(result + 1, this.uniqueVisit.getPVCount(getToday()));
	}
	
	/**
	 * ���� ����ڰ� �� �� ȣ��Ǿ ���湮Ƚ���� �� ���� �����ϴ��� Ȯ��
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
