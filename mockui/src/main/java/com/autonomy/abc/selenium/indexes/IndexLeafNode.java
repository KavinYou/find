package com.autonomy.abc.selenium.indexes;

import com.autonomy.abc.selenium.element.Checkbox;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class IndexLeafNode implements IndexNodeElement {
    private final Checkbox checkbox;
    private final WebElement container;

    public IndexLeafNode(WebElement element, WebDriver driver) {
        checkbox = new Checkbox(element, driver);
        container = element;
    }

    @Override
    public void select() {
        checkbox.check();
    }

    @Override
    public void deselect() {
        checkbox.uncheck();
    }

    @Override
    public boolean isSelected() {
        return checkbox.isChecked();
    }

    @Override
    public String getName() {
        return container.findElement(By.cssSelector("[data-name]")).getAttribute("data-name");
    }
}
