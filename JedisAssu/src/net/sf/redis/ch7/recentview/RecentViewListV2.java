package net.sf.redis.ch7.recentview;

import java.util.Set;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * �ֱ� ��ȸ ��ǰ ��� (���ĵ� �� ��������, ����ġ�δ� ���� �ð� ����)
 * 
 *  - ����Ǵ� ��ǰ�� �ִ� ������ 20��
 *  - ȭ�� ������ �ֱ� ��ǰ 4�� ����
 *  - �� ���� Ŭ���ϸ� ����� ��� ��ǰ��� ���
 *  
 *  -> �ߺ��� ��ǰ ����
 *  
 * @author assu
 * @date 2016.05.06
 */
public class RecentViewListV2 {
	private Jedis jedis;
	/** ����ڰ� ��ȸ�� ��ǰ ��� (recent:view:zset:12345) */
	private static final String KEY_VIEW_SET = "recent:view:zset:";
	private static final int LIST_MAX_SIZE = 20;
	private String KEY;
	
	public RecentViewListV2(JedisHelper helper, String userNo) {
		this.jedis = helper.getConnection();
		this.KEY = KEY_VIEW_SET + userNo;
	}
	
	/**
	 * �ֱ� ��ȸ ��ǰ��Ͽ� ��ǰ �߰�
	 * @param productNo ��ǰ ��ȣ
	 * @return ����� ��ǰ ����� ����
	 */
	public Long add(String productNo) {
		// zadd(Ű,����ġ,���) : �̹� �����ϸ� ����� ����� ����ġ ����, ���� �߰��Ǹ� 1, ������ �����ϸ� 0 ��ȯ
		Long result = jedis.zadd(this.KEY, System.nanoTime(), productNo);
		
		// zrangeByRank(Ű,�����ε���,�����ε���) : �־��� Ű�� ����� ��� �߿��� ������ �����ε������� �����ε��� ������ �ش��ϴ� ��� ����
        //                                     : ������ ���� �� ��ȯ
		// ��ǰ ���� �� �ִ� �������� ���� ������ �����ϱ� ���� zremrangebyrank ��ɼ����Ͽ� ���� ������ ������ ����
		jedis.zremrangeByRank(this.KEY, -(LIST_MAX_SIZE+1), -(LIST_MAX_SIZE+1));
		System.out.println("�ֱ� ��ȸ ��ǰ��Ͽ� ��ǰ �߰� add - " + result);
		return result;
	}
	
	/**
	 * �־��� ������� ����� �ֱ� ��ȸ ��ǰ ��� ��ȸ
	 * @return ��ȸ�� ��ǰ ���
	 */
	public Set<String> getRecentViewList() {
		// zrange(Ű,�����ε���,�����ε���) : �����ε������� �����ε������� ����ġ ������������ ��ȸ (zrevrange�� ��������
		//                               : ��ȸ�� �� ��� ��ȯ
		System.out.println("�־��� ������� ����� �ֱ� ��ȸ ��ǰ ��� ��ȸ getRecentViewList - " + this.jedis.zrevrange(this.KEY, 0, -1));
		return this.jedis.zrevrange(this.KEY, 0, -1);
	}
	
	/**
	 * �־��� ������ �ش��ϴ� �ֱ� ��ȸ ��ǰ ��� ��ȸ
	 * @param cnt ��ȸ�� ��ǰ�� ����
	 * @return ��ȸ�� ��ǰ ���
	 */
	public Set<String> getRecentViewList(int cnt) {
		System.out.println("�־��� ������ �ش��ϴ� �ֱ� ��ȸ ��ǰ ��� ��ȸ getRecentViewList - " + this.jedis.zrevrange(this.KEY, 0, cnt-1));
		return this.jedis.zrevrange(this.KEY, 0, cnt-1);
	}
	
	/**
	 * ���尡���� ��ǰ ����� �ִ� ���� ��ȸ
	 * @return ��ǰ ����� �ִ� ����
	 */
	public int getListMaxSize() {
		return LIST_MAX_SIZE;
	}
}
