package com.autonomy.abc.selenium.page;

import com.autonomy.abc.selenium.AppElement;
import com.autonomy.abc.selenium.element.DatePicker;
import com.autonomy.abc.selenium.menubar.NotificationsDropDown;
import com.autonomy.abc.selenium.menubar.SideNavBar;
import com.autonomy.abc.selenium.menubar.TopNavBar;
import com.autonomy.abc.selenium.page.admin.AboutPage;
import com.autonomy.abc.selenium.page.admin.SettingsPage;
import com.autonomy.abc.selenium.page.admin.UsersPage;
import com.autonomy.abc.selenium.page.keywords.CreateNewKeywordsPage;
import com.autonomy.abc.selenium.page.keywords.KeywordsPage;
import com.autonomy.abc.selenium.page.login.LoginHostedPage;
import com.autonomy.abc.selenium.page.login.LoginOnPremisePage;
import com.autonomy.abc.selenium.page.overview.OverviewPage;
import com.autonomy.abc.selenium.page.promotions.CreateNewDynamicPromotionsPage;
import com.autonomy.abc.selenium.page.promotions.CreateNewPromotionsPage;
import com.autonomy.abc.selenium.page.promotions.PromotionsPage;
import com.autonomy.abc.selenium.page.promotions.SchedulePage;
import com.autonomy.abc.selenium.page.search.EditDocumentReferencesPage;
import com.autonomy.abc.selenium.page.search.SearchPage;
import com.autonomy.abc.selenium.util.AbstractWebElementPlaceholder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AppBody extends AppElement {

	private final AbstractWebElementPlaceholder<OverviewPage> overviewPage;
	private final AbstractWebElementPlaceholder<PromotionsPage> promotionsPage;
	private final AbstractWebElementPlaceholder<AboutPage> aboutPage;
	private final AbstractWebElementPlaceholder<UsersPage> usersPage;
	private final AbstractWebElementPlaceholder<SettingsPage> settingsPage;
	private final KeywordsPage.Placeholder keywordsPage;
	private final SearchPage.Placeholder searchPage;
	private final LoginOnPremisePage.Placeholder loginOnPremPage;
	private final CreateNewPromotionsPage.Placeholder createPromotionsPage;
	private final CreateNewDynamicPromotionsPage.Placeholder dynamicPromotionsPage;
	private final SchedulePage.Placeholder schedulePage;
	private final CreateNewKeywordsPage.Placeholder createKeywordsPage;
	private final TopNavBar topNavBar;
	private final SideNavBar sideNavBar;
	private final NotificationsDropDown.Placeholder notifications;
	private final EditDocumentReferencesPage.Placeholder editReferences;

	public AppBody(final WebDriver driver) {
		this(driver, new SideNavBar(driver), new TopNavBar(driver));
	}

	public AppBody(final WebDriver driver, final SideNavBar navBar, final TopNavBar topNavBar) {
		super(driver.findElement(By.cssSelector("body")), driver);

		this.overviewPage = new OverviewPage.Placeholder(this, navBar, topNavBar);
		this.promotionsPage = new PromotionsPage.Placeholder(this, navBar, topNavBar);
		this.aboutPage = new AboutPage.Placeholder(this, navBar, topNavBar);
		this.usersPage = new UsersPage.Placeholder(this, navBar, topNavBar);
		this.settingsPage = new SettingsPage.Placeholder(this, navBar, topNavBar);

		this.searchPage = new SearchPage.Placeholder(topNavBar);
		this.loginOnPremPage = new LoginOnPremisePage.Placeholder(topNavBar);
		this.createPromotionsPage = new CreateNewPromotionsPage.Placeholder(topNavBar);
		this.dynamicPromotionsPage = new CreateNewDynamicPromotionsPage.Placeholder(topNavBar);
		this.schedulePage = new SchedulePage.Placeholder(topNavBar);
		this.createKeywordsPage = new CreateNewKeywordsPage.Placeholder(topNavBar);
		this.keywordsPage = new KeywordsPage.Placeholder(topNavBar);
		this.topNavBar = new TopNavBar(driver);
		this.sideNavBar = new SideNavBar(driver);
		this.notifications = new NotificationsDropDown.Placeholder(topNavBar);
		this.editReferences = new EditDocumentReferencesPage.Placeholder(topNavBar);
	}

	public OverviewPage getOverviewPage() {
		return overviewPage.$page();
	}

	public PromotionsPage getPromotionsPage() {
		return promotionsPage.$page();
	}

	public KeywordsPage getKeywordsPage() {
		return keywordsPage.$keywordsPage(getDriver().findElement(By.cssSelector(".keywords-container")));
	}

	public AboutPage getAboutPage() {
		return aboutPage.$topNavBarDropDownPage();
	}

	public UsersPage getUsersPage() {
		return usersPage.$topNavBarDropDownPage();
	}

	public SettingsPage getSettingsPage() {
		return settingsPage.$topNavBarDropDownPage();
	}

	public SearchPage getSearchPage() {
		return searchPage.$searchPage(getParent(getParent(getParent(getDriver().findElement(By.cssSelector(".search-control-column"))))));
	}

	public CreateNewPromotionsPage getCreateNewPromotionsPage() {
		return createPromotionsPage.$createNewPromotionsPage(getDriver().findElement(By.cssSelector(".pd-wizard")));
	}

	public CreateNewDynamicPromotionsPage getCreateNewDynamicPromotionsPage() {
		return dynamicPromotionsPage.$createNewDynamicPromotionsPage(getDriver().findElement(By.cssSelector(".pd-wizard")));
	}

	public SchedulePage getSchedulePage() {
		return schedulePage.$schedulePage(getDriver().findElement(By.cssSelector(".pd-wizard")));
	}

	public CreateNewKeywordsPage getCreateKeywordsPage() {
		return createKeywordsPage.$createNewKeywordsPage(getDriver().findElement(By.cssSelector(".pd-wizard")));
	}

	public LoginOnPremisePage getLoginOnPremisePage() {
		return loginOnPremPage.$loginPage(getDriver().findElement(By.cssSelector(".loginscreen")));
	}

	public LoginHostedPage getLoginHostedPage() {
		return new LoginHostedPage(this, getDriver());
	}

	public TopNavBar getTopNavBar() {
		return topNavBar;
	}

	public SideNavBar getSideNavBar() {
		return sideNavBar;
	}

	public NotificationsDropDown getNotifications() {
		return notifications.$notificationsDropDown(findElement(By.cssSelector(".notification-list")));
	}

	public EditDocumentReferencesPage getEditDocumentReferencesPage() {
		return editReferences.$editReferences(findElement(By.cssSelector("[style='display: block;'] .main-page-contents.search-page-contents")));
	}

	public void logout() {
		topNavBar.findElement(By.cssSelector(".fa-cog")).click();
		topNavBar.findElement(By.xpath(".//a[text()=' Logout']")).click();
	}

	public DatePicker getDatePicker() {
		return new DatePicker(this, getDriver());
	}
}
