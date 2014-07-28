package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectMembersPage;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

/**
 * <p>
 * Integration test to ensure that the Project Collaborators Page.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectMembersPageIT {
	public static final Long PROJECT_ID = 1L;
	private WebDriver driver;
	private ProjectMembersPage membersPage;

	private static final ImmutableList<String> COLLABORATORS_NAMES = ImmutableList.of("Mr. Manager", "test User");

	@Before
	public void setUp() {
        driver = new PhantomJSDriver();
        driver.manage().window().setSize(new Dimension(1024, 900));
		LoginPage loginPage = LoginPage.to(driver);
		loginPage.doLogin();

		membersPage = new ProjectMembersPage(driver, PROJECT_ID);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	@Test
	public void testPageSetUp() {
		assertEquals("Page h1 tag is properly set.", "project Members", membersPage.getTitle());
		List<String> names = membersPage.getProjectMembersNames();
		for (String name : names) {
			assertTrue("Has the correct members names", COLLABORATORS_NAMES.contains(name));
		}
	}

	@Test
	public void testRemoveUser() {
		membersPage.clickRemoveUserButton(2l);
		membersPage.clickModialPopupButton();
		List<String> userNames = membersPage.getProjectMembersNames();
		assertEquals(1, userNames.size());
	}

	@Test
	public void testEditRole() {
		Long userid = 2l;
		membersPage.clickEditButton(userid);
		assertTrue("Role select dropdowns should be visible", membersPage.roleSelectDisplayed(userid));
		membersPage.setRoleForUser(2l, ProjectRole.PROJECT_OWNER.toString());
		assertTrue(membersPage.notySuccessDisplayed());
		assertTrue("Role span display should be visible", membersPage.roleSpanDisplayed(userid));

	}
}
