package dmit2015.faces;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verify your Apache Maven includes the following dependencies:
 *
 * <dependency>
 * <groupId>org.seleniumhq.selenium</groupId>
 * <artifactId>selenium-java</artifactId>
 * <version>4.35.0</version>
 * <scope>test</scope>
 * </dependency>
 * <dependency>
 * <groupId>io.github.bonigarcia</groupId>
 * <artifactId>webdrivermanager</artifactId>
 * <version>6.3.1</version>
 * <scope>test</scope>
 * </dependency>
 * <dependency>
 * <groupId>io.github.bonigarcia</groupId>
 * <artifactId>selenium-jupiter</artifactId>
 * <version>6.3.0</version>
 * <scope>test</scope>
 * </dependency>
 *
 * <dependency>
 * <groupId>org.assertj</groupId>
 * <artifactId>assertj-core</artifactId>
 * <version>3.27.6</version>
 * <scope>test</scope>
 * </dependency>
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskCrudPageSeleniumIT {

    private static WebDriver driver;

    private static JavascriptExecutor js;

    static List<String> sharedEditIds = new ArrayList<>();

    private static final String primeFacesDataTableId = "form:dt-Tasks";

    @BeforeAll
    static void beforeAllTests() {
        // You can download the latest version of Selenium Manager from https://github.com/titusfortner/selenium_manager_debug/releases
        // and then use the command `chmod u+x ~/Downloads/selenium-manager-linux` to make the file executable.
        // Use the command `~/Downloads/selenium-manager-linux --browser chrome` to download the webdriver for Chrome browser
        // Use the command `~/Downloads/selenium-manager-linux --browser firefox` to download the webdriver for Firefox browser
        // Uncomment statement below to specify the location of the webdriver file.
//        System.setProperty("webdriver.chrome.driver", "/home/user2015/.cache/selenium/chromedriver/linux64/137.0.7151.70/chromedriver");
//        System.setProperty("webdriver.gecko.driver", "/snap/bin/geckodriver");

        WebDriverManager
                .chromedriver()
                .setup();

        var chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--headless=new");              // Chrome 109+ modern headless
        chromeOptions.addArguments("--window-size=1366,900");      // important for consistent layout
        driver = new ChromeDriver(chromeOptions);

//        WebDriverManager.firefoxdriver().setup();
//        FirefoxOptions ffOptions = new FirefoxOptions();
////        ffOptions.addArguments("-headless");
//        ffOptions.addArguments("--width=1366", "--height=900");
//        driver = new FirefoxDriver(ffOptions);

        js = (JavascriptExecutor) driver;
    }

    @AfterAll
    static void afterAllTests() {
        driver.quit();
    }

    @BeforeEach
    void beforeEachTestMethod() {

    }

    @AfterEach
    void afterEachTestMethod() throws InterruptedException {
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        Thread.sleep(1000);
    }

    private void setTextValue(String fieldId, String fieldValue) {
        WebElement element = driver.findElement(By.id(fieldId));
        element.clear();
        element.sendKeys(fieldValue);
    }

    private void setPrimeFacesDatePickerValue(String fieldId, String fieldValue) {
        // The text field for the p:datePicker component has a suffix of "_input" appended to the end of the field id.
        final String datePickerTextFieldId = String.format("%s_input", fieldId);
        WebElement element = driver.findElement(By.id(datePickerTextFieldId));

        element.sendKeys(Keys.chord(Keys.ESCAPE));

        element.sendKeys(fieldValue);
        element.sendKeys(Keys.chord(Keys.TAB));
    }

    private void setPrimeFacesSelectOneMenuValue(String fieldId, String fieldValue) {
        // The id of the element to click for p:selectOneMenu has a suffix of "_label" appended to the id of the p:selectOneMenu
        String selectOneMenuId = String.format("%s_label", fieldId);
        driver.findElement(By.id(selectOneMenuId)).click();
        // Wait until 3 seconds for select items to pop up
        var waitSelectOneMenu = new WebDriverWait(driver, Duration.ofSeconds(3));
        // The id of the items for p:selectOneMenu has a suffix of "_items" appended to the id of the p:selectOneMenu
        String selectOneMenuItemsId = String.format("%s_items", fieldId);
        var selectOneMenuItems = waitSelectOneMenu.until(ExpectedConditions.visibilityOfElementLocated(By.id(selectOneMenuItemsId)));
        // The value for each item is stored a attribute named "data-label"
        String selectItemXPath = String.format("//*[@data-label=\"%s\"]", fieldValue);
        var selectItem = selectOneMenuItems.findElement(By.xpath(selectItemXPath));
        selectItem.click();
    }

    /**
     * Find and returns the table row element with the "data-rk" attribute value that matches idValue.
     *
     * @param idValue The unique identifier value for the row.
     * @return The row element that contains the idValue valur or null if not found.
     */
    private WebElement findRowIndex(String idValue) {
        // Check each paginator page in the dataTable for a row that contains the idValue until found or the last page is checked.
        // To find the total number of pages in the table paginator we need to:
        // 1) Find the last page link that is identified with an element attribute of `aria-label="Last Page"`
        // 2) Check if the last page link is enabled by checking if the element attribute tabindex is not "-1"
        // 3) Click on the last page link if it is enabled.
        // 4) The last page number is the last element value in the collection of elements with the css class `ui-paginator-page`
        // 5) Go back to the first page by finding then and clicking on the first page link
        int totalPages = 1;
        WebElement lastPageLink = driver.findElement(By.cssSelector("[aria-label=\"Last Page\"]"));
        if (!Objects.equals(lastPageLink.getDomAttribute("tabindex"), "-1")) {
            lastPageLink.click();
            List<WebElement> pages = driver.findElements(By.className("ui-paginator-page"));
            totalPages = Integer.parseInt(pages.getLast().getText());
            WebElement firstPageLink = driver.findElement(By.cssSelector("[aria-label=\"First Page\"]"));
            firstPageLink.click();
        } else {
            totalPages = driver.findElements(By.className("ui-paginator-page")).size();
        }

        // Set the current page to 1 of the paginator
        int currentPage = 1;
        // Check each page of the dataTable
        while (currentPage <= totalPages) {
            // find the table element
            var tableElement = driver.findElement(By.id(primeFacesDataTableId));
            // Get all the rows from the table
            var tableRows = tableElement.findElements(By.tagName("tr"));
            // Track which row index the idValue is located
            int currentRowIndex = 0;
            // Check each row in the dataTable
            for (WebElement currentRow : tableRows) {
                // The idValue is stored in the "data-rk" attribute of the <tr> element
                String rowIdValue = currentRow.getDomAttribute("data-rk");
                // If the row contains a id value and it matches idValue then return the current row
                if (rowIdValue != null && rowIdValue.equalsIgnoreCase(idValue)) {
                    return tableRows.get(currentRowIndex);
                }
                // Check the next row
                currentRowIndex += 1;
            }
            // Check the next page
            currentPage += 1;
            // Click on the next page link
            var pageNextLink = driver.findElement(By.className("ui-paginator-next"));
            if (pageNextLink.isEnabled()) {
                pageNextLink.click();
            }
        }

        return null;
    }

    @Order(1)
    @ParameterizedTest
    @CsvSource(value = {
            "description, First Selenium Test, priority, High, done, true,",
            //"description, Second Selenium Test, priority, Medium, done, false,",
            //"description, Third Selenium Test, priority, Low, done, false,",
    })
    void shouldCreate(
            String field1Id, String field1Value,
            String field2Id, String field2Value,
            String field3Id, boolean field3Value
    ) throws InterruptedException, IOException {

        driver.get("http://localhost:8080/tasks/crud-tasks.xhtml");
        // Maximize the browser window to see the data being inputted
        driver.manage().window().maximize();
        Thread.sleep(1000);

        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Task - CRUD");
        driver.manage().window().maximize();

        // Find the New button by id then click on it
        var newButtonElement = driver.findElement(By.id("form:newButton"));
        assertThat(newButtonElement).isNotNull();
        newButtonElement.click();

        // Set the value for each form field.
        // Add suffix `_input` for p:inputNumber
        setTextValue("dialogs:" + field1Id, field1Value);
        // setPrimeFacesSelectOneMenuValue(field2Id, field2Value);
        // setPrimeFacesDatePickerValue(field1Id, field1Value);
        setTextValue("dialogs:" + field2Id, field2Value);
        //setTextValue("dialogs:" + field3Id, field3Value);
        if (field3Value) {
            var doneElement = driver.findElement(By.id("dialogs:" + field3Id));
            doneElement.click();
        }

        Thread.sleep(1000);

        // Find the Save button then click on it
        var saveButtonElement = driver.findElement(By.id("dialogs:saveButton"));
        assertThat(saveButtonElement).isNotNull();
        saveButtonElement.click();

        // Wait for 3 seconds and verify navigate has been redirected to the listing page
        var wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        var growlMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ui-growl-message")));
        // Verify the feedback message is displayed in the page
        String feedbackMessage = growlMessage.getText();
        assertThat(feedbackMessage)
                .containsIgnoringCase("Create was successful");
        // The primary key of the entity is at the end of the feedback message after the last space character.
        final int indexOfPrimaryKeyValue = feedbackMessage.lastIndexOf(" ") + 1;
        // Extract the primary key and remove any comma separating numbers greater 999
        String idValue = feedbackMessage.substring(indexOfPrimaryKeyValue).replaceAll(",", "");
        // Add the primary key to the list for usage in other test methods
        sharedEditIds.add(idValue);

        // Take screenshot of page and save source code
//        snapshot(driver, "after-submit" + idValue);

        Thread.sleep(1000);
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({
            "0, First Selenium Test, High, true",
            "1, Second Selenium Test, Medium, false",
            "2, Third Selenium Test, Low, false",
    })
    void shouldList(
            int idIndex,
            String expectedColumn1Value, String expectedColumn2Value, String expectedColumn3Value
    ) throws InterruptedException {
        String expectedIdValue = sharedEditIds.get(idIndex);
        // Open a browser and navigate to the index page
        driver.get("http://localhost:8080/tasks/crud-tasks.xhtml");
        // Maximize the browser window so we can see the data being inputted
        driver.manage().window().maximize();
        Thread.sleep(1000);

        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Task - CRUD");

        WebElement rowElement = findRowIndex(expectedIdValue);
        assertThat(rowElement).isNotNull();

        var rowColumns = rowElement.findElements(By.xpath("td"));
        final String column1Value = rowColumns.get(0).getText();
        final String column2Value = rowColumns.get(1).getText();
        final String column3Value = rowColumns.get(2).getText();

        assertThat(column1Value)
                .isEqualToIgnoringCase(expectedColumn1Value);
        assertThat(column2Value)
                .isEqualToIgnoringCase(expectedColumn2Value);
        assertThat(column3Value)
                .isEqualToIgnoringCase(expectedColumn3Value);

        // Take screenshot of page and save source code
        // snapshot(driver, "found-row" + expectedIdValue);

    }

    @Order(3)
    @Test
    void shouldDelete() throws InterruptedException, IOException {
        // Open a browser and navigate to the target page
        driver.get("http://localhost:8080/tasks/crud-tasks.xhtml");
        // Maximize the browser window so we can see the data being inputted
        driver.manage().window().maximize();
        Thread.sleep(1000);

        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Task - CRUD");

        // Find and delete all the rows added from the shouldCreate() method
        for (String idValue : sharedEditIds) {

            WebElement rowElement = findRowIndex(idValue);
            assertThat(rowElement).isNotNull();

            var rowColumns = rowElement.findElements(By.xpath("td"));
            var lastColumnIndex = rowColumns.size() - 1;
            var actionColumnButtons = rowColumns.get(lastColumnIndex).findElements(By.tagName("button"));
            // Delete button is the 2nd button in the column
            var deleteButton = actionColumnButtons.get(1);
            deleteButton.click();

            var wait = new WebDriverWait(driver, Duration.ofSeconds(1));

            var confirmationDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ui-dialog-buttonpane")));
            Thread.sleep(1000);
            var confirmButtons = confirmationDialog.findElements(By.tagName("button"));
            // Yes button is the 1std button in the diaog
            var yesButton = confirmButtons.getFirst();
            yesButton.click();

            wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            var growlMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ui-growl-message")));
            assertThat(driver.getTitle())
                    .isEqualToIgnoringCase("Task - CRUD");
            assertThat(growlMessage.getText())
                    .containsIgnoringCase("Delete was successful");

            // Take screenshot of page and save source code
//             snapshot(driver, "after-delete" + idValue);

        }

    }

    void snapshot(WebDriver d, String name) throws IOException {
        // Save screenshot of page as a png file to the target folder
        byte[] png = ((TakesScreenshot) d).getScreenshotAs(OutputType.BYTES);
        Files.write(Path.of("target", name + ".png"), png);

        // Save page source code as an html file to the target folder
        // String html = d.getPageSource();
        // Assertions.assertNotNull(html);
        // Files.writeString(Path.of("target", name + ".html"), html);
    }

}