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

	// 打开浏览器
	public static void openBrowser(String object, String data, String y) {
		// 讓瀏覽器後台運行

		try {
			ChromeOptions option = new ChromeOptions();
			// option.addArguments("headless"); //瀏覽器後台運行
			option.addArguments("disable-infobars");// 取消提示
			// 导入Chrome驱动
			System.setProperty("webdriver.chrome.driver",
					"D:\\chromedriver.exe");
			driver = new ChromeDriver(option);
			Log.info("启动Chrome浏览器!");
		} catch (Exception e) {
			Log.error("无法启动浏览器 --- " + e.getMessage());
			DriverScript.bResult = false;

		}

	}

	// 打开测试网址
	public static void openUrl(String object, String data, String y) {

		try {
			Log.info("浏览器窗口最大化！");
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			Log.info("打开测试服务器地址！");
			driver.get(data);
		} catch (Exception e) {
			Log.error("无法打开测试环境地址 --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}
	// 刷新页面
	public static void refresh(String object,String data,String y){
		driver.navigate().refresh();
	}
	// 点击页面元素
	public static void click(String object, String data, String y) {
		try {
			Log.info("点击元素： " + object);
			driver.findElement(By.xpath(OR.getProperty(object))).click();
		} catch (Exception e) {
			Log.error("无法点击元素 --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 在输入框输入内容
	public static void input(String object, String data, String y) {
		try {
			Log.info("开始在 " + object + "输入文本");
			Thread.sleep(1000 * 1);
			driver.findElement(By.xpath(OR.getProperty(object))).sendKeys(data);
		} catch (Exception e) {
			Log.error("无法输入文本 ---" + object + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 获得页面title，并判断是否正确
	public static void getTitle(String object, String data, String y) {

		try {
			Log.info("开始判断页面title是否正确---");
			Boolean a = driver.getTitle().equals(data);
			System.out.println("测试网址title是否正确： " + a);

		} catch (Exception e) {
			Log.error("无法获得页面title " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 统计主屏幕一共有多少个模块
	public static void getMOduleNum(String object, String data, String y) {

		try {
			List<WebElement> allModule = driver.findElements(By.cssSelector(OR.getProperty(object)));
			moduletotalNum = allModule.size();
//			System.out.println(moduletotalNum);
			Log.info("成功获得屏幕模块总数： " + moduletotalNum);
		} catch (Exception e) {
			Log.error("无法获得屏幕模块总数 " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 根据模块名称获取模块所在位置
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
			Log.info("查询每个模块的名称");
		} catch (Exception e) {
			Log.error("无法查询模板名称 " + e.getMessage());
			DriverScript.bResult = false;
		}
	}

	// 模拟鼠标双击data地域
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
			Log.info("鼠标双击"+data);
		} catch (Exception e) {
			Log.error("无法悬停地域 " + e.getMessage());
			DriverScript.bResult = false;
		}

	}
	// 模拟鼠标悬停到data地域
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
			Log.info("鼠标悬停地域"+data);
		} catch (Exception e) {
			Log.error("无法悬停地域 " + e.getMessage());
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

	// 等待data秒
	public static void waitFor(String object, String data, String y) {
		try {

			int time = Integer.parseInt(data);
			Log.info("等待" + time + "秒");
			Thread.sleep(time * 1000);
		} catch (Exception e) {

			System.out.println(e);
			Log.error("无法等待 --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// data次点击 使用cssSelector定位
	public static void manyClick(String object, String data, String y) {
		int a = Integer.parseInt(data);
		for (int i = 0; i < a; i++) {
			try {
				Log.info("点击元素： " + object);
				driver.findElement(By.cssSelector(OR.getProperty(object)))
						.click();
				Thread.sleep(1000 * 2);
			} catch (Exception e) {
				Log.error("无法点击元素 --- " + e.getMessage());
				DriverScript.bResult = false;
			}

		}
	}

	// 获得网商列表总页数
	public static void getTotalPage(String object, String data, String y) {

		try {

			// 查看一共有多少页
			Thread.sleep(1000 * 2);
			String page_all = driver.findElement(
					By.cssSelector(OR.getProperty(object))).getText();
			all = page_all.substring(2);
			System.out.println("网商总页数：" + all);
			Log.info("获得网商总页数" + all);
		} catch (Exception e) {
			Log.error("无法获得 " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 输入网商列表总页数
	public static void jumpInput(String object, String data, String y) {
		try {
			driver.findElement(By.cssSelector(OR.getProperty(object))).clear();
			driver.findElement(By.cssSelector(OR.getProperty(object)))
					.sendKeys(all);

		} catch (Exception e) {
			Log.error("无法获得 " + e.getMessage());
			DriverScript.bResult = false;
		}

	}
	
	// 获得系统当前时间
	public static String getDate(){
	
		//获得当前日期
		Date date =new Date();
		SimpleDateFormat df = new SimpleDateFormat("yy.MM.dd");//设置日期格式("yyyy-MM-dd HH:mm:ss")
        String Nowdate = df.format(date);
//		System.out.println("当天日期 ： "+Nowdate);
		return Nowdate;
	}
	
	public static String getName() {

		String city_name = driver.findElement(By.cssSelector(".marquee-line-text")).getText();
		String city = city_name.substring(0,city_name.lastIndexOf(" "));
//		System.out.println(city);
		return city;
		}
	// 对当前页面进行截图
	public static void screenShots(String object, String data, String y) {

		try {
			
			String NowDate=getDate();
			String city = getName();
			Thread.sleep(1000 * 2);
			File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(srcFile, new File("D:\\tj\\"+city+"\\"+NowDate+"\\"+data + ".png"));
		} catch (Exception e) {
			Log.error("无法截图" + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 关闭浏览器并退出
	public static void closeBrowser(String object, String data, String y) {
		try {
			Log.info("关闭并退出浏览器");
			driver.quit();
		} catch (Exception e) {
			Log.error("无法关闭浏览器 --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 主界面进入各个模块
	public static void intoModule(String object, String data, String y) {

		try {
			int a = Integer.parseInt(data);
			driver.findElement(
					By.cssSelector(".section-body>:nth-child(" + a + ")"))
					.click();
		} catch (Exception e) {
			Log.error("无法进入模块 --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 返回上一级
	public static void backUp(String object, String data, String y) {
		try {
			int a = Integer.parseInt(data);
			Thread.sleep(1000 * 1);
			List<WebElement> back = driver.findElements(By.cssSelector(OR
					.getProperty(object)));
			back.get(a).click();
			Thread.sleep(1000 * 1);
		} catch (Exception e) {
			Log.error("无法返回上一级 --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 鼠标点击元素指定坐标点（data,Y）
	public static void mouseXy(String object, String data, String y)
			throws Exception {

		try {
			Actions drawPen = new Actions(driver);
			// 定位元素
			WebElement element = driver.findElement(By.xpath(OR.getProperty(object)));
			int x = Integer.parseInt(data);
			int Y = Integer.parseInt(y);
			Thread.sleep(1000 * 1);
			drawPen.moveToElement(element, x, Y).doubleClick().perform();
			Log.info("成功点击" + x + ":" + y + "坐标");
		} catch (Exception e) {
			Log.error("无法点击坐标" + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 需求热力分布下探省级地域（前五名）
	public static void demandDip(String object, String data, String y) {

		try {
			Thread.sleep(1000 * 1);
			driver.findElement(
					By.cssSelector(".content-wrap>.list-wrap>.common-list>:nth-child("
							+ data + ")")).click();
			Log.info("开始下探地域 ----");
		} catch (Exception e) {

			Log.error("无法下探地域 --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 对webElement进行截图
	public static BufferedImage createElementImage(WebElement webElement,
			String y) throws IOException {
		// 获得webElement的位置和大小。
		Point location = webElement.getLocation();
		Dimension size = webElement.getSize();
		// 创建全屏截图。
		BufferedImage originalImage = ImageIO.read(takeScreenshot());
		// 截取webElement所在位置的子图。
		BufferedImage croppedImage = originalImage.getSubimage(location.getX(),
				location.getY(), size.getWidth(), size.getHeight());
		return croppedImage;
	}

	public static File takeScreenshot() throws IOException {
		TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
		File screenshotAs = takesScreenshot.getScreenshotAs(OutputType.FILE);
		return screenshotAs;
	}

	// 进入frame表单
	public static void iframe(String object, String data, String y) {

		try {
			WebElement iframe = driver.findElement(By.xpath(OR
					.getProperty(object)));
			driver.switchTo().frame(iframe);
			Log.info("成功进入iframe --- ");
		} catch (Exception e) {
			Log.error("无法进入frame表单--- " + e.getMessage());
			DriverScript.bResult = false;
		}
	}

	// 进行窗口切换
	public static void windowSwitch(String object, String data, String y) {

		try {
			// 切换句柄
			// 获取当前页面句柄
			Thread.sleep(1000);
			String handle = driver.getWindowHandle();
			for (String temhandle : driver.getWindowHandles()) {

				if (!temhandle.equals(handle)) {

					driver.switchTo().window(temhandle);
					Thread.sleep(1000);
					Log.info("成功进行窗口切换！");
					// try {
					// title = driver.getTitle();
					//
					// } catch (Exception e) {
					// title = "null";
					// }
					// 关闭切换后的窗口
					// driver.close();
					// }
					// 切换到原来的窗口
					// driver.switchTo().window(handle);
				}
			}
		} catch (Exception e) {
			Log.error("无法切换窗口 --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 新打开一个窗口访问data网址
	public static void newOpenUrl(String object, String data, String y) {
		try {
			// 打开前端浏览器
			JavascriptExecutor oJavaScriptExecutor = (JavascriptExecutor) driver;
			oJavaScriptExecutor.executeScript("window.open( 'http://" + data
					+ "' );");
		} catch (Exception e) {
			Log.error("无法新打开一个窗口--- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 鼠标悬停在指定元素上
	public static void mouseHover(String object, String data, String y) {
		Actions drawPen = new Actions(driver);
		WebElement element = driver.findElement(By.xpath(OR.getProperty(object)));
		drawPen.moveToElement(element).clickAndHold().perform();

	}

	// 鼠标单击指定元素
	public static void mouseClick(String object, String data, String y) {
		Actions drawPen = new Actions(driver);
		WebElement element = driver.findElement(By.xpath(OR.getProperty(object)));
		drawPen.moveToElement(element).click().release().perform();

	}

	// 鼠标双击指定元素
	public static void mouseDoubleClick(String object, String data, String y) {
		Actions drawPen = new Actions(driver);
		WebElement element = driver.findElement(By.xpath(OR.getProperty(object)));
		drawPen.moveToElement(element).doubleClick().perform();

	}

}
