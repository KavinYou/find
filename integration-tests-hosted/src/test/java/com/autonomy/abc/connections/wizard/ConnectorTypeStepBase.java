package com.autonomy.abc.connections.wizard;

import com.autonomy.abc.config.HostedTestBase;
import com.autonomy.abc.config.TestConfig;
import com.autonomy.abc.selenium.config.ApplicationType;
import com.autonomy.abc.selenium.menu.NavBarTabId;
import com.autonomy.abc.selenium.page.connections.ConnectionsPage;
import com.autonomy.abc.selenium.page.connections.NewConnectionPage;
import com.autonomy.abc.selenium.page.connections.wizard.ConnectorType;
import com.autonomy.abc.selenium.page.connections.wizard.ConnectorTypeStepTab;
import com.autonomy.abc.selenium.util.Waits;
import com.hp.autonomy.frontend.selenium.util.AppElement;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;

import static com.autonomy.abc.framework.ABCAssert.assertThat;
import static com.autonomy.abc.matchers.ElementMatchers.hasClass;
import static org.hamcrest.Matchers.not;


/**
 * Created by avidan on 29-10-15.
 */
public class ConnectorTypeStepBase extends HostedTestBase {
    public ConnectorTypeStepBase(TestConfig config, String browser, ApplicationType type, Platform platform) {
        super(config, browser, type, platform);
    }

    protected final String INVALID_INPUT_CLASS = "has-error";


    protected ConnectionsPage connectionsPage;
    protected NewConnectionPage newConnectionPage;
    protected ConnectorTypeStepTab connectorTypeStepTab;

    @Before
    public void setUp() {
        body.getSideNavBar().switchPage(NavBarTabId.CONNECTIONS);

        connectionsPage = getElementFactory().getConnectionsPage();
        connectionsPage.newConnectionButton().click();

        newConnectionPage = getElementFactory().getNewConnectionPage();
        connectorTypeStepTab = newConnectionPage.getConnectorTypeStep();
    }

    @After
    public void cleanUp() {

    }

    protected void selectConnectorType(ConnectorType type) {
        connectorTypeStepTab.typeBtn(type).click();
        Waits.loadOrFadeWait();

        for (ConnectorType eachType : ConnectorType.values()) {
            if (eachType.equals(type)) {
                assertThat(connectorTypeStepTab.typeBtn(eachType), hasClass("connector-icon-selected"));
            } else {
                assertThat(connectorTypeStepTab.typeBtn(eachType), not(hasClass("connector-icon-selected")));
            }
        }
    }

    protected static Matcher<? super WebElement> stepIsCurrent() {
        return new TypeSafeMatcher<WebElement>() {
            @Override
            protected boolean matchesSafely(WebElement item) {
                return AppElement.hasClass("current", item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is current step");
            }
        };
    }

    protected static Matcher<? super WebElement> stepIsValid() {
        return new TypeSafeMatcher<WebElement>() {
            @Override
            protected boolean matchesSafely(WebElement item) {
                return !AppElement.hasClass("error", item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is valid step");
            }
        };
    }
}
