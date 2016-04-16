package net.sf.redis.ch7.visitcount;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * �̺�Ʈ ���̵� ���ڷ� �޾Ƽ� �湮 Ƚ�� ������Ű�� ��ȸ
 *  + ��¥�� �湮Ƚ���� ��¥�� ��ü �̺�Ʈ ������ �湮Ƚ�� ��ȸ
 *  + ������ �̺�Ʈ�� �������� ���� ���� (�ؽõ����ͷ� ó��)
 *  
 * ��¥�� �̺�Ʈ ������ �湮Ƚ�� ����
 * ��¥�� ��� ������ �湮Ƚ�� ��ȸ
 * ��¥�� ���� �̺�Ʈ ������ �湮Ƚ�� ��ȸ
 * ��� ��¥�� �湮Ƚ�� ��ȸ
 * 
 * @author assu
 * @date 2016.04.16
 */
public class VisitCountOfDayV2 {
	private JedisHelper jedisHelper;
	private Jedis jedis;
	private static final String KEY_EVENT_DAILY_CLICK_TOTAL = "event:daily:click:total:hash";
	private static final String KEY_EVENT_DAILY_CLICK = "event:daily:click:hash:";
	
	/**
	 * �湮Ƚ�� ó���� ���� Ŭ���� ������ 
	 * @param jedisHelper ���� ���� ��ü
	 */
	public VisitCountOfDayV2(JedisHelper jedisHelper) {
		this.jedisHelper = jedisHelper;
		this.jedis = this.jedisHelper.getConnection();
	}
	
	/**
	 * �̺�Ʈ ���̵� �ش��ϴ� ��¥�� �湮Ƚ���� ��¥�� ��ü �湮Ƚ�� ����
	 * @param eventId �̺�Ʈ ���̵�
	 * @return �̺�Ʈ ������ �湮Ƚ��
	 */
	public Long addVisit(String eventId) {
		this.jedis.hincrBy(KEY_EVENT_DAILY_CLICK_TOTAL, getToday(), 1);
		return this.jedis.hincrBy(KEY_EVENT_DAILY_CLICK + eventId, getToday(), 1);
	}
	
	/**
	 * �̺�Ʈ �������� ���� ��� ��¥�� �湮Ƚ�� ��ȸ
	 * @return ��ü �̺�Ʈ ������ �湮Ƚ��
	 */
	public SortedMap<String, String> getVisitCountByDailyTotal() {
		// ��ü �̺�Ʈ �������� �湮Ƚ���� ���ĵ� ���·� ��ȸ�ϱ� ����
		SortedMap<String, String> result = new TreeMap<String, String>();
		
		// ��ȸ�� ��¥�� ��ü �̺�Ʈ ������ �湮 Ƚ���� �ʵ������ �����ϱ� ���� SortedMap ��ü�� ����
		result.putAll(this.jedis.hgetAll(KEY_EVENT_DAILY_CLICK_TOTAL));
		return result;
	}
	
	/**
	 * ��û �̺�Ʈ �������� �ش��ϴ¸�� ��¥�� �湮Ƚ�� ��ȸ 
	 * @param eventId �̺�Ʈ ���̵�
	 * @return ��¥�� ���ĵ� �湮Ƚ�� ���
	 */
	public SortedMap<String, String> getVisitCountByDilay(String eventId) {
		SortedMap<String, String> result = new TreeMap<String, String>();
		result.putAll(this.jedis.hgetAll(KEY_EVENT_DAILY_CLICK + eventId));
		return result;
	}
	
	private String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
}
