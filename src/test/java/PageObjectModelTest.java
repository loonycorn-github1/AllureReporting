import com.loonycorn.learningselenium.pages.*;
import com.loonycorn.learningselenium.utils.DriverFactory;
import io.qameta.allure.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

@Listeners(io.qameta.allure.testng.AllureTestNg.class)
@Epic("E-commerce Website Testing")
@Feature("Shopping Cart Functionality")
public class PageObjectModelTest {

    private static final String SITE =
            "https://www.saucedemo.com/";

    private WebDriver driver;

    private LoginPage loginPage;
    private ProductsPage productsPage;
    private ProductPage productPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private FinalCheckoutPage finalCheckoutPage;
    private OrderCompletionPage orderCompletionPage;


    @BeforeClass
    public void setUp() {
        driver = DriverFactory.createDriver(DriverFactory.BrowserType.CHROME);

        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
        productPage = new ProductPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);
        finalCheckoutPage = new FinalCheckoutPage(driver);
        orderCompletionPage = new OrderCompletionPage(driver);

        driver.get(SITE);
    }

    private static void delay() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



    @Test
    @Description("Test to verify login functionality")
    @Severity(SeverityLevel.NORMAL)
    public void testLogin() {
        loginPage.login("standard_user", "secret_sauce");

        Assert.assertTrue(productsPage.isPageOpened(), "Login failed!");

        delay();
    }

    @Test(dependsOnMethods = "testLogin")
    @Description("Test to add a backpack to the cart")
    @Severity(SeverityLevel.NORMAL)
    public void testAddBackpackToCart() {
        productsPage.navigateToProductPage("Sauce Labs Backpack");

        productPage.addToCart();

        Assert.assertEquals(productPage.getButtonText(), "Remove",
                "Button text did not change");

        delay();

        driver.navigate().back();
    }

    @Test(dependsOnMethods = "testAddBackpackToCart")
    @Description("Test to add a fleece jacket to the cart")
    @Severity(SeverityLevel.NORMAL)
    public void testAddFleeceJacketToCart() {
        productsPage.navigateToProductPage("Sauce Labs Fleece Jacket");

        productPage.addToCart();

        Assert.assertEquals(productPage.getButtonText(), "Remove",
                "Button text did not change");

        delay();

        driver.navigate().back();
    }

    @Test(dependsOnMethods = {"testAddBackpackToCart", "testAddFleeceJacketToCart"})
    @Description("Test to verify the cart functionality")
    @Severity(SeverityLevel.NORMAL)
    public void testCart() {
        productsPage.navigateToCart();

        Assert.assertTrue(cartPage.isPageOpened(), "Cart page not loaded");
        Assert.assertEquals(cartPage.getCartItemCount(), "2", "Incorrect number of items in the cart");
        Assert.assertEquals(cartPage.getContinueButtonText(), "Checkout",
                "Incorrect button text on the cart page");

        Assert.assertTrue(cartPage.productInCart("Sauce Labs Backpack"));
        Assert.assertTrue(cartPage.productInCart("Sauce Labs Fleece Jacket"));

        delay();
    }

    @Test(dependsOnMethods = "testCart")
    @Description("Test to verify the checkout functionality")
    @Severity(SeverityLevel.CRITICAL)
    public void testCheckout() {
        cartPage.continueCheckout();

        Assert.assertTrue(checkoutPage.isPageOpened(), "Checkout page not loaded");
        checkoutPage.enterDetails("Peter", "Hank", "12345");

        Assert.assertEquals(checkoutPage.getFirstNameFieldValue(), "Peter",
                "First name field value is incorrect");
        Assert.assertEquals(checkoutPage.getLastNameFieldValue(), "Hank",
                "Last name field value is incorrect");
        Assert.assertEquals(checkoutPage.getZipCodeFieldValue(), "12345",
                "Zip code field value is incorrect");

        delay();
    }


    @Test(dependsOnMethods = "testCheckout")
    @Description("Test to verify the final checkout functionality")
    @Severity(SeverityLevel.CRITICAL)
    public void testFinalCheckout() {
        checkoutPage.continueCheckout();

        Assert.assertTrue(finalCheckoutPage.isPageOpened(),
                "Checkout page not loaded");
        Assert.assertEquals(finalCheckoutPage.getPaymentInfoValue(),
                "SauceCard #31337");
        Assert.assertEquals(finalCheckoutPage.getShippingInfoValue(), "" +
                "Free Pony Express Delivery!");
        Assert.assertEquals(finalCheckoutPage.getTotalLabel(),
                "Total: $86.38");

        delay();
    }


    @Test(dependsOnMethods = "testFinalCheckout")
    @Description("Test to verify the order completion functionality")
    @Severity(SeverityLevel.NORMAL)
    public void testOrderCompletion() {
        finalCheckoutPage.finishCheckout();

        delay();
    }


    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
