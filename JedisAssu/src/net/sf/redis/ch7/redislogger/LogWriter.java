package net.sf.redis.ch7.redislogger;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * 레디스 서버에 로그 문자열을 기록.
 * 
 * [문제점]
 * append 명령을 처리할 때마다 기존 문자열과 새로 추가되는 문자열의 크기에 따로 새로운 배열을 생성.
 * 새로운 메모리 할당하고 기존 메모리 해제하는데에 많은 리소스 소요.
 * --> 리스트 데이터형 사용하는 것으로 대체 (LogWriterV2)
 * 
 * @author assu
 * @date 2016.04.02
 */
public class LogWriter {
	private static final String KEY_WAS_LOG = "was:log";		// 레디스에 저장될 로그의 키이름
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS ");	// 로그에 추가할 날짜와 시간 문자열 포맷
	
	JedisHelper helper;
	
	/**
	 * f레디스에 로그를 기록하기 위한 Logger 생성자
	 * @param helper 제디스 헬퍼 객체
	 */
	public LogWriter(JedisHelper helper) {
		this.helper = helper;
	}
	
	/**
	 * 레디스에 로그를 기록하는 메서드
	 * @param log 저장할 로그문자열
	 * @return 저장된 후의 레디스에 저장된 로그 문자열의 길이
	 */
	public Long log(String log) {
		Jedis jedis = this.helper.getConnection();	// 제디스 연결을 가져온다.
		Long rtn = jedis.append(KEY_WAS_LOG, sdf.format(new Date()) + log + "\n");
		
		this.helper.returnResource(jedis);
		
		return rtn;
	}
}
