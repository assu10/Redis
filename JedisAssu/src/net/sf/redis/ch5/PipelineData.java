package net.sf.redis.ch5;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 레디스 명령행 클라이언트에서 사용할 파이프라인을 위한 텍스트 파일 생성
 * @author Administrator
 *
 */
public class PipelineData {
	private BufferedWriter writer;
	
	private String fileNamePrefix = "./redis_data";
	
	private String fileNamePostfix = ".txt";
	
	private final int TOTAL_NUMBER_OF_COMMAND = 100000;	// 작성할 데이터 건수 지정 (십만건)
	
	public static void main(String[] args) throws IOException {
		PipelineData data = new PipelineData();
		data.makeDataFileAsCommans();
		data.makeDataFileAsProtocol();
	}
	
	/**
	 * 레디스 명령어 형식으로 텍스트 파일 생성
	 * set key100000 key100000
	 * @throws IOException 
	 */
	private void makeDataFileAsCommans() throws IOException {
		String fileName = fileNamePrefix + "_command" + fileNamePostfix;	// 명령어 형식 데이터 파일의 이름 지정
		writer = new BufferedWriter(new FileWriter(fileName));
		
		String key, value;
		for (int i = 0; i < TOTAL_NUMBER_OF_COMMAND; i++) {
			key = value = String.valueOf("key" + (100000+i));
			writer.write("set " + key + " " + value + "\r\n");	// 명령어 형식의 데이터 생성, 키와 값은 	9바이트로 고정
		}
		
		writer.flush();
		writer.close();
	}
	
	/**
	 * 레디스 프로토콜 형식의 텍스트 파일 생성
	 * *3
	 * $3
	 * set
	 * $9
	 * key100000
	 * $9
	 * key100000
	 * @throws IOException 
	 */
	private void makeDataFileAsProtocol() throws IOException {
		String fileName = fileNamePrefix + "_protocal" + fileNamePostfix;	// 프로토콜 형식 데이터 파일의 이름을 지정
		writer = new BufferedWriter(new FileWriter(fileName));
		
		String key, value;
		
		for (int i = 0; i < TOTAL_NUMBER_OF_COMMAND; i++) {
			key = value = String.valueOf("key" + (100000+i));
			
			writer.write("*3\r\n");		// 5
			writer.write("$3\r\n");
			writer.write("set\r\n");
			writer.write("$" + key.length() + "\r\n");
			writer.write(key + "\r\n");
			writer.write("$" + value.length() + "\r\n");
			writer.write(value + "\r\n");
		}
		
		writer.flush();
		writer.close();
	}
}
