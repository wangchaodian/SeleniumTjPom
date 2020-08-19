package executionEngine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class dataValue {

	/**
	 * 根据key读取value
	 * 
	 * @Title: getProperties_2
	 * @Description: 第二种方式：使用缓冲输入流读取配置文件，然后将其加载，再按需操作 绝对路径或相对路径，
	 *               如果是相对路径，则从当前项目下的目录开始计算， 如：当前项目路径/config/config.properties,
	 *               相对路径就是config/config.properties
	 * 
	 * @param filePath
	 * @param keyWord
	 * @return
	 * @return String
	 * @throws
	 */
	public static String getProperties(String filePath, String keyWord) {
		Properties prop = new Properties();
		String value = null;
		try {
			// 通过输入缓冲流进行读取配置文件
			InputStream InputStream = new BufferedInputStream(
					new FileInputStream(new File(filePath)));
			// 加载输入流
			prop.load(InputStream);
			// 根据关键字获取value值
			value = prop.getProperty(keyWord);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

}
