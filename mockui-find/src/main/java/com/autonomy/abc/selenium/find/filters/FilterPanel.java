package com.autonomy.abc.selenium.find.filters;

import com.autonomy.abc.selenium.find.Container;
import com.autonomy.abc.selenium.indexes.Index;
import com.autonomy.abc.selenium.indexes.tree.IndexCategoryNode;
import com.autonomy.abc.selenium.indexes.tree.IndexesTree;
import com.autonomy.abc.selenium.query.DatePickerFilter;
import com.autonomy.abc.selenium.query.StringDateFilter;
import com.hp.autonomy.frontend.selenium.element.Collapsible;
import com.hp.autonomy.frontend.selenium.element.FormInput;
import com.hp.autonomy.frontend.selenium.util.ElementUtil;
import com.hp.autonomy.frontend.selenium.util.ParametrizedFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

import static com.hp.autonomy.frontend.selenium.util.CssUtil.cssifyIndex;

public class FilterPanel {
    private final WebElement panel;
    private final WebDriver driver;
    private final ParametrizedFactory<IndexCategoryNode, IndexesTree> indexesTreeFactory;

    public FilterPanel(final ParametrizedFactory<IndexCategoryNode, IndexesTree> indexesTreeFactory, final WebDriver driver) {
        this.indexesTreeFactory = indexesTreeFactory;
        this.driver = driver;
        this.panel = Container.LEFT.findUsing(driver);
    }

    //INDEX/DATABASE RELATED
    public IndexesTree indexesTree() {
        return indexesTreeFactory.create(new FindIndexCategoryNode(panel.findElement(By.cssSelector(".databases-list [data-category-id='all']")), driver));
    }

    public Index getIndex(final int i) {
        return indexesTree().allIndexes().getIndex(i);
    }

    public void waitForIndexes() {
        new WebDriverWait(driver, 10).until(ExpectedConditions.invisibilityOfElementLocated(By.className("not-loading")));
    }

    public void clickFirstIndex(){
        panel.findElement(By.cssSelector(".child-categories li:first-child")).click();
    }

    //REFACTOR - START
    public ListFilterContainer indexesTreeContainer() {
        final WebElement heading = panel.findElement(By.xpath(".//h4[contains(text(), 'Indexes') or contains(text(), 'Databases')]"));
        final WebElement container = ElementUtil.ancestor(heading, 2);
        return new IndexesTreeContainer(container, driver);
    }

    protected DateFilterContainer dateFilterContainer() {
        final WebElement heading = panel.findElement(By.xpath(".//h4[contains(text(), 'Dates')]"));
        final WebElement container = ElementUtil.ancestor(heading, 2);
        return new DateFilterContainer(container, driver);
    }

    public List<ParametricFieldContainer> parametricFieldContainers() {
        final List<ParametricFieldContainer> containers = new ArrayList<>();
        for (final WebElement container : getParametricFilters()) {
            containers.add(new ParametricFieldContainer(container, driver));
        }
        return containers;
    }

    private List<WebElement> getParametricFilters() {
        final List<WebElement> ancestors = new ArrayList<>();
        for (final WebElement element : panel.findElements(By.className("parametric-fields-table"))) {
            ancestors.add(ElementUtil.ancestor(element, 3));
        }
        return ancestors;
    }
    public ParametricFieldContainer parametricContainerOfFieldValue(final String fieldName) {
        //container could be [data-field-display-name'="+fieldName+"'] div] -> might be easier
        WebElement field = panel.findElement(By.cssSelector(".parametric-value-element[data-value='"+fieldName+"']"));
        return new ParametricFieldContainer(ElementUtil.ancestor(field,4),driver);
    }
    //REFACTOR - END

    public ParametricFieldContainer parametricField(final int i) {
        return parametricFieldContainers().get(i);
    }

    //DATE SPECIFIC
    public void toggleFilter(final DateOption filter) {
        dateFilterContainer().toggleFilter(filter);
    }

    public DatePickerFilter.Filterable datePickerFilterable() {
        return dateFilterContainer();
    }

    public StringDateFilter.Filterable stringDateFilterable() {
        return dateFilterContainer();
    }

    //CHECKBOXES
    public List<FindParametricFilter> checkBoxesForParametricFieldContainer(final int i ){
        ParametricFieldContainer container = parametricField(i);
        container.expand();
        return container.getFilters();
    }

    public FindParametricFilter checkboxForParametricValue(final String fieldName, final String fieldValue) {
        final WebElement checkbox = panel.findElement(By.cssSelector("[data-field-display-name='" + fieldName.replace(" ", "_") + "'] [data-value='" + fieldValue.toUpperCase() + "']"));
        return new FindParametricFilter(checkbox, driver);
    }

    public FindParametricFilter checkboxForParametricValue(final int fieldIndex, final int valueIndex) {
        final WebElement checkbox = panel.findElement(By.cssSelector("[data-field]:nth-of-type(" + cssifyIndex(fieldIndex) +") [data-value]:nth-of-type(" + cssifyIndex(valueIndex) + ')'));
        return new FindParametricFilter(checkbox, driver);
    }

    //EXPANDING AND COLLAPSING
    protected List<FilterContainer> allFilterContainers() {
        final List<FilterContainer> nodes = new ArrayList<>();
        nodes.add(indexesTreeContainer());
        nodes.add(dateFilterContainer());
        nodes.addAll(parametricFieldContainers());
        return nodes;
    }

    public void collapseAll() {
        for (final Collapsible collapsible : allFilterContainers()) {
            collapsible.collapse();
        }
    }

    //OTHER
    public String getErrorMessage() {
        return panel.findElement(By.cssSelector("p:not(.hide)")).getText();
    }

    public boolean noParametricFields() { return !panel.findElements(By.cssSelector(".parametric-empty:not(.hide)")).isEmpty(); }

    public void waitForParametricFields() {
        Container.LEFT.waitForLoad(driver);
    }

    protected WebElement getPanel(){
        return this.panel;
    }
}
