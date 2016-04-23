package net.sf.redis.ch7.like;

import java.util.ArrayList;
import java.util.List;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * ���ƿ� ��� ����(Set)
 *  
 * (�� ���� ����ڴ� �ϳ��� �Խù��� �ѹ��� ���ƿ� ǥ��, �ݴ�� ���ƿ� ��ҵ� ����,
 *  �� �Խù����� ���ƿ� Ƚ������ ���, �Խù��� �����Ǹ� �Խù��� ���� ���ƿ� ������ ���� ����
 *  �Խù� ��Ͽ��� �� �Խù��� ���� ���ƿ� Ƚ�� ǥ��)
 *  
 *  - �Խù��� ���� ���ƿ� ǥ��
 *  - ���ƿ� ǥ�� ���
 *  - ������� ���ƿ� ǥ�� ���� Ȯ��
 *  - ���ƿ� Ƚ�� ���� ����
 *  - �Խù��� ���ƿ� Ƚ�� ��ȸ
 *  - �Խù� ����� ���ƿ� Ƚ�� ��ȸ
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
	
	/** �Խù� 12345�� ���ƿ����� (posting:like:12345) */
	private static final String KEY_LIKE_SET = "posting:like:";
	
	public LikePosting(JedisHelper helper) {
		this.jedis = helper.getConnection();
	}
	
	/**
	 * ������ ����ڰ� �Խù��� ���ƿ� ǥ��
	 * @param postingNo �Խù� ��ȣ
	 * @param userNo ����� ��ȣ
	 * @return true: ����ó��
	 */
	public boolean like(String postingNo, String userNo) {
		return this.jedis.sadd(KEY_LIKE_SET + postingNo, userNo) > 0;
	}
	
	/**
	 * ������ ����ڰ� �Խù��� ���ƿ� ���
	 * @param postingNo �Խù� ��ȣ
	 * @param userNo ����� ��ȣ
	 * @return true: ����ó��
	 */
	public boolean unLike(String postingNo, String userNo) {
		return this.jedis.srem(KEY_LIKE_SET + postingNo, userNo) > 0;
	}
	
	/**
	 * ������ ������� ���ƿ� ǥ�� ���� Ȯ��
	 * @param postingNo �Խù� ��ȣ
	 * @param userNo ����� ��ȣ
	 * @return true: ���ƿ� ó����
	 */
	public boolean isLiked(String postingNo, String userNo) {
		return this.jedis.sismember(KEY_LIKE_SET + postingNo, userNo);
	}
	
	/**
	 * �Խù��� ���� ���ƿ� ���� ����
	 * @param postingNo
	 * @return
	 */
	public boolean deleteLikeInfo(String postingNo) {
		return this.jedis.del(KEY_LIKE_SET + postingNo) > 0;
	}
	
	/**
	 * �Խù��� ���ƿ� Ƚ�� ��ȸ
	 * @param postingNo �Խù� ��ȣ
	 * @return ���ƿ� Ƚ��
	 */
	public Long getLikeCount(String postingNo) {
		return this.jedis.scard(KEY_LIKE_SET + postingNo);
	}
	
	/**
	 * �־��� �Խù� ����� ���ƿ� Ƚ�� ��ȸ
	 * @param postingList ��ȸ��� �Խù� ���
	 * @return ���ƿ� Ƚ�� ���
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
		System.out.println("�־��� �Խù� ����� ���ƿ� Ƚ�� ��ȸ getLikeCountList - " + result);
		return result;
	}
}
