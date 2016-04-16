package net.sf.redis.ch7.redislogger;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * ���� ������ �α� ���ڿ��� ���.
 * 
 * [������]
 * append ����� ó���� ������ ���� ���ڿ��� ���� �߰��Ǵ� ���ڿ��� ũ�⿡ ���� ���ο� �迭�� ����.
 * ���ο� �޸� �Ҵ��ϰ� ���� �޸� �����ϴµ��� ���� ���ҽ� �ҿ�.
 * --> ����Ʈ �������� ����ϴ� ������ ��ü (LogWriterV2)
 * 
 * @author assu
 * @date 2016.04.02
 */
public class LogWriter {
	private static final String KEY_WAS_LOG = "was:log";		// ���𽺿� ����� �α��� Ű�̸�
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS ");	// �α׿� �߰��� ��¥�� �ð� ���ڿ� ����
	
	JedisHelper helper;
	
	/**
	 * f���𽺿� �α׸� ����ϱ� ���� Logger ������
	 * @param helper ���� ���� ��ü
	 */
	public LogWriter(JedisHelper helper) {
		this.helper = helper;
	}
	
	/**
	 * ���𽺿� �α׸� ����ϴ� �޼���
	 * @param log ������ �α׹��ڿ�
	 * @return ����� ���� ���𽺿� ����� �α� ���ڿ��� ����
	 */
	public Long log(String log) {
		Jedis jedis = this.helper.getConnection();	// ���� ������ �����´�.
		Long rtn = jedis.append(KEY_WAS_LOG, sdf.format(new Date()) + log + "\n");
		
		this.helper.returnResource(jedis);
		
		return rtn;
	}
}
