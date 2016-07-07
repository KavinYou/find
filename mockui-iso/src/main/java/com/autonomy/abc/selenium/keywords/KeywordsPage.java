package com.autonomy.abc.selenium.keywords;

import com.autonomy.abc.selenium.language.Language;
import com.autonomy.abc.selenium.language.LanguageDropdown;
import com.hp.autonomy.frontend.selenium.element.FormInput;
import com.hp.autonomy.frontend.selenium.util.ElementUtil;
import com.hp.autonomy.frontend.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public abstract class KeywordsPage extends KeywordsBase {

    public KeywordsPage(final WebDriver driver) {
        super(new WebDriverWait(driver,30).until(ExpectedConditions.visibilityOfElementLocated(By.className("wrapper-content"))), driver);
        waitForLoad();
    }

    @Override
    public void waitForLoad() {
        new WebDriverWait(getDriver(),30)
                .withMessage("Keywords page failed to load")
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("keywords-container")));
    }

    public ExpectedCondition<Boolean> keywordLoadingIndicatorsGone(){
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return findElements(By.cssSelector(".keywords-sub-list .keyword-processing-indicator.fa-spin")).size()<1;
            }
        };
    }

    public ExpectedCondition<Boolean> allKeywordsGone() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return findElements(By.cssSelector(".data-keywords")).size() < 1;
            }
        };
    }

    public WebElement createNewKeywordsButton() {
        return findElement(By.xpath(".//div[contains(@class,'keywords-controls')]//a[contains(text(), 'New')]"));
    }

    public int countSynonymLists() {
        return keywordsContainer().synonymGroups().size();
    }

    public List<WebElement> allKeywordGroups() {
        return keywordsContainer().keywordGroups();
    }

    public List<String> getAllKeywords() {
        return keywordsContainer().getKeywords();
    }

    public List<String> getAllFirstKeywords(){
        return keywordsContainer().getFirstKeywords();
    }

    public List<WebElement> removeButtons(final WebElement keywordGroup) {
        return keywordGroup.findElements(By.cssSelector("li .remove-keyword"));
    }

    public WebElement synonymInGroup(final String synonym){
        return findElement(By.xpath(".//div[contains(@class, 'keywords-list')]//li[@data-term='" + synonym + "']"));
    }

    public WebElement synonymGroup(final String synonym){
        return ElementUtil.ancestor(synonymInGroup(synonym), 2);
    }

    public WebElement loadingIndicator(){
        return findElement(By.className("loading-indicator"));
    }
    
    public void filterView(final KeywordFilter filter) {
        final WebDriverWait wait = new WebDriverWait(getDriver(),5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".keywords-filters .dropdown-toggle"))).click();
        findElement(By.xpath("//*[contains(@class,'keywords-filters')]//a[text()='"+ filter +"']")).click();
        Waits.loadOrFadeWait();
    }

    public int countKeywords() {
        if (findElement(By.cssSelector(".keyword-list-message")).isDisplayed()) {
            return 0;
        }
        return findElements(By.cssSelector(".keywords-list .remove-keyword")).size();
    }

    public int countSynonymGroupsWithSynonym(final String synonym) {
        return findElement(By.cssSelector(".keywords-container .keywords-list")).findElements(By.xpath(".//ul[contains(@class,'keywords-sub-list')]/li[@data-term='" + synonym + "']")).size();
    }

    public WebElement searchFilterTextBox() {
        return findElement(By.className("keywords-search-filter"));
    }

    public FormInput searchFilterBox() {
        return new FormInput(searchFilterTextBox(), getDriver());
    }

    //should use LanguageDropdown class
    public boolean languageMenuDisabled(){
        return findElements(By.cssSelector(".languages-select-view-container .dropdown:nth-of-type(2) button[disabled]")).size()>0;
    }

    public final void selectLanguage(final Language language) {
        languageDropdown().select(language);
    }

    public Language getSelectedLanguage() {
        return languageDropdown().getSelected();
    }

    public WebElement selectLanguageButton() {
        return new WebDriverWait(getDriver(),20).until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".keywords-filters .current-language-selection")));
    }

    public List<Language> getLanguageList() {
        final List<Language> languages = new ArrayList<>();

        if (ElementUtil.isAttributePresent(ElementUtil.getParent(selectLanguageButton()), "disabled")) {
            languages.add(getSelectedLanguage());
            return languages;
        } else {
            selectLanguageButton().click();
            Waits.loadOrFadeWait();

            for (final WebElement language : findElements(By.cssSelector(".keywords-filters .scrollable-menu a"))) {
                languages.add(Language.fromString(language.getText()));
            }

            selectLanguageButton().click();
            return languages;
        }
    }

    protected abstract LanguageDropdown languageDropdown();

    public List<String> getFirstSynonymsList() {
        final List<String> leadSynonyms = new ArrayList<>();
        for (final WebElement synonymGroup : findElements(By.cssSelector(".keywords-list > ul > li"))) {
            leadSynonyms.add(synonymGroup.findElement(By.cssSelector("li:first-child span span")).getText());
        }
        return leadSynonyms;
    }
}

