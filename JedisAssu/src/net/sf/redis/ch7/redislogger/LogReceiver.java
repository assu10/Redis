package net.sf.redis.ch7.redislogger;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * ���� ������ ����� �α׸� ���Ϸ� ����
 * 
 * @author assu
 * @date 2016.04.02
 */
public class LogReceiver {
	private static final JedisHelper helper = JedisHelper.getInstance();
	private static final String KEY_WAS_LOG = "was:log";
	private static final String LOG_FILE_NAME_PREFIX = "./waslog";		// �α������� ��ο� �̸� ����
	
	// �α� ���ϸ��� �ð����� ���� �����ϱ� ���� ������ ����
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH'.log'");
	private static int WAITING_TERM = 5000;	// �α������� ����� �ֱ� ����(5��)
	
	/**
	 * ���� �������� �α׸� �о ���Ϸ� ����.
	 * ���α׷��� ����Ǳ� ������ ���� ����.
	 */
	public void start() {
		Random random = new Random();
		Jedis jedis = helper.getConnection();
		
		while(true) {	// �α״� �׻� �߻��ϱ� ������ ���α׷��� ����� ������ �׻� ����Ǿ�� �ϹǷ� ���� ����.
			// ������ getset ����� �̿��Ͽ� �����͸� �а� �� ���ڿ��� �����Ͽ� �αװ� �ߺ� ������� �ʵ��� ���𽺿��� �����.
			// getset : �Էµ� ���� �����ϰ� ������ ����� ���� ������, �������� �ʴ� Ű�� �� ���� �� nil ����.
			writeFile(jedis.getSet(KEY_WAS_LOG, ""));
			
			try {
				Thread.sleep(random.nextInt(WAITING_TERM)); 	// �α������� ����ϰ� ���� 5�ʵ��� ���.
			} catch (Exception e) {
				// do nothing.
			}
		}
	}
	
	private void writeFile(String log) {
		try {
			FileWriter writer = new FileWriter(getCurrentFileName(), true);
			
			writer.write(log);	// �޼��尡 ȣ��� �ð��� �ش��ϴ� ���Ͽ� �α׸� ���
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �޼��尡 ȣ��� �ð��� �ش��ϴ� �α����ϸ� ����.
	 * @return ���� �ð��� �ش��ϴ� �α����ϸ�
	 */
	public String getCurrentFileName() {
		// �޼��尡 ȣ��� �ð��� ���� �α׸� ������ ���ϸ� ����
		return LOG_FILE_NAME_PREFIX + sdf.format(new Date());
	}
}
