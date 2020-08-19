package executionEngine;

/*
 * �����������ͬ����
 */

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import Sql.DBProxy;

public class dataCheckPage {

	public static WebDriver driver;
	public static DBProxy proxy = new DBProxy("jdbc:mysql://192.168.1.222:3306/tj", "tjtest", "123456");
	public static String username, password,local;
	public static String NowDate = getDate();
	public static int countRow;

	public static void main(String [] args) throws Exception{
		ChromeOptions option = new ChromeOptions();
		// option.addArguments("headless"); //�g�[����̨�\��
		option.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});// ȡ����ʾ
		// ����Chrome����
		System.setProperty("webdriver.chrome.driver","D:\\chromedriver.exe");
		driver = new ChromeDriver(option);
		driver.manage().window().maximize();
		// ��ʽʱ��ȴ�����λ����
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		// ҳ�����ʱ�ĳ�ʱ�ȴ�����
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		String url = dataValue.getProperties("E://dbinfo.properties", "url") ;
		driver.get(url);
		userName(countRow);
		for (int i = 0; i < countRow; i++) {

			try {
				Thread.sleep(1000*1);
				driver.findElement(By.cssSelector(".login-toggle")).click();
				Thread.sleep(1000*1);
				userName(i);
				driver.findElement(By.cssSelector(".user-label.username>input")).clear();
				driver.findElement(By.cssSelector(".user-label.username>input")).sendKeys(username);
				driver.findElement(By.cssSelector(".user-label.password>input")).clear();
				driver.findElement(By.cssSelector(".user-label.password>input")).sendKeys(password);
				driver.findElement(By.cssSelector(".submit-login")).click();
				Thread.sleep(6000);	
				String city = getName();
				File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(srcFile, new File("D:\\tj\\"+NowDate+"\\"+city + "1.png"));
				Thread.sleep(1000);
				System.out.println((i+1)+":"+local+" ��һ����ͼ���");
				//�ж��Ƿ� ������һ����������ڵ����һ�����н�ͼ
				for (int j = 0; j < 50; j++) {
					
					mouseXy("footer-sensor", "400", "3");
					Thread.sleep(2000);
					WebElement a = driver.findElement(By.cssSelector(".app-footer > .screen-footer > .screen-pager > div:nth-child(2)"));
					String attribute = a.getAttribute("class");
//					System.out.println(attribute);
					if(attribute.equals("disabled-false")){
						a.click();
						Thread.sleep(1000*4);
						File srcFile1 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
						FileUtils.copyFile(srcFile1, new File("D:\\tj\\"+NowDate+"\\"+city + (j+2)+".png"));
						System.out.println((i+1)+":"+local+" ��"+(j+2)+"����ͼ���");
				
					}else {
						
						driver.navigate().refresh();
						break ;
					}
				}
		
			} catch (Exception e) {
				System.err.println((i+1)+":�޷���¼�ĵ��� :"+local);
				File srcFile2 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(srcFile2, new File("D:\\tj\\"+NowDate+"\\"+local +".png"));
				driver.navigate().refresh();				
			}			
		}		
		System.out.println("���Խ���");
//		driver.close();  // �رյ�ǰҳ��
		driver.quit();   // �ر������ȫ��ҳ�棬��ɱ��Chromedriver����
					
	}
	
	public static void userName(int i) {
        
		String table = dataValue.getProperties("E://dbinfo.properties", "table");
		List<Map<String, Object>> listLinkedMap = proxy.getListLinkedMap("SELECT * FROM "+table);
		username = (String) listLinkedMap.get(i).get("username");
		password = (String) listLinkedMap.get(i).get("password");
		local = (String) listLinkedMap.get(i).get("city");
		countRow = (int)listLinkedMap.size();

	}

	// ���ϵͳ��ǰʱ��
	public static String getDate() {

		// ��õ�ǰ����
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yy.MM.dd");// �������ڸ�ʽ("yyyy-MM-dd HH:mm:ss")
		String Nowdate = df.format(date);
		// System.out.println("�������� �� "+Nowdate);
		return Nowdate;
	}

	public static String getName() {

		String city_name = driver.findElement(
				By.cssSelector(".marquee-line-text")).getText();
		// System.out.println(city);
		return city_name;
	}

	// �����Ԫ��ָ������㣨X,Y��
	public static void mouseXy(String object, String x, String y)
			throws Exception {

		try {
			Actions drawPen = new Actions(driver);
			// ��λԪ��
			WebElement element = driver.findElement(By.className(object));
			int X = Integer.parseInt(x);
			int Y = Integer.parseInt(y);
			Thread.sleep(1000 * 1);
			drawPen.moveToElement(element,X, Y).doubleClick().perform();
		} catch (Exception e) {
		
			DriverScript.bResult = false;
		}

	}
}
