package com.vinteque.automations;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class LinkedInMessagingAutomation {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final int DEFAULT_WAIT_TIME = 20;
    private HumanBehaviorSimulator simulate;

    public LinkedInMessagingAutomation() {
        initializeDriver();
    }

    private void initializeDriver() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-data-dir=C:\\automation-profile");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIME));
        simulate = new HumanBehaviorSimulator();

        System.out.println("✓ Chrome initialized");
    }

    public void accessTest(){
        driver.get("https://linkedin.com/feed");
    }

    public void login(String email, String password) {
        try {
            driver.get("https://www.linkedin.com/login");

            WebElement emailField = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("username"))
            );

            WebElement passwordField = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

            simulate.simulateDecisionTime();
            emailField.sendKeys(email);
//            simulate.typeLikeHuman(emailField, email);

            simulate.simulateDecisionTime();
            passwordField.sendKeys(password);
//            simulate.typeLikeHuman(passwordField, password);

            simulate.simulateDecisionTime();
            loginButton.click();

            System.out.println("Complete verification if required and press ENTER...");
            new Scanner(System.in).nextLine();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void navigateToSearchResults(int startPage) {
        driver.get("https://www.linkedin.com/search/results/people/?keywords=executive%20director&origin=FACETED_SEARCH&network=%5B%22F%22%5D&page="+startPage+"&spellCorrectionEnabled=true");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div[role='list']")
        ));

        try {
            simulate.randomDelayWithVariance(2500, 40);
        } catch (InterruptedException ignored) {}
    }

    public void processAllConnections(String message, int startPage, int maxPages) {
        int currentPage = startPage;

        while (currentPage <= maxPages) {
            System.out.println("Processing Page " + currentPage);

            try {
                simulate.randomDelayWithVariance(3000, 40);
                processConnectionsOnCurrentPage(message);

                if (currentPage < maxPages && goToNextPage()) {
                    currentPage++;
                } else {
                    break;
                }

            } catch (Exception e) {
                break;
            }
        }

        System.out.println("Automation Complete");
    }

    private void processConnectionsOnCurrentPage(String message) {

        List<WebElement> connectionCards = driver.findElements(
                By.cssSelector("div[data-view-name='people-search-result']")
        );

        for (int i = 0; i < connectionCards.size(); i++) {

            try {
                WebElement card = connectionCards.get(i);

                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({behavior: 'smooth',block: 'center'});", card
                );

                simulate.simulateDecisionTime();
                simulate.simulateReadingTime(card.getText());

                String personName = getPersonName(card);

                String personalizedMessage =
                        "Hello " + personName.split(" ")[0] + ",\n" + message;

                if (!sendMessageToConnection(card, personalizedMessage, personName)) {
                    continue;
                }

                simulate.randomDelayWithOccasionalPause(
                        2000, 4000,
                        8000, 15000,
                        0.15
                );

            } catch (Exception ignored) {}
        }
    }

    private String getPersonName(WebElement card) {
        try {
            return card.findElement(
                    By.cssSelector("a[data-view-name='search-result-lockup-title']")
            ).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private boolean sendMessageToConnection(WebElement card,
                                            String message,
                                            String personName) {

        try {
            WebElement messageButton = card.findElement(
                    By.cssSelector("a[aria-label='Message']")
            );

            simulate.simulateDecisionTime();
            messageButton.click();

            simulate.randomDelayWithVariance(4000, 50);

            WebElement popupHost = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("#interop-outlet"))
            );

            SearchContext shadowRoot = popupHost.getShadowRoot();

            WebElement messagePopup =
                    shadowRoot.findElement(By.cssSelector("div[role='dialog']"));

//            check if there are previous conversations
            if(priorConversation(messagePopup)){
                IO.println("skipping ... "+personName+", there are prior conversations");
                return closePopup(messagePopup);
            }

            WebElement messageBox =
                    messagePopup.findElement(By.cssSelector("div[role='textbox']"));

            simulate.simulateDecisionTime();
            messageBox.click();

            messageBox.sendKeys(message);
//            simulate.typeLikeHuman(messageBox, message);

            simulate.randomDelay(800, 2000);

            WebElement sendButton =
                    null;
            try {
                sendButton = messagePopup.findElement(By.cssSelector("footer button[type='submit']"));
            } catch (Exception e) {
                System.err.println("Send button not found: " + e.getMessage());
                return false;
            }

            simulate.simulateDecisionTime();
            sendButton.click();

            simulate.randomDelayWithVariance(2000, 50);



            return closePopup(messagePopup);

        } catch (Exception e) {
            return false;
        }
    }

    private boolean closePopup(WebElement messagePopup) throws InterruptedException {
        try {
            WebElement closeButton =
                    messagePopup.findElement(By.cssSelector("header")).findElement(By.cssSelector("button:has(svg[data-test-icon='close-small'])"));
            closeButton.click();
            return true;
        } catch (Exception e) {
            simulate.simulateDecisionTime();
            IO.println("proceeding to retry closing the popup");
            try {
                WebElement closeButton =
                        messagePopup.findElement(By.cssSelector("header")).findElement(By.cssSelector("button:has(svg[data-test-icon='close-small'])"));
                closeButton.click();
                return true;
            } catch (Exception ex) {
                System.err.println("Failed to close message popup: " + ex.getMessage());
                return false;
            }
        }
    }

    private boolean priorConversation(WebElement popUp) {
        try {
            popUp.findElement(By.cssSelector("li.msg-s-message-list__event"));
            IO.println("---Prior conversation found");
//            TODO if found, whan was it last, if very old talk, if not ignore and add ai to proceed with follow up
            return true;
        } catch (Exception e) {
            IO.println("---No prior conversation");
            return false;
        }
    }

    private boolean goToNextPage() {
        try {
            WebElement nextButton = driver.findElement(
                    By.cssSelector("button[data-testid='pagination-controls-next-button-visible']")
            );

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", nextButton
            );

            simulate.simulateDecisionTime();
            nextButton.click();

            simulate.randomDelayWithVariance(3000, 40);

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("div[role='list']")
            ));

            return true;

        } catch (Exception e) {
            return false;
        }
    }



    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
