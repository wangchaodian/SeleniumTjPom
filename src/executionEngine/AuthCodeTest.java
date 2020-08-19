package executionEngine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class AuthCodeTest {

	public static WebDriver driver;

	public static void main(String[] args) throws InterruptedException,
			IOException {
		ChromeOptions option = new ChromeOptions();
		// option.addArguments("headless"); //瀏覽器後台運行

		//
		option.setExperimentalOption("excludeSwitches",
				new String[] { "enable-automation" });
		// 导入Chrome驱动
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
		driver = new ChromeDriver(option);
		driver.manage().window().maximize();
		// 隐式时间等待（定位对象）
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		// 页面加载时的超时等待（）
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		driver.get("http://192.168.1.222:8764/countryBack/success");
		Thread.sleep(2000);
		driver.findElement(By.id("username")).sendKeys("wangchao");
		Thread.sleep(500);
		driver.findElement(By.id("password")).sendKeys("Qq123456");

		BufferedImage createElementImage = createElementImage(driver
				.findElement(By.id("safecode")));
		ImageIO.write(createElementImage, "png", new File("d:\\img\\code.png"));
	}

	// 对webElement进行截图
	public static BufferedImage createElementImage(WebElement webElement)
			throws IOException {
		// 获得webElement的位置和大小。
		Point location = webElement.getLocation();
		Dimension size = webElement.getSize();
		// 创建全屏截图
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

}
