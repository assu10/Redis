package net.sf.redis.ch7.like;

import java.util.ArrayList;
import java.util.List;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * 좋아요 기능 구현(Set)
 *  
 * (한 명의 사용자는 하나의 게시물에 한번만 좋아요 표시, 반대로 좋아요 취소도 가능,
 *  각 게시물에는 좋아요 횟수정보 출력, 게시물이 삭제되면 게시물에 대한 좋아요 정보도 같이 삭제
 *  게시물 목록에서 각 게시물에 대한 좋아요 횟수 표시)
 *  
 *  - 게시물에 대한 좋아요 표시
 *  - 좋아요 표시 취소
 *  - 사용자의 좋아요 표시 여부 확인
 *  - 좋아요 횟수 정보 삭제
 *  - 게시물의 좋아요 횟수 조회
 *  - 게시물 목록의 좋아요 횟수 조회
 * 
 * Hash : key - field:value
 *              field:value
 * Set  : key - value
 *              value
 *              
 * @author assu
 * @2016.04.23
 */
public class LikePosting {
	private Jedis jedis;
	
	/** 게시물 12345의 좋아요정보 (posting:like:12345) */
	private static final String KEY_LIKE_SET = "posting:like:";
	
	public LikePosting(JedisHelper helper) {
		this.jedis = helper.getConnection();
	}
	
	/**
	 * 지정된 사용자가 게시물에 좋아요 표시
	 * @param postingNo 게시물 번호
	 * @param userNo 사용자 번호
	 * @return true: 정상처리
	 */
	public boolean like(String postingNo, String userNo) {
		return this.jedis.sadd(KEY_LIKE_SET + postingNo, userNo) > 0;
	}
	
	/**
	 * 지정된 사용자가 게시물의 좋아요 취소
	 * @param postingNo 게시물 번호
	 * @param userNo 사용자 번호
	 * @return true: 정상처리
	 */
	public boolean unLike(String postingNo, String userNo) {
		return this.jedis.srem(KEY_LIKE_SET + postingNo, userNo) > 0;
	}
	
	/**
	 * 지정된 사용자의 좋아요 표시 여부 확인
	 * @param postingNo 게시물 번호
	 * @param userNo 사용자 번호
	 * @return true: 좋아요 처리됨
	 */
	public boolean isLiked(String postingNo, String userNo) {
		return this.jedis.sismember(KEY_LIKE_SET + postingNo, userNo);
	}
	
	/**
	 * 게시물에 대한 좋아요 정보 삭제
	 * @param postingNo
	 * @return
	 */
	public boolean deleteLikeInfo(String postingNo) {
		return this.jedis.del(KEY_LIKE_SET + postingNo) > 0;
	}
	
	/**
	 * 게시물의 좋아요 횟수 조회
	 * @param postingNo 게시물 번호
	 * @return 좋아요 횟수
	 */
	public Long getLikeCount(String postingNo) {
		return this.jedis.scard(KEY_LIKE_SET + postingNo);
	}
	
	/**
	 * 주어진 게시물 목록의 좋아요 횟수 조회
	 * @param postingList 조회대상 게시물 목록
	 * @return 좋아요 횟수 목록
	 */
	public List<Long> getLikeCountList(String[] postingList) {
		List<Long> result = new ArrayList<Long>();
		
		Pipeline p = this.jedis.pipelined();
		for (String postingNo : postingList) {
			p.scard(KEY_LIKE_SET + postingNo);
		}
		List<Object> pipelineResult = p.syncAndReturnAll();
		
		for (Object item : pipelineResult) {
			result.add((Long) item);
		}
		System.out.println("주어진 게시물 목록의 좋아요 횟수 조회 getLikeCountList - " + result);
		return result;
	}
}
