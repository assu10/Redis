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
 * LikePosting 테스트 클래스
 * 
 * 20개의 게시물 번호가 저장된 배열 생성 후 테스트 
 * 
 * @author assu
 * @date 2016.04.23
 */
public class LikePostingTest {
	static JedisHelper helper;
	
	private LikePosting likePosting;
	private static Random rand = new Random();
	/** 게시물 갯수 */
	private static final int POSTING_COUNT = 20;
	/** 테스트를 위한 임의의 사용자번호 생성 */
	private static final int TESTUSER = rand.nextInt(10000000);
	/** 테스트에 사용할 게시물 번호 선언 */
	private static String[] POSTLIST = new String[POSTING_COUNT];
	
	/**
	 * 게시물 배열에 게시물번호 셋팅
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();
		// 게시물 번호를 1~20까지 초기화
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
	 * 좋아요 기능 테스트 
	 * 
	 * 좋아요 기능을 테스트하기 위해 테스트 대상 데이터에 이미 좋아요가 되어있는지 확인 후
	 * 좋아요가 되어있다면 취소하고 기능 테스트
	 */
	@Test
	public void testLike() {
		// 1~20사이의 임의의 게시물 번호 가져옴
		String postingNo = String.valueOf(LikePostingTest.rand.nextInt(POSTING_COUNT));
		
		// 좋아요 기능을 테스트하기 위해 테스트 대상 데이터에 이미 좋아요가 되어있는지 확인 후 
		// 좋아요가 되어있다면 취소함
		if (this.likePosting.isLiked(postingNo, String.valueOf(TESTUSER))) {
			this.likePosting.unLike(postingNo, String.valueOf(TESTUSER));
		}
		// 좋아요 기능 테스트
		assertTrue(this.likePosting.like(postingNo, String.valueOf(TESTUSER)));
	}
	
	/**
	 * 좋아요 취소 기능 테스트
	 */
	@Test
	public void testUnLike() {
		String postingNo = String.valueOf(LikePostingTest.rand.nextInt(POSTING_COUNT));
		
		// 좋아요가 되어있지 않으면 좋아요 기능 실행 후 취소 기능 테스트 
		if (this.likePosting.isLiked(postingNo, String.valueOf(TESTUSER))) {
			assertTrue(this.likePosting.unLike(postingNo, String.valueOf(TESTUSER)));
		} else {
			assertTrue(this.likePosting.like(postingNo, String.valueOf(TESTUSER)));
			assertTrue(this.likePosting.unLike(postingNo, String.valueOf(TESTUSER)));
		}
	}
	
	/**
	 * 특정 게시물의 좋아요 횟수 조회 기능 테스트
	 */
	@Test
	public void testGetLikeCount() {
		String postingNo = String.valueOf(LikePostingTest.rand.nextInt(POSTING_COUNT));
		
		// 횟수증가 테스트를 위해 이미 좋아요가 되어있으면 좋아요 취소기능 실행
		if (this.likePosting.isLiked(postingNo, String.valueOf(TESTUSER))) {
			assertTrue(this.likePosting.unLike(postingNo, String.valueOf(TESTUSER)));
		}
		
		// 테스트 대상 게시물의 좋아요 횟수 조회
		Long prevCount = this.likePosting.getLikeCount(postingNo);
		this.likePosting.like(postingNo, String.valueOf(TESTUSER));
		
		// 테스트 대상 게시물의 좋아요 처리 후 횟수가 1만큼 증가했는지 확인
		System.out.println(postingNo + " - " + this.likePosting.getLikeCount(postingNo));
		assertEquals(this.likePosting.getLikeCount(postingNo), new Long(prevCount + 1));
	}
	
	/**
	 * 게시물 목록의 좋아요 횟수 조회 기능 테스트
	 */
	@Test
	public void testGetLikeCountList() {
		List<Long> countList = this.likePosting.getLikeCountList(POSTLIST);
		System.out.println(this.likePosting.getLikeCountList(POSTLIST));
		assertEquals(countList.size(), POSTING_COUNT);
	}
	
	/**
	 * 특정 게시물에 틀정 사용자가 좋아요 했는지 여부 테스트
	 */
	@Test
	public void testIsLiked() {
		String postingNo = String.valueOf(LikePostingTest.rand.nextInt(POSTING_COUNT));
		this.likePosting.like(postingNo, String.valueOf(TESTUSER));
		assertTrue(this.likePosting.isLiked(postingNo, String.valueOf(TESTUSER)));
	}
	
	/**
	 * 게시물에 대한 좋아요 정보 삭제 기능 테스트
	 */
	@Test
	public void testDeleteLikeInfo() {
		String postingNo = "1324567890";	// 존재하지 않는 게시물번호 생성
		this.likePosting.like(postingNo, String.valueOf(TESTUSER));
		
		// 존재하지 않는 게시물 번호에 대하여 좋아요 처리 후 좋아요 정보가 저장된 데이터 삭제
		assertTrue(this.likePosting.deleteLikeInfo(postingNo));
	}
	
	@Test
	public void testRandomLike() {
		// 20명의 임의의 사용자 번호 생성하여 20개의 게시물에 좋아요 표시
		for (int i=1; i<POSTING_COUNT+1; i++) {
			String sudoRandomUser = String.valueOf(rand.nextInt(10000000));
			this.likePosting.like(String.valueOf(i), sudoRandomUser);
		}
	}
}
