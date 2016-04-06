package com.autonomy.abc.selenium.keywords;

import com.autonomy.abc.selenium.language.LanguageDropdown;
import com.autonomy.abc.selenium.language.WarningLanguageDropdown;
import org.openqa.selenium.WebDriver;

public class HSODKeywordsPage extends KeywordsPage {
    private HSODKeywordsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected LanguageDropdown languageDropdown() {
        return new WarningLanguageDropdown();
    }


    public static class Factory extends SOPageFactory<HSODKeywordsPage> {
        public Factory() {
            super(HSODKeywordsPage.class);
        }

        @Override
        public HSODKeywordsPage create(WebDriver context) {
            return new HSODKeywordsPage(context);
        }
    }
}
