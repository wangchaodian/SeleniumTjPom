package Sql;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.alibaba.druid.util.StringUtils;

public class TypeConverter {
	public static void main(String[] args) throws Exception {
		getImageFromNetByUrl(
				"http://tj.sunsharp.cn/tianjiBack/verifiCode?t=Math.random()",
				"d:\\", "1.jpg");
		String imgBase64 = TypeConverter.GetImageStr("d:\\1.jpg");
		String url = "http://192.168.1.154:8091/code-statistics/verificationCode/getRest";
		String body = "a=" + imgBase64;
		String crawForJson = crawForJsonPOST(url, null, body, "utf-8");
		System.out.println(crawForJson);
	}

	// 鍥剧墖杞寲鎴恇ase64瀛楃涓�
	public static String GetImageStr(String path) {// 灏嗗浘鐗囨枃浠惰浆鍖栦负瀛楄妭鏁扮粍瀛楃涓诧紝骞跺鍏惰繘琛孊ase64缂栫爜澶勭悊
		String imgFile = path;// 寰呭鐞嗙殑鍥剧墖
		InputStream in = null;
		byte[] data = null;
		// 璇诲彇鍥剧墖瀛楄妭鏁扮粍
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 瀵瑰瓧鑺傛暟缁凚ase64缂栫爜
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);// 杩斿洖Base64缂栫爜杩囩殑瀛楄妭鏁扮粍瀛楃涓�
	}

	// 鍥剧墖杞寲鎴恇ase64瀛楃涓�
	public static String GetImageStrByFileInput(InputStream in) {// 灏嗗浘鐗囨枃浠惰浆鍖栦负瀛楄妭鏁扮粍瀛楃涓诧紝骞跺鍏惰繘琛孊ase64缂栫爜澶勭悊
		byte[] data = null;
		// 璇诲彇鍥剧墖瀛楄妭鏁扮粍
		try {
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 瀵瑰瓧鑺傛暟缁凚ase64缂栫爜
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);// 杩斿洖Base64缂栫爜杩囩殑瀛楄妭鏁扮粍瀛楃涓�
	}

	// base64瀛楃涓茶浆鍖栨垚鍥剧墖
	public static boolean GenerateImage(String imgStr) {
		System.out.print("宸茬粡鏀跺埌浜嗘妸瀛楄妭鐮佽浆鍖栦负鍥剧墖鐨勬柟娉�");
		// 瀵瑰瓧鑺傛暟缁勫瓧绗︿覆杩涜Base64瑙ｇ爜骞剁敓鎴愬浘鐗�
		if (imgStr == null) // 鍥惧儚鏁版嵁涓虹┖
			return false;

		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64瑙ｇ爜
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 璋冩暣寮傚父鏁版嵁
					b[i] += 256;
				}
			}
			// 鐢熸垚jpeg鍥剧墖
			String imagePath = "c:";
			// System.currentTimeMillis()
			String imgFilePath = "C:\\inetpub\\wwwroot\\school_mart.jpg";// 鏂扮敓鎴愮殑鍥剧墖
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void getImageFromNetByUrl(String strUrl, String path,
			String imageName) throws Exception {
		URL url = new URL(strUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		conn.setConnectTimeout(5 * 1000);
		// String headerFields = conn.getHeaderField("code");
		// String imageName =headerFields+".jpg";
		InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
		byte[] btImg = readInputStream(inStream);// 得到图片的二进制数据
		inStream.close();
		conn.disconnect();
		try {
			File file = new File(path + imageName);
			FileOutputStream fops = new FileOutputStream(file);
			fops.write(btImg);
			fops.flush();
			fops.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}

	public static String crawForJsonPOST(String url,
			Map<String, String> headers, String body, String charSet) {
		String result = "";
		int count = 0;
		while (count < 3) {
			try {
				URL path = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) path
						.openConnection();
				conn.addRequestProperty("Accept", "application/json");
				conn.setReadTimeout(60000);
				conn.setConnectTimeout(60000);
				conn.setRequestMethod("POST");
				conn.setDoInput(true);
				boolean empty = StringUtils.isEmpty(body);
				if (!StringUtils.isEmpty(body)) {
					conn.setDoOutput(true);
				}
				if (headers != null) {
					for (Entry<String, String> entry : headers.entrySet()) {
						conn.addRequestProperty(entry.getKey(),
								entry.getValue());
					}
				}
				conn.connect();
				OutputStreamWriter osw = null;
				if (!StringUtils.isEmpty(body)) {
					osw = new OutputStreamWriter(conn.getOutputStream(),
							charSet);
					osw.write(body);
					osw.flush();
				}
				InputStream inputStream = conn.getInputStream();
				BufferedReader d = new BufferedReader(new InputStreamReader(
						inputStream, charSet));
				String readLine = null;
				while ((readLine = d.readLine()) != null) {
					result += readLine;
				}
				if (null != osw)
					osw.close();
				inputStream.close();
				d.close();
				conn.disconnect();
				break;
			} catch (Exception e) {
				count++;
				e.printStackTrace();
			}
		}
		return result;
	}

}
