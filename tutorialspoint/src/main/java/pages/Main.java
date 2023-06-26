package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        /*Create an interaction with a website where the URL
        tutorialspoint.com/html/html_iframes.htm is loaded and the program navigates
        into the page below “Document content goes here…“, interacts with the “About us”
        link,then returns the URL of the new page, a list of all URLs on the page, a list of all
        buttons on the page, a list of all text input fields on the page,then creates an
        account and logs into the site. The output should be both via console and output to
        the appended file.*/

        //WebDriver setup for chrome
        System.setProperty("webdriver.chrome.driver", "tutorialspoint/src/main/resources/chromedriver.exe");

        // Create an instance of ChromeDriver
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Navigate to tutorialspoint website
        driver.get("https://www.tutorialspoint.com/html/html_iframes.htm");

        //click agree button
        driver.findElement(By.xpath("//a[text()='Agree']")).click();

        //switch to iframes
        WebElement iframe_Result = driver.findElement(By.xpath("//iframe[@class='result']"));
        driver.switchTo().frame(iframe_Result);
        WebElement iframe_Result_2 = driver.findElement(By.xpath("//iframe[@src='/html/menu.htm']"));
        scrollToElement(driver, iframe_Result_2);
        driver.switchTo().frame(iframe_Result_2);
        sleep(1000);

        //click agree button inside iframes
        driver.findElement(By.xpath("//a[text()='Agree']")).click();

        //click About Us
        WebElement link_AboutUs = driver.findElement(By.xpath("//a[@href = '/about/index.htm']"));
        scrollToBottom(driver);
        link_AboutUs.click();

        //switch to new page iframe
        driver.switchTo().defaultContent();
        driver.switchTo().frame(iframe_Result);
        WebElement iframe_Result_3 = driver.findElement(By.xpath("//iframe[@src='/html/menu.htm']"));
        driver.switchTo().frame(iframe_Result_3);

        //prints url of the new page
        System.out.println("Page URL: " + driver.getCurrentUrl());

        //prints all a, button and input text elements and appends it to output.txt file
        traverseElements(driver);
        appendToFile("------------------------------------------------");

        //clicks on login button
        driver.switchTo().defaultContent();
        WebElement btn_login = driver.findElement(By.xpath("//a[@href = 'https://www.tutorialspoint.com/market/login.asp']"));
        btn_login.click();

        //clicks on sign up button
        WebElement btn_signUp = driver.findElement(By.xpath("//a[@href = 'signup.jsp']"));
        scrollToElement(driver, btn_signUp);
        btn_signUp.click();

        //fills sign up form
        driver.findElement(By.id("textRegName")).sendKeys("Test user");
        driver.findElement(By.id("phone")).sendKeys("1234567890");
        driver.findElement(By.id("textSRegEmail")).sendKeys("test@test.com");
        driver.findElement(By.id("user_password")).sendKeys("testpassword1");

        //Here I would create a logic to login if creating an account didnt require a verification step.

        //Quit driver
        sleep(3000);
        driver.quit();
    }

    public static void appendToFile(String text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
            writer.write(text);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }


    public static void traverseElements(WebDriver driver) {
        //Find all 'a', 'button', and 'input' text elements on the current page
        List<WebElement> elements = driver.findElements(By.cssSelector("a, button, input[type='text']"));

        //Loop through each element and print the text
        for (WebElement element : elements) {
            String elementText = element.getText() != null && !element.getText().isEmpty() ? ", Text:"+ element.getText() : "";
            String elementHref = element.getAttribute("href") != null && !element.getAttribute("href").isEmpty() ? ", href:"+ element.getAttribute("href") : "";
            String elementType = element.getAttribute("type") != null && !element.getAttribute("type").isEmpty() ? ", type:"+ element.getAttribute("type") : "";
            String elementDetail = "Element tag: " + element.getTagName() + elementText + elementHref + elementType;
            System.out.println(elementDetail);
            appendToFile(elementDetail);
        }

        //Find all iframes on the current page
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));

        //Loop through each iframe and switch to it
        for (WebElement iframe : iframes) {
            driver.switchTo().frame(iframe);

            // Call the recursive method to traverse the elements inside the iframe
            traverseElements(driver);

            // Switch back to the parent frame or the main page
            driver.switchTo().parentFrame();
        }
    }

    private static void scrollToElement(WebDriver driver, WebElement element) throws InterruptedException {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
        sleep(1000);
    }

    private static void scrollToBottom(WebDriver driver) throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        sleep(1000);
    }


}
