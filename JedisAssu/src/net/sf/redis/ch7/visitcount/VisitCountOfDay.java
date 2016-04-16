package net.sf.redis.ch7.visitcount;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * �̺�Ʈ ���̵� ���ڷ� �޾Ƽ� �湮 Ƚ�� ������Ű�� ��ȸ
 *  + ��¥�� �湮Ƚ���� ��¥�� ��ü �̺�Ʈ ������ �湮Ƚ�� ��ȸ
 *  --> Ư�� �̺�Ʈ�� ���� �����ߴ����� ���� ������ �������� �ʱ� ������ ���۳�¥�� ���ᳯ¥ ��ȸ �Ұ���
 *  
 * ��¥�� �̺�Ʈ ������ �湮Ƚ�� ����
 * ��¥�� ��� ������ �湮Ƚ�� ��ȸ
 * ��¥�� ���� �̺�Ʈ ������ �湮Ƚ�� ��ȸ
 * 
 * @author assu
 * @date 2016.04.16
 */
public class VisitCountOfDay {
	private JedisHelper jedisHelper;
	private Jedis jedis;
	private static final String KEY_EVENT_CLICK_DAILY_TOTAL = "event:click:daily:total:";
	private static final String KEY_EVENT_CLICK_DAILY = "event:click:daily:";
	
	/**
	 * ��¥�� �湮Ƚ�� ó���� ���� Ŭ���� ������
	 * @param jedisHelper ���� ���� ��ü
	 */
	public VisitCountOfDay(JedisHelper jedisHelper) {
		this.jedisHelper = jedisHelper;
		this.jedis= this.jedisHelper.getConnection();
	}
	
	/**
	 * �̺�Ʈ ���̵� �ش��ϴ� ��¥�� �湮Ƚ���� ��¥�� ��ü �湮Ƚ�� ����
	 * @param eventId �̺�Ʈ ���̵�
	 * @return �̺�Ʈ ������ �湮Ƚ��
	 */
	public Long addVisit(String eventId) {
		System.out.println("addVisit() : " + eventId + " - " + this.jedis.get(KEY_EVENT_CLICK_DAILY + getToday() + ":" + eventId));
		
		this.jedis.incr(KEY_EVENT_CLICK_DAILY_TOTAL + getToday());
		return this.jedis.incr(KEY_EVENT_CLICK_DAILY + getToday() + ":" + eventId);
	}
	
	/**
	 * ��û�� ��¥�� �ش簣�� ��ü �̺�Ʈ ������ �湮Ƚ�� ��ȸ
	 * @return ��ü �̺�Ʈ ������ �湮Ƚ��
	 */
	public String getVisitTotal(String date) {
		return this.jedis.get(KEY_EVENT_CLICK_DAILY_TOTAL + date);
	}
	
	/**
	 * �̺�Ʈ ���̵� �ش��ϴ� ��û�� ��¥���� �湮Ƚ�� ��ȸ
	 * @param eventId ��û�� �̺�Ʈ ���̵�
	 * @param dateList ��û��¥ ���
	 * @return ��¥ ��Ͽ� ���� �湮Ƚ�� ���
	 */
	public List<String> getVisitCountByDate(String eventId, String[] dateList) {
		List<String> result = new ArrayList<String>();
		for(int i=0; i<dateList.length; i++) {
			result.add(jedis.get(KEY_EVENT_CLICK_DAILY + dateList[i] + ":" + eventId));
		}
		return result;
	}
	
	private String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
}
