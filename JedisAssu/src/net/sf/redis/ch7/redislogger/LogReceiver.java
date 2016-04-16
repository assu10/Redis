package net.sf.redis.ch7.redislogger;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * 레디스 서버에 저장된 로그를 파일로 저장
 * 
 * @author assu
 * @date 2016.04.02
 */
public class LogReceiver {
	private static final JedisHelper helper = JedisHelper.getInstance();
	private static final String KEY_WAS_LOG = "was:log";
	private static final String LOG_FILE_NAME_PREFIX = "./waslog";		// 로그파일의 경로와 이름 지정
	
	// 로그 파일명을 시간별로 새로 생성하기 위한 포맷터 지정
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH'.log'");
	private static int WAITING_TERM = 5000;	// 로그파일을 기록할 주기 지정(5초)
	
	/**
	 * 레디스 서버에서 로그를 읽어서 파일로 저장.
	 * 프로그램이 종료되기 전까지 무한 실행.
	 */
	public void start() {
		Random random = new Random();
		Jedis jedis = helper.getConnection();
		
		while(true) {	// 로그는 항상 발생하기 때문에 프로그램이 종료될 때까지 항상 실행되어야 하므로 무한 루프.
			// 레디스의 getset 명령을 이용하여 데이터를 읽고 빈 문자열을 저장하여 로그가 중복 저장되지 않도록 레디스에서 지운다.
			// getset : 입력된 값을 저장하고 이전에 저장된 값을 돌려줌, 존재하지 않는 키면 값 저장 후 nil 리턴.
			writeFile(jedis.getSet(KEY_WAS_LOG, ""));
			
			try {
				Thread.sleep(random.nextInt(WAITING_TERM)); 	// 로그파일을 기록하고 나서 5초동안 대기.
			} catch (Exception e) {
				// do nothing.
			}
		}
	}
	
	private void writeFile(String log) {
		try {
			FileWriter writer = new FileWriter(getCurrentFileName(), true);
			
			writer.write(log);	// 메서드가 호출된 시간에 해당하는 파일에 로그를 기록
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 메서드가 호출된 시간에 해당하는 로그파일명 생성.
	 * @return 현재 시간에 해당하는 로그파일명
	 */
	public String getCurrentFileName() {
		// 메서드가 호출된 시간에 따라서 로그를 저장할 파일명 생성
		return LOG_FILE_NAME_PREFIX + sdf.format(new Date());
	}
}
