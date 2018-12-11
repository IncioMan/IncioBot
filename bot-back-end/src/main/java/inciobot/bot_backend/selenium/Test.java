package inciobot.bot_backend.selenium;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import inciobot.bot_backend.model.PhotoLink;

public class Test {
	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		System.setProperty("webdriver.chrome.driver", new File("").getAbsolutePath() + "\\chromedriver.exe");

		System.out.println(new File("").getAbsolutePath() + "\\chromedriver.exe");
		Test.getPicture();
	}

	public static PhotoLink getPicture() throws IOException {
		System.out.println(Test.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		System.setProperty("webdriver.chrome.driver", new File("").getAbsolutePath() + "/chromedriver.exe");
		PhotoLink photoLink = new PhotoLink();

		WebDriver driver = new ChromeDriver();

		driver.get("http://www.ordnet.dk/ddo");
		WebElement ele = driver.findElement(By.className("dagensord"));
		// Get entire page screenshot
		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		BufferedImage fullImg = ImageIO.read(screenshot);
		// Get the location of element on the page
		Point point = ele.getLocation();

		System.out.println(ele.getText());
		// Get width and height of the element
		int eleWidth = ele.getSize().getWidth();
		int eleHeight = ele.getSize().getHeight();
		// Crop the entire page screenshot to get only element screenshot
		BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
		ImageIO.write(eleScreenshot, "png", screenshot);
		// Copy the element screenshot to disk
		File screenshotLocation = new File("C:\\Users\\alex.incerti\\Pictures\\dagensord.png");
		FileUtils.copyFile(screenshot, screenshotLocation);

		String link = driver.findElement(By.xpath("//*[@id=\"content\"]/div[3]/div[2]/div[1]/div/div[3]/a"))
				.getAttribute("href");

		photoLink.setFile(screenshotLocation);
		photoLink.setLink(link);
		driver.quit();

		return photoLink;
	}
}
