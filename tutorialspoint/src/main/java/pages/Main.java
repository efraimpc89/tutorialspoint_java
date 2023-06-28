package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--start-maximized");
        ChromeDriver driver = new ChromeDriver(options);

        //Create a WebDriverWait instance with a timeout of 10 seconds
        WebDriverWait wait = new WebDriverWait(driver,  Duration.ofSeconds(10));

        //Navigate to tutorialspoint website
        driver.get("https://www.tutorialspoint.com/html/html_iframes.htm");

        //Click agree button on popup
        driver.findElement(By.xpath("//a[text()='Agree']")).click();

        //Switch to corresponding iframe
        WebElement iframe_Result = driver.findElement(By.xpath("//iframe[@class='result']"));
        driver.switchTo().frame(iframe_Result);
        WebElement iframe_Result_2 = driver.findElement(By.xpath("//iframe[@src='/html/menu.htm']"));
        scrollToElement(driver, iframe_Result_2);
        driver.switchTo().frame(iframe_Result_2);

        //Click agree button inside iframes
        WebElement btnAgreeInIframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[text()='Agree']")));
        btnAgreeInIframe.click();

        //Click About Us inside iframe
        WebElement link_AboutUs = driver.findElement(By.xpath("//a[@href = '/about/index.htm']"));
        scrollToBottom(driver);
        link_AboutUs.click();

        //Switch to new page iframe
        driver.switchTo().defaultContent();
        driver.switchTo().frame(iframe_Result);
        WebElement iframe_Result_3 = driver.findElement(By.xpath("//iframe[@src='/html/menu.htm']"));
        driver.switchTo().frame(iframe_Result_3);

        //Prints url of the new page
        System.out.println("Page URL: " + driver.getCurrentUrl());

        //Prints all a, button and input text elements and appends it to output.txt file
        traverseElements(driver);

        //Clicks on login button on default content
        driver.switchTo().defaultContent();
        By by_btnLogin = By.xpath("//a[@href = 'https://www.tutorialspoint.com/market/login.asp']");
        WebElement btnLogin = driver.findElement(by_btnLogin);
        btnLogin.click();

        //clicks on sign up button
        WebElement btn_signUp = driver.findElement(By.xpath("//a[@href = 'signup.jsp']"));
        scrollToElement(driver, btn_signUp);
        btn_signUp.click();

        //Go to https://www.fakemail.net/ to use it for signing up
        driver.executeScript("window.open()");
        Set<String> handles = driver.getWindowHandles();
        String tutorialsPointTab = driver.getWindowHandle();
        handles.remove(tutorialsPointTab);
        String fakeEmailTab = handles.iterator().next();
        driver.switchTo().window(fakeEmailTab);
        driver.get("https://www.fakemail.net/");

        //Copy the new random email
        String testEmail = driver.findElement(By.id("email")).getText();

        //Switch back to tutorialspoint.com
        driver.switchTo().window(tutorialsPointTab);

        //Fills sign up form
        String testPassword = "testpassword1";
        driver.findElement(By.id("textRegName")).sendKeys("Test user");
        driver.findElement(By.id("phone")).sendKeys(generateRandomPhoneNumber());
        Select countrySelector = new Select(driver.findElement(By.id("country_code")));
        countrySelector.selectByValue("52");
        driver.findElement(By.id("textSRegEmail")).sendKeys(testEmail);
        driver.findElement(By.id("user_password")).sendKeys(testPassword);
        driver.findElement(By.id("validate_email_id")).click();

        //Append email and password used to file
        appendToFile("user_email: " +testEmail);
        appendToFile("user_password: " +testPassword);

        //Go back to the fake email tab
        driver.switchTo().window(fakeEmailTab);

        //Open the confirmation email
        String otpEmailSelector = "//td[text()='Signup One Time Password (OTP)']";
        WebElement otpEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(otpEmailSelector)));
        otpEmail.click();

        //Copy the registration One time password code
        WebElement iframeConfirmationEmail = driver.findElement(By.id("iframeMail"));
        driver.switchTo().frame(iframeConfirmationEmail);
        String confirmationOTP = driver.findElement(By.cssSelector("p[style*='color:#fff;']")).getText();

        //Output the confirmation OTP
        System.out.println("OTP: " + confirmationOTP);
        appendToFile("Fake email OTP: " +testEmail);

        //Switch back to tutorialspoint.com and validates email
        driver.switchTo().window(tutorialsPointTab);
        driver.findElement(By.id("txtEmailValidateOTP")).sendKeys(confirmationOTP);
        driver.findElement(By.id("validateEmailOtp")).click();
        WebElement btnSignUp = wait.until(ExpectedConditions.elementToBeClickable(By.id("signUpNew")));
        btnSignUp.click();

        //Logout
        WebElement btnSkipMobileOTP = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("skipMobileOtp")));
        btnSkipMobileOTP.click();
        WebElement btnProfileImage = wait.until(ExpectedConditions.elementToBeClickable(By.id("profileImage")));
        btnProfileImage.click();
        driver.findElement(By.cssSelector("a.logout")).click();

        //Login again with new account
        btnLogin = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.btn[href*='login']")));
        btnLogin.click();
        driver.findElement(By.id("user_email")).sendKeys(testEmail);
        driver.findElement(By.id("user_password")).sendKeys(testPassword);
        WebElement btnFormLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("user_login")));
        sleep(1000); //there is a delay in login button after password is set
        btnFormLogin.click();

        //Append something to separate executions
        appendToFile("------------------------------------------------");

        //Quit driver
        sleep(5000); //Enough time to check what's on screen before it closes
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

    public static String generateRandomPhoneNumber() {
        Random random = new Random();
        StringBuilder phoneNumberBuilder = new StringBuilder();

        //Ensure the first digit is not zero
        int firstDigit = random.nextInt(9) + 1;
        phoneNumberBuilder.append(firstDigit);

        //Generate the remaining 9 digits
        for (int i = 0; i < 9; i++) {
            int digit = random.nextInt(10);
            phoneNumberBuilder.append(digit);
        }
        return phoneNumberBuilder.toString();
    }

}
