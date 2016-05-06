package net.sf.redis.ch7.recentview;

import java.util.List;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * �ֱ� ��ȸ ��ǰ ��� (list ��������)
 * 
 *  - ����Ǵ� ��ǰ�� �ִ� ������ 30��
 *  - ȭ�� ������ �ֱ� ��ǰ 4�� ����
 *  - �� ���� Ŭ���ϸ� ����� ��� ��ǰ��� ���
 *  
 *  -> �ߺ��� ��ǰ�� �����
 *  
 * @author assu
 * @date 2016.05.06
 */
public class RecentViewList {
	private Jedis jedis;
	/** ����ڰ� ��ȸ�� ��ǰ ��� (recent:view:12345) */
	private static final String KEY_VIEW_LIST = "recent:view:";
	private static final int LIST_MAX_SIZE = 30;
	private String userNo;
	
	public RecentViewList(JedisHelper helper, String userNo) {
		this.jedis = helper.getConnection();
		this.userNo = userNo;
	}
	
	/**
	 * �ֱ� ��ȸ ��ǰ ��Ͽ� ��ǰ �߰� �� �ִ����尹���� ���� �������� ����
	 * @param productNo ��ǰ ��ȣ
	 * @return ����� ��ǰ ��� ����
	 */
	public Long add(String productNo) {
		Long result = this.jedis.lpush(KEY_VIEW_LIST + this.userNo, productNo);	// ����Ʈ ������ ���ʿ� ����
		
		// ltrim(Ű,�����ε���,�����ε���) : �־��� Ű�� �����ε������� �����ε��� ������ ��Ҹ��� ����� ����
		//                                ��, �����ε����� �����ε������� ������ ��� ��� ����
		//                                ���ϰ��� �׻� OK
		this.jedis.ltrim(KEY_VIEW_LIST + this.userNo, 0, LIST_MAX_SIZE-1);
		System.out.println("�ֱ� ��ȸ ��ǰ ��Ͽ� ��ǰ �߰� �� �ִ����尹���� ���� �������� ���� add - " + result);
		return result;
	}
	
	/**
	 * �־��� ������� ����� �ֱ� ��ȸ ��ǰ��� ��ȸ
	 * @return ��ȸ�� ��ǰ���
	 */
	public List<String> getRecentViewList() {
		System.out.println("�־��� ������� ����� �ֱ� ��ȸ ��ǰ��� ��ȸ getRecentViewList - " + this.jedis.lrange(KEY_VIEW_LIST + this.userNo, 0, -1));
		// lrange(Ű,�����ε���,�����ε���) : �����ε������� �����ε��� ���� ��� ��ȸ, ������ Ű�� �������� ������ nil
		return this.jedis.lrange(KEY_VIEW_LIST + this.userNo, 0, -1);
	}
	
	/**
	 * �־��� ������ �ش��ϴ� �ֱ� ��ȸ ��ǰ��� ��ȸ
	 * @param cnt ��ȸ�� ��ǰ ����
	 * @return ��ȸ�� ��ǰ���
	 */
	public List<String> getRecentViewList(int cnt) {
		System.out.println("�־��� ������ �ش��ϴ� �ֱ� ��ȸ ��ǰ��� ��ȸ getRecentViewList(int) - " + this.jedis.lrange(KEY_VIEW_LIST + this.userNo, 0, cnt-1));
		return this.jedis.lrange(KEY_VIEW_LIST + this.userNo, 0, cnt-1);
	}
	
	/**
	 * ���尡���� ��ǰ ����� �ִ� ���� ��ȸ
	 * @return ��ǰ����� �ִ� ����
	 */
	public int getListMaxSize() {
		return LIST_MAX_SIZE;
	}
}
