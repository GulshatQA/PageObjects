package ru.netology.pageobject.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.pageobject.data.DataHelper;
import ru.netology.pageobject.page.DashboardPage;
import ru.netology.pageobject.page.LoginPage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.pageobject.page.DashboardPage.pushFirstCardButton;
import static ru.netology.pageobject.page.DashboardPage.pushSecondCardButton;

public class MoneyTransferTest {

    @BeforeEach
    public void setUp() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Test
    public void shouldTransferMoneyFromFirstCardToSecondCard() {
        var dashboardPage = new DashboardPage();
        var firstCardBalanceStart = dashboardPage.getFirstCardBalance();
        var secondCardBalanceStart = dashboardPage.getSecondCardBalance();
        int amount = 1_635;

        var transactionPage = pushSecondCardButton();
        transactionPage.transactionMoney(amount, DataHelper.getFirstCardNumber());
        var firstCardBalanceResult = firstCardBalanceStart - amount;
        var secondCardBalanceResult = secondCardBalanceStart + amount;

        assertEquals(firstCardBalanceResult, dashboardPage.getFirstCardBalance());
        assertEquals(secondCardBalanceResult, dashboardPage.getSecondCardBalance());
    }

    @Test
    public void shouldTransferMoneyFromSecondCardToFirstCard() {
        var dashboardPage = new DashboardPage();
        var firstCardBalanceStart = dashboardPage.getFirstCardBalance();
        var secondCardBalanceStart = dashboardPage.getSecondCardBalance();
        int amount = 729;

        var transactionPage = pushFirstCardButton();
        transactionPage.transactionMoney(amount, DataHelper.getSecondCardNumber());
        var firstCardBalanceResult = firstCardBalanceStart + amount;
        var secondCardBalanceResult = secondCardBalanceStart - amount;

        assertEquals(firstCardBalanceResult, dashboardPage.getFirstCardBalance());
        assertEquals(secondCardBalanceResult, dashboardPage.getSecondCardBalance());
    }

    @Test
    public void shouldNotTransferMoneyIfAmountMoreBalance() {
        int amount = 50_000;
        var transactionPage = pushSecondCardButton();
        transactionPage.transactionMoney(amount, DataHelper.getFirstCardNumber());
        transactionPage.getErrorLimit();
    }

    @Test
    public void shouldGetErrorIfTheSameCards() {
        int amount = 500;
        var transactionPage = pushFirstCardButton();
        transactionPage.transactionMoney(amount, DataHelper.getFirstCardNumber());
        transactionPage.getErrorInvalidCard();
    }
}
