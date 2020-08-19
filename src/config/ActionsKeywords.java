package config;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import utility.ExcelUtils;
import utility.Log;
import executionEngine.DriverScript;
import static executionEngine.DriverScript.OR;

public class ActionsKeywords {

	public static WebDriver driver;
	public static String all;
	public static String title;
	public static int moduletotalNum;

	// �������
	public static void openBrowser(String object, String data, String y) {
		// ׌�g�[����̨�\��

		try {
			ChromeOptions option = new ChromeOptions();
			// option.addArguments("headless"); //�g�[����̨�\��
			option.addArguments("disable-infobars");// ȡ����ʾ
			// ����Chrome����
			System.setProperty("webdriver.chrome.driver",
					"D:\\chromedriver.exe");
			driver = new ChromeDriver(option);
			Log.info("����Chrome�����!");
		} catch (Exception e) {
			Log.error("�޷���������� --- " + e.getMessage());
			DriverScript.bResult = false;

		}

	}

	// �򿪲�����ַ
	public static void openUrl(String object, String data, String y) {

		try {
			Log.info("�����������󻯣�");
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			Log.info("�򿪲��Է�������ַ��");
			driver.get(data);
		} catch (Exception e) {
			Log.error("�޷��򿪲��Ի�����ַ --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}
	// ˢ��ҳ��
	public static void refresh(String object,String data,String y){
		driver.navigate().refresh();
	}
	// ���ҳ��Ԫ��
	public static void click(String object, String data, String y) {
		try {
			Log.info("���Ԫ�أ� " + object);
			driver.findElement(By.xpath(OR.getProperty(object))).click();
		} catch (Exception e) {
			Log.error("�޷����Ԫ�� --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// ���������������
	public static void input(String object, String data, String y) {
		try {
			Log.info("��ʼ�� " + object + "�����ı�");
			Thread.sleep(1000 * 1);
			driver.findElement(By.xpath(OR.getProperty(object))).sendKeys(data);
		} catch (Exception e) {
			Log.error("�޷������ı� ---" + object + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// ���ҳ��title�����ж��Ƿ���ȷ
	public static void getTitle(String object, String data, String y) {

		try {
			Log.info("��ʼ�ж�ҳ��title�Ƿ���ȷ---");
			Boolean a = driver.getTitle().equals(data);
			System.out.println("������ַtitle�Ƿ���ȷ�� " + a);

		} catch (Exception e) {
			Log.error("�޷����ҳ��title " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// ͳ������Ļһ���ж��ٸ�ģ��
	public static void getMOduleNum(String object, String data, String y) {

		try {
			List<WebElement> allModule = driver.findElements(By.cssSelector(OR.getProperty(object)));
			moduletotalNum = allModule.size();
//			System.out.println(moduletotalNum);
			Log.info("�ɹ������Ļģ�������� " + moduletotalNum);
		} catch (Exception e) {
			Log.error("�޷������Ļģ������ " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// ����ģ�����ƻ�ȡģ������λ��
	public static void getHeaders(String object, String data, String y) {
		try {

			int RowsTotal = ExcelUtils.getRowCount(Constants.Sheet_TestSteps);
			for (int i = 0; i < RowsTotal; i++) {
				String modelName = ExcelUtils.getCellData(i,
						Constants.col_ModuleName, Constants.Sheet_TestSteps);
				for (int j = 0; j < moduletotalNum + 1; j++) {

					List<WebElement> HeadersText = driver.findElements(By
							.cssSelector(OR.getProperty(object)));
					String text = HeadersText.get(j).getText();
					if (text.contains(modelName) && modelName != "") {

						String a = Integer.toString(j);
						ExcelUtils.setCellData(a, i, Constants.Col_DataSet,
								Constants.Sheet_TestSteps);
					}

				}
			}
			Log.info("��ѯÿ��ģ�������");
		} catch (Exception e) {
			Log.error("�޷���ѯģ������ " + e.getMessage());
			DriverScript.bResult = false;
		}
	}

	// ģ�����˫��data����
	public static void doubleClickLocal(String object,String data,String y) throws InterruptedException{
		try {
			Actions drawPen = new Actions(driver);
			Thread.sleep(500);
			List<WebElement> li = driver.findElements(By.cssSelector(OR.getProperty(object)));
			for (WebElement i :li) {
				String local = i.getText(); 
//				System.out.println(local);
				if (local.equals(data)) {
					Thread.sleep(1000);
					drawPen.moveToElement(i).doubleClick().release().perform();
					break;
				}
			}
			Log.info("���˫��"+data);
		} catch (Exception e) {
			Log.error("�޷���ͣ���� " + e.getMessage());
			DriverScript.bResult = false;
		}

	}
	// ģ�������ͣ��data����
	public static void clickAndHoldLocal(String object,String data,String y) {
		try {
			Actions drawPen = new Actions(driver);
			Thread.sleep(500);
			List<WebElement> li = driver.findElements(By.cssSelector(OR.getProperty(object)));
			for (WebElement i :li) {
				String local = i.getText(); 
//				System.out.println(local);
				if (local.equals(data)) {
					Thread.sleep(500);
					drawPen.moveToElement(i).clickAndHold().perform();
					break;
				}
			}
			Log.info("�����ͣ����"+data);
		} catch (Exception e) {
			Log.error("�޷���ͣ���� " + e.getMessage());
			DriverScript.bResult = false;
		}
		
	}
	
	public static void getLocal(String local1,String local2 ,String local3) throws InterruptedException{
		
		Actions drawPen = new Actions(driver);
		WebElement element = driver.findElement(By.cssSelector(".app-header"));
		drawPen.moveToElement(element, 500, 1).doubleClick().perform();
		Thread.sleep(1000*2);
		driver.findElement(By.cssSelector(".area-selector>.selector-box")).click();
		Thread.sleep(1000);
		List<WebElement> li1 = driver.findElements(By.cssSelector(".area-list-container>ul>li"));
		for (WebElement i :li1) {
			String local = i.getText(); 
//			System.out.println(local);
			if (local.equals(local1)) {
				drawPen.moveToElement(i).clickAndHold().perform();
				List<WebElement> li2 = driver.findElements(By.cssSelector(".area-list-container>:nth-child(2)>li"));
				for (WebElement j :li2) {
//					System.out.println(j.getText());
					if (j.getText().equals(local2)) {						
						drawPen.moveToElement(j).click().perform();
						Thread.sleep(1000);
						List<WebElement> li3 = driver.findElements(By.cssSelector(".area-list-container>:nth-child(3)>li"));
						for (WebElement k : li3) {
							System.out.println(k.getText());
							Thread.sleep(1000);
							if (k.getText().equals(local3)) {
								drawPen.moveToElement(k).doubleClick().perform();	
								break;
							}
											
						}
						break;
					}
				}
				break;
			}
		}
	}

	// �ȴ�data��
	public static void waitFor(String object, String data, String y) {
		try {

			int time = Integer.parseInt(data);
			Log.info("�ȴ�" + time + "��");
			Thread.sleep(time * 1000);
		} catch (Exception e) {

			System.out.println(e);
			Log.error("�޷��ȴ� --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// data�ε�� ʹ��cssSelector��λ
	public static void manyClick(String object, String data, String y) {
		int a = Integer.parseInt(data);
		for (int i = 0; i < a; i++) {
			try {
				Log.info("���Ԫ�أ� " + object);
				driver.findElement(By.cssSelector(OR.getProperty(object)))
						.click();
				Thread.sleep(1000 * 2);
			} catch (Exception e) {
				Log.error("�޷����Ԫ�� --- " + e.getMessage());
				DriverScript.bResult = false;
			}

		}
	}

	// ��������б���ҳ��
	public static void getTotalPage(String object, String data, String y) {

		try {

			// �鿴һ���ж���ҳ
			Thread.sleep(1000 * 2);
			String page_all = driver.findElement(
					By.cssSelector(OR.getProperty(object))).getText();
			all = page_all.substring(2);
			System.out.println("������ҳ����" + all);
			Log.info("���������ҳ��" + all);
		} catch (Exception e) {
			Log.error("�޷���� " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// ���������б���ҳ��
	public static void jumpInput(String object, String data, String y) {
		try {
			driver.findElement(By.cssSelector(OR.getProperty(object))).clear();
			driver.findElement(By.cssSelector(OR.getProperty(object)))
					.sendKeys(all);

		} catch (Exception e) {
			Log.error("�޷���� " + e.getMessage());
			DriverScript.bResult = false;
		}

	}
	
	// ���ϵͳ��ǰʱ��
	public static String getDate(){
	
		//��õ�ǰ����
		Date date =new Date();
		SimpleDateFormat df = new SimpleDateFormat("yy.MM.dd");//�������ڸ�ʽ("yyyy-MM-dd HH:mm:ss")
        String Nowdate = df.format(date);
//		System.out.println("�������� �� "+Nowdate);
		return Nowdate;
	}
	
	public static String getName() {

		String city_name = driver.findElement(By.cssSelector(".marquee-line-text")).getText();
		String city = city_name.substring(0,city_name.lastIndexOf(" "));
//		System.out.println(city);
		return city;
		}
	// �Ե�ǰҳ����н�ͼ
	public static void screenShots(String object, String data, String y) {

		try {
			
			String NowDate=getDate();
			String city = getName();
			Thread.sleep(1000 * 2);
			File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(srcFile, new File("D:\\tj\\"+city+"\\"+NowDate+"\\"+data + ".png"));
		} catch (Exception e) {
			Log.error("�޷���ͼ" + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// �ر���������˳�
	public static void closeBrowser(String object, String data, String y) {
		try {
			Log.info("�رղ��˳������");
			driver.quit();
		} catch (Exception e) {
			Log.error("�޷��ر������ --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// ������������ģ��
	public static void intoModule(String object, String data, String y) {

		try {
			int a = Integer.parseInt(data);
			driver.findElement(
					By.cssSelector(".section-body>:nth-child(" + a + ")"))
					.click();
		} catch (Exception e) {
			Log.error("�޷�����ģ�� --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// ������һ��
	public static void backUp(String object, String data, String y) {
		try {
			int a = Integer.parseInt(data);
			Thread.sleep(1000 * 1);
			List<WebElement> back = driver.findElements(By.cssSelector(OR
					.getProperty(object)));
			back.get(a).click();
			Thread.sleep(1000 * 1);
		} catch (Exception e) {
			Log.error("�޷�������һ�� --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// �����Ԫ��ָ������㣨data,Y��
	public static void mouseXy(String object, String data, String y)
			throws Exception {

		try {
			Actions drawPen = new Actions(driver);
			// ��λԪ��
			WebElement element = driver.findElement(By.xpath(OR.getProperty(object)));
			int x = Integer.parseInt(data);
			int Y = Integer.parseInt(y);
			Thread.sleep(1000 * 1);
			drawPen.moveToElement(element, x, Y).doubleClick().perform();
			Log.info("�ɹ����" + x + ":" + y + "����");
		} catch (Exception e) {
			Log.error("�޷��������" + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// ���������ֲ���̽ʡ������ǰ������
	public static void demandDip(String object, String data, String y) {

		try {
			Thread.sleep(1000 * 1);
			driver.findElement(
					By.cssSelector(".content-wrap>.list-wrap>.common-list>:nth-child("
							+ data + ")")).click();
			Log.info("��ʼ��̽���� ----");
		} catch (Exception e) {

			Log.error("�޷���̽���� --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// ��webElement���н�ͼ
	public static BufferedImage createElementImage(WebElement webElement,
			String y) throws IOException {
		// ���webElement��λ�úʹ�С��
		Point location = webElement.getLocation();
		Dimension size = webElement.getSize();
		// ����ȫ����ͼ��
		BufferedImage originalImage = ImageIO.read(takeScreenshot());
		// ��ȡwebElement����λ�õ���ͼ��
		BufferedImage croppedImage = originalImage.getSubimage(location.getX(),
				location.getY(), size.getWidth(), size.getHeight());
		return croppedImage;
	}

	public static File takeScreenshot() throws IOException {
		TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
		File screenshotAs = takesScreenshot.getScreenshotAs(OutputType.FILE);
		return screenshotAs;
	}

	// ����frame��
	public static void iframe(String object, String data, String y) {

		try {
			WebElement iframe = driver.findElement(By.xpath(OR
					.getProperty(object)));
			driver.switchTo().frame(iframe);
			Log.info("�ɹ�����iframe --- ");
		} catch (Exception e) {
			Log.error("�޷�����frame��--- " + e.getMessage());
			DriverScript.bResult = false;
		}
	}

	// ���д����л�
	public static void windowSwitch(String object, String data, String y) {

		try {
			// �л����
			// ��ȡ��ǰҳ����
			Thread.sleep(1000);
			String handle = driver.getWindowHandle();
			for (String temhandle : driver.getWindowHandles()) {

				if (!temhandle.equals(handle)) {

					driver.switchTo().window(temhandle);
					Thread.sleep(1000);
					Log.info("�ɹ����д����л���");
					// try {
					// title = driver.getTitle();
					//
					// } catch (Exception e) {
					// title = "null";
					// }
					// �ر��л���Ĵ���
					// driver.close();
					// }
					// �л���ԭ���Ĵ���
					// driver.switchTo().window(handle);
				}
			}
		} catch (Exception e) {
			Log.error("�޷��л����� --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// �´�һ�����ڷ���data��ַ
	public static void newOpenUrl(String object, String data, String y) {
		try {
			// ��ǰ�������
			JavascriptExecutor oJavaScriptExecutor = (JavascriptExecutor) driver;
			oJavaScriptExecutor.executeScript("window.open( 'http://" + data
					+ "' );");
		} catch (Exception e) {
			Log.error("�޷��´�һ������--- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// �����ͣ��ָ��Ԫ����
	public static void mouseHover(String object, String data, String y) {
		Actions drawPen = new Actions(driver);
		WebElement element = driver.findElement(By.xpath(OR.getProperty(object)));
		drawPen.moveToElement(element).clickAndHold().perform();

	}

	// ��굥��ָ��Ԫ��
	public static void mouseClick(String object, String data, String y) {
		Actions drawPen = new Actions(driver);
		WebElement element = driver.findElement(By.xpath(OR.getProperty(object)));
		drawPen.moveToElement(element).click().release().perform();

	}

	// ���˫��ָ��Ԫ��
	public static void mouseDoubleClick(String object, String data, String y) {
		Actions drawPen = new Actions(driver);
		WebElement element = driver.findElement(By.xpath(OR.getProperty(object)));
		drawPen.moveToElement(element).doubleClick().perform();

	}

}
