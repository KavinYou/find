package com.autonomy.abc.find;

import com.autonomy.abc.base.FindTestBase;
import com.autonomy.abc.selenium.error.Errors;
import com.autonomy.abc.selenium.find.FindService;
import com.autonomy.abc.selenium.find.FindTopNavBar;
import com.autonomy.abc.selenium.find.results.ResultsView;
import com.autonomy.abc.selenium.find.results.RelatedConceptsPanel;
import com.hp.autonomy.frontend.selenium.config.TestConfig;
import com.hp.autonomy.frontend.selenium.framework.categories.CoreFeature;
import com.hp.autonomy.frontend.selenium.framework.logging.ActiveBug;
import com.hp.autonomy.frontend.selenium.framework.logging.RelatedTo;
import com.hp.autonomy.frontend.selenium.framework.logging.ResolvedBug;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.hp.autonomy.frontend.selenium.framework.state.TestStateAssert.assertThat;
import static com.hp.autonomy.frontend.selenium.framework.state.TestStateAssert.verifyThat;
import static com.hp.autonomy.frontend.selenium.matchers.CommonMatchers.containsItems;
import static com.hp.autonomy.frontend.selenium.matchers.ElementMatchers.*;
import static com.hp.autonomy.frontend.selenium.matchers.StringMatchers.containsString;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

@RelatedTo("CSA-2091")
public class RelatedConceptsITCase extends FindTestBase {
    private FindService findService;
    private FindTopNavBar navBar;


    public RelatedConceptsITCase(final TestConfig config) {
        super(config);
    }

    @Before
    public void setUp() {
        findService = getApplication().findService();
        navBar = getElementFactory().getTopNavBar();
    }

    @Test
    public void testRelatedConceptsHasResults() {
        findService.search("Danye West");
        for (final WebElement concept : conceptsPanel()) {
            assertThat(concept, hasTextThat(not(isEmptyOrNullString())));
            assertThat(concept, not(containsTextIgnoringCase("loading")));
        }
    }

    @Test
    @RelatedTo("CCUK-3598")
    public void testRelatedConceptsNavigateOnClick() {
        final String search = "Red";
        findService.search(search);
        final WebElement topRelatedConcept = conceptsPanel().concept(0);
        final String concept = topRelatedConcept.getText();

        topRelatedConcept.click();
        assertThat(navBar.getAlsoSearchingForTerms(), hasItem(equalToIgnoringCase(concept)));
        assertThat(navBar.getSearchBoxTerm(), is(search));
    }

    @Test
    @ResolvedBug({"CCUK-3498", "CSA-2066"})
    public void testRelatedConceptsHover() {
        findService.search("Find");
        final WebElement popover = conceptsPanel().hoverOverRelatedConcept(0);
        verifyThat(popover, hasTextThat(not(isEmptyOrNullString())));
        verifyThat(popover.getText(), not(containsString("QueryText-Placeholder")));
        verifyThat(popover.getText(), not(containsString(Errors.Search.RELATED_CONCEPTS)));
        getElementFactory().getFindPage().unhover();
    }

    @Test
    public void testMultipleAdditionalConcepts() {
        findService.search("bongo");

        final Collection<String> relatedConcepts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<WebElement> newRelatedConcepts = conceptsPanel().relatedConcepts();
            if(!newRelatedConcepts.isEmpty()) {
                final String newConcept = clickFirstNewConcept(relatedConcepts,newRelatedConcepts);
                verifyThat(navBar.getAlsoSearchingForTerms(), hasItem(equalToIgnoringCase(newConcept)));
            }
        }
        verifyThat(navBar.getSearchBoxTerm(), is("bongo"));
        verifyThat(navBar.getAlsoSearchingForTerms(), hasSize(relatedConcepts.size()));
        verifyThat(navBar.getAlsoSearchingForTerms(), containsItems(relatedConcepts));
    }

    @Test
    @Category(CoreFeature.class)
    public void testAddRemoveConcepts() {
        findService.search("jungle");
        final Collection<String> concepts = new ArrayList<>();
        final String firstConcept = clickFirstNewConcept(concepts,conceptsPanel().relatedConcepts());
        final String secondConcept = clickFirstNewConcept(concepts,conceptsPanel().relatedConcepts());

        verifyThat(navBar.getAlsoSearchingForTerms(), hasSize(2));

        if (isHosted()) {
            navBar.additionalConcept(secondConcept).removeAndWait();
        } else {
            navBar.closeFirstConcept();
        }

        final List<String> alsoSearchingFor = navBar.getAlsoSearchingForTerms();

        verifyThat(alsoSearchingFor, hasSize(1));
        verifyThat(alsoSearchingFor, not(hasItem(equalToIgnoringCase(secondConcept))));
        verifyThat(alsoSearchingFor, hasItem(equalToIgnoringCase(firstConcept)));
        verifyThat(navBar.getSearchBoxTerm(), is("jungle"));
    }

    @Test
    @ResolvedBug("CCUK-3566")
    @ActiveBug("FIND-109")
    public void testTermNotInRelatedConcepts() {
        final String query = "world cup";
        findService.search(query);
        final Collection<String> addedConcepts = new ArrayList<>();
        final RelatedConceptsPanel panel = conceptsPanel();

        verifyThat(panel.getRelatedConcepts(), not(hasItem(equalToIgnoringCase(query))));

        for (int i = 0; i < 5; i++) {
            clickFirstNewConcept(addedConcepts,conceptsPanel().relatedConcepts());
            verifyThat(panel.getRelatedConcepts(), not(hasItem(equalToIgnoringCase(query))));
        }
    }

    @Test
    @ResolvedBug("CCUK-3566")
    public void testAdditionalConceptsNotAlsoRelated() {
        findService.search("matt");
        final Collection<String> addedConcepts = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            clickFirstNewConcept(addedConcepts,conceptsPanel().relatedConcepts());
            final List<String> relatedConcepts = conceptsPanel().getRelatedConcepts();

            for (final String addedConcept : addedConcepts) {
                verifyThat(relatedConcepts, not(hasItem(equalToIgnoringCase(addedConcept))));
            }
        }
    }

    @Test
    @ActiveBug("FIND-164")
    public void testAddSausageToQuery() {
        ResultsView results = findService.search("sausage");
        conceptsPanel().toggleHighlight();

        final Collection<String> addedConcepts = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            final WebElement firstSausage = results.scrollForHighlightedSausages().get(0);

            // Lower case since concepts are upper-cased for display in the UI, but appear in their original case in the nav bar
            final String firstSausageText = firstSausage.getText().toLowerCase();

            LOGGER.info("clicking sausage {}", firstSausageText);

            addedConcepts.add(firstSausageText);
            firstSausage.click();

            // Query text has not changed
            verifyThat(navBar.getSearchBoxTerm(), equalToIgnoringCase("sausage"));

            // But we have a new related concept
            verifyThat(navBar.getAlsoSearchingForTerms(), containsItems(addedConcepts));
        }
    }

    @Test
    @RelatedTo("FIND-243")
    public void testRefreshAddedConcepts() {
        findService.search("fresh");
        final Collection<String> concepts = new ArrayList<>();
        clickFirstNewConcept(concepts,conceptsPanel().relatedConcepts());
        clickFirstNewConcept(concepts,conceptsPanel().relatedConcepts());

        getWindow().refresh();
        navBar = getElementFactory().getTopNavBar();

        verifyThat(navBar.getSearchBoxTerm(), is("fresh"));
        verifyThat(navBar.getAlsoSearchingForTerms(), containsItems(concepts));
    }

    private String clickFirstNewConcept(final Collection<String> existingConcepts, List<WebElement> relatedConcepts) {
        for (final WebElement concept : relatedConcepts) {
            final String conceptText = concept.getText();
            if (!existingConcepts.contains(conceptText)) {
                LOGGER.info("Clicking concept " + conceptText);
                concept.click();

                existingConcepts.add(conceptText.toLowerCase());

                return conceptText;
                }
            }
        throw new NoSuchElementException("no new related concepts");
    }

    private RelatedConceptsPanel conceptsPanel() {
        return getElementFactory().getRelatedConceptsPanel();
    }
}
