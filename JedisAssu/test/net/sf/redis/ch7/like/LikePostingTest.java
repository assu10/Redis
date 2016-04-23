package net.sf.redis.ch7.like;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * LikePosting �׽�Ʈ Ŭ����
 * 
 * 20���� �Խù� ��ȣ�� ����� �迭 ���� �� �׽�Ʈ 
 * 
 * @author assu
 * @date 2016.04.23
 */
public class LikePostingTest {
	static JedisHelper helper;
	
	private LikePosting likePosting;
	private static Random rand = new Random();
	/** �Խù� ���� */
	private static final int POSTING_COUNT = 20;
	/** �׽�Ʈ�� ���� ������ ����ڹ�ȣ ���� */
	private static final int TESTUSER = rand.nextInt(10000000);
	/** �׽�Ʈ�� ����� �Խù� ��ȣ ���� */
	private static String[] POSTLIST = new String[POSTING_COUNT];
	
	/**
	 * �Խù� �迭�� �Խù���ȣ ����
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();
		// �Խù� ��ȣ�� 1~20���� �ʱ�ȭ
		for (int i=0; i<POSTLIST.length; i++) {
			POSTLIST[i] = String.valueOf(i+1);
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool();
	}
	
	@Before
	public void setUp() {
		this.likePosting = new LikePosting(helper);
		assertNotNull(this.likePosting);
	}
	
	/**
	 * ���ƿ� ��� �׽�Ʈ 
	 * 
	 * ���ƿ� ����� �׽�Ʈ�ϱ� ���� �׽�Ʈ ��� �����Ϳ� �̹� ���ƿ䰡 �Ǿ��ִ��� Ȯ�� ��
	 * ���ƿ䰡 �Ǿ��ִٸ� ����ϰ� ��� �׽�Ʈ
	 */
	@Test
	public void testLike() {
		// 1~20������ ������ �Խù� ��ȣ ������
		String postingNo = String.valueOf(LikePostingTest.rand.nextInt(POSTING_COUNT));
		
		// ���ƿ� ����� �׽�Ʈ�ϱ� ���� �׽�Ʈ ��� �����Ϳ� �̹� ���ƿ䰡 �Ǿ��ִ��� Ȯ�� �� 
		// ���ƿ䰡 �Ǿ��ִٸ� �����
		if (this.likePosting.isLiked(postingNo, String.valueOf(TESTUSER))) {
			this.likePosting.unLike(postingNo, String.valueOf(TESTUSER));
		}
		// ���ƿ� ��� �׽�Ʈ
		assertTrue(this.likePosting.like(postingNo, String.valueOf(TESTUSER)));
	}
	
	/**
	 * ���ƿ� ��� ��� �׽�Ʈ
	 */
	@Test
	public void testUnLike() {
		String postingNo = String.valueOf(LikePostingTest.rand.nextInt(POSTING_COUNT));
		
		// ���ƿ䰡 �Ǿ����� ������ ���ƿ� ��� ���� �� ��� ��� �׽�Ʈ 
		if (this.likePosting.isLiked(postingNo, String.valueOf(TESTUSER))) {
			assertTrue(this.likePosting.unLike(postingNo, String.valueOf(TESTUSER)));
		} else {
			assertTrue(this.likePosting.like(postingNo, String.valueOf(TESTUSER)));
			assertTrue(this.likePosting.unLike(postingNo, String.valueOf(TESTUSER)));
		}
	}
	
	/**
	 * Ư�� �Խù��� ���ƿ� Ƚ�� ��ȸ ��� �׽�Ʈ
	 */
	@Test
	public void testGetLikeCount() {
		String postingNo = String.valueOf(LikePostingTest.rand.nextInt(POSTING_COUNT));
		
		// Ƚ������ �׽�Ʈ�� ���� �̹� ���ƿ䰡 �Ǿ������� ���ƿ� ��ұ�� ����
		if (this.likePosting.isLiked(postingNo, String.valueOf(TESTUSER))) {
			assertTrue(this.likePosting.unLike(postingNo, String.valueOf(TESTUSER)));
		}
		
		// �׽�Ʈ ��� �Խù��� ���ƿ� Ƚ�� ��ȸ
		Long prevCount = this.likePosting.getLikeCount(postingNo);
		this.likePosting.like(postingNo, String.valueOf(TESTUSER));
		
		// �׽�Ʈ ��� �Խù��� ���ƿ� ó�� �� Ƚ���� 1��ŭ �����ߴ��� Ȯ��
		System.out.println(postingNo + " - " + this.likePosting.getLikeCount(postingNo));
		assertEquals(this.likePosting.getLikeCount(postingNo), new Long(prevCount + 1));
	}
	
	/**
	 * �Խù� ����� ���ƿ� Ƚ�� ��ȸ ��� �׽�Ʈ
	 */
	@Test
	public void testGetLikeCountList() {
		List<Long> countList = this.likePosting.getLikeCountList(POSTLIST);
		System.out.println(this.likePosting.getLikeCountList(POSTLIST));
		assertEquals(countList.size(), POSTING_COUNT);
	}
	
	/**
	 * Ư�� �Խù��� Ʋ�� ����ڰ� ���ƿ� �ߴ��� ���� �׽�Ʈ
	 */
	@Test
	public void testIsLiked() {
		String postingNo = String.valueOf(LikePostingTest.rand.nextInt(POSTING_COUNT));
		this.likePosting.like(postingNo, String.valueOf(TESTUSER));
		assertTrue(this.likePosting.isLiked(postingNo, String.valueOf(TESTUSER)));
	}
	
	/**
	 * �Խù��� ���� ���ƿ� ���� ���� ��� �׽�Ʈ
	 */
	@Test
	public void testDeleteLikeInfo() {
		String postingNo = "1324567890";	// �������� �ʴ� �Խù���ȣ ����
		this.likePosting.like(postingNo, String.valueOf(TESTUSER));
		
		// �������� �ʴ� �Խù� ��ȣ�� ���Ͽ� ���ƿ� ó�� �� ���ƿ� ������ ����� ������ ����
		assertTrue(this.likePosting.deleteLikeInfo(postingNo));
	}
	
	@Test
	public void testRandomLike() {
		// 20���� ������ ����� ��ȣ �����Ͽ� 20���� �Խù��� ���ƿ� ǥ��
		for (int i=1; i<POSTING_COUNT+1; i++) {
			String sudoRandomUser = String.valueOf(rand.nextInt(10000000));
			this.likePosting.like(String.valueOf(i), sudoRandomUser);
		}
	}
}
