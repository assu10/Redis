package net.sf.redis.ch7.uniquevisit;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * ����Ʈ ���湮�ڼ��� �����湮�ڼ� ���� �� ��ȸ(setBit)
 *  + �����ϴ����� ���湮�ڼ�(=�����ϵ��� ���� �α����� �����)��ȸ(bitop, bitcount)
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
 * bitop(��Ʈ������, ���Ű, ����Ű1 [����Ű2...]) - ��������(���Ű�� ����Ʈ ����)
 *       ��밡�� ��Ʈ�����ڴ� and, or, xor, not
 * 
 * @author assu
 * @date 2016.05.01
 */
public class UniqueVisitV2 {
	private Jedis jedis;
	/** �����湮�ڼ�(page:view:20160423) */
	private static final String KEY_PAGE_VIEW = "page:view:";
	/** ���湮�ڼ�(unique:visitors:20160423) */
	private static final String KEY_UNIQUE_VISITOR = "unique:visitors:";
	
	public UniqueVisitV2(JedisHelper helper) {
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
	 * �׽�Ʈ�� ���� �־��� ��¥�� ���湮Ƚ���� �����湮Ƚ�� ����
	 * @param userNo ����ڹ�ȣ
	 * @param date �׽�Ʈ�� ���� ��¥
	 */
	public void visit(int userNo, String date) {
		Pipeline p = this.jedis.pipelined();
		p.incr(KEY_PAGE_VIEW + date);
		p.setbit(KEY_UNIQUE_VISITOR + date, userNo, true);
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
			result = Integer.parseInt(this.jedis.get(KEY_PAGE_VIEW + date));
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
	 */
	public Long getUVCount(String date) {
		// bitcount(key, [start index],[end index]) - ��������(������ ������ ����� ��Ʈ �� 1�� ����)
		System.out.println("��û�� ��¥�� ���湮�ڼ�(UniqueView) ��ȸ, getUVCount - " + jedis.bitcount(KEY_UNIQUE_VISITOR + date));
		return this.jedis.bitcount(KEY_UNIQUE_VISITOR + date);
	}
	
	private String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
	
	/**
	 * �־��� �Ⱓ�� ���湮�ڼ��� ���
	 * @param dateList ���湮�ڼ��� ���� �Ⱓ
	 * @return �־��� �Ⱓ�� ���湮�ڼ�
	 */
	public Long getUVSum(String[] dateList) {
		String[] keyList = new String[dateList.length];
		int i = 0;
		for (String item : dateList) {
			keyList[i] = KEY_UNIQUE_VISITOR + item;		// �־��� ��¥��Ͽ� �ش��ϴ� ���湮�ڼ� Ű ����
			i++;
		}
		
		// bitop(��Ʈ������, ���Ű, ����Ű1 [����Ű2...]) - ��������(���Ű�� ����Ʈ ����)
		// ������ Ű��ϰ� bitop ����� ����Ͽ� ��Ʈ���� ������� �� ����� uv:event Ű�� ����
		jedis.bitop(BitOP.AND, "uv:event", keyList);
		System.out.println("�־��� �Ⱓ�� ���湮�ڼ��� ���, getUVSum - " + jedis.bitcount("uv:event"));
		
		return jedis.bitcount("uv:event");
	}
}
