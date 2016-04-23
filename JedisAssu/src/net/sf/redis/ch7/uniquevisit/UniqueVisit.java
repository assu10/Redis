package net.sf.redis.ch7.uniquevisit;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * ����Ʈ ���湮�ڼ��� �����湮�ڼ� ���� �� ��ȸ(setBit)
 * 
 * �� ����ڴ� ������ ����ڹ�ȣ�� ���� ������ �湮 Ƚ���� �����湮Ƚ���� �����湮Ƚ���� ����.
 * ���Ϲ湮Ƚ���� �� ���� ����ڴ� �ѹ��� �湮 Ƚ���� ���
 * �����湮Ƚ���� �� ���� ����ڰ� ������ �湮�ϸ� �湮�� Ƚ����ŭ ���
 * ����� ���湮Ƚ���� �����湮Ƚ���� ��� ��ȸ�� �����ؾ� ��.
 * 
 * - ����ڰ� ������ ��¥�� �����湮Ƚ�� ����
 * - ����ڰ� ������ ��¥�� ���湮Ƚ�� ����
 * - ������ ��¥�� �����湮Ƚ�� ��ȸ
 * - ������ ��¥�� ���湮Ƚ�� ��ȸ
 * 
 * setbit(key, offset, value) - ��������(������ ����Ǿ��� ��Ʈ��)
 * 
 * @author assu
 * @date 2016.04.23
 */
public class UniqueVisit {
	private Jedis jedis;
	/** �����湮�ڼ�(page:view:20160423) */
	private static final String KEY_PAGE_VIEW = "page:view:";
	/** ���湮�ڼ�(unique:visitors:20160423) */
	private static final String KEY_UNIQUE_VISITOR = "unique:visitors:";
	
	public UniqueVisit(JedisHelper helper) {
		this.jedis = helper.getConnection();
	}
	
	/**
	 * Ư�� ������� ���湮Ƚ���� �����湮Ƚ�� ����
	 * @param userNo ����ڹ�ȣ
	 */
	public void visit(int userNo) {
		// 2���� ���� ����� �ѹ��� �����ϱ� ���� ���������� ���
		Pipeline p = this.jedis.pipelined();
		p.incr(KEY_PAGE_VIEW + getToday());
		
		// ����ڹ�ȣ�� �ش��ϴ� ������ ��ġ�� ��Ʈ���� 1�� ����
		p.setbit(KEY_UNIQUE_VISITOR + getToday(), userNo, true);
		p.sync();
	}
	
	/**
	 * ��û�� ��¥�� �����湮�ڼ�(PageView) ��ȸ
	 * @param date ��ȸ��� ��¥
	 * @return �����湮�ڼ�
	 */
	public int getPVCount(String date) {
		int result = 0;
		try {
			result = Integer.parseInt(jedis.get(KEY_PAGE_VIEW + date));
		} catch (Exception e) {
			result = 0;
		}
		System.out.println("��û�� ��¥�� �����湮�ڼ�(PageView) ��ȸ, getPVCount - " + result);
		return result;
	}
	
	/**
	 * ��û�� ��¥�� ���湮�ڼ�(UniqueView) ��ȸ
	 * @param date ��ȸ��� ��¥
	 * @return ���湮�ڼ�
	 * @deprecated bitcount�� 2.6.0���� ��밡���ѵ� ���� 2.4.5�� �׽�Ʈ �Ұ�
	 */
	public Long getUVCount(String date) {
		// bitcount(key, [start index],[end index]) - ��������(������ ������ ����� ��Ʈ �� 1�� ����)
		// 2.6.0���� ��밡���ѵ� ���� 2.4.5�� �׽�Ʈ �Ұ�
		return jedis.bitcount(KEY_UNIQUE_VISITOR + date);
	}
	
	private String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
}
