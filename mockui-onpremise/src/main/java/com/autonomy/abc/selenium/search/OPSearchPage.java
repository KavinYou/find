package com.autonomy.abc.selenium.search;

import com.hp.autonomy.frontend.selenium.element.Pagination;
import com.autonomy.abc.selenium.indexes.OPDatabaseTree;
import com.autonomy.abc.selenium.indexes.tree.IndexesTree;
import com.autonomy.abc.selenium.language.LanguageDropdown;
import com.autonomy.abc.selenium.language.OPLanguageDropdown;
import com.hp.autonomy.frontend.selenium.util.ElementUtil;
import com.hp.autonomy.frontend.selenium.util.ParametrizedFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class OPSearchPage extends SearchPage {
    private OPSearchPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected LanguageDropdown languageDropdown() {
        return new OPLanguageDropdown(findElement(By.cssSelector(".search-language")), getDriver());
    }

    public WebElement promotionsLabel() {
        return findElement(By.cssSelector(".promotions .promotion-name"));
    }

    @Override
    public IndexesTree indexesTree() {
        return new OPDatabaseTree(super.indexesTree());
    }

    public List<String> getPromotionLabels() {
        waitForPromotionsLoadIndicatorToDisappear();
        final List<String> labelList = new ArrayList<>();

        if (showMorePromotionsButton().isDisplayed()) {
            showMorePromotions();
        }
        labelList.addAll(getPromotionTypeLabels());

        while (ElementUtil.isEnabled(promotionPaginationButton(Pagination.NEXT))) {
            switchPromotionPage(Pagination.NEXT);
            labelList.addAll(getPromotionTypeLabels());
        }
        return labelList;
    }

    private List<String> getPromotionTypeLabels() {
        return ElementUtil.getTexts(findElements(By.className("promotion-name")));
    }

    public static class Factory extends SOPageFactory<OPSearchPage> {
        public Factory() {
            super(OPSearchPage.class);
        }

        public OPSearchPage create(WebDriver context) {
            return new OPSearchPage(context);
        }
    }
}
