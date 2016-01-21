package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * <p>
 * Page Object to represent the project samples page.
 * </p>
 *
 */
public class ProjectSamplesPage extends ProjectPageBase {
	private static final String RELATIVE_URL = "projects/";

	@FindBy(tagName = "h1")
	private WebElement pageHeader;

	@FindBy(id = "samplesTable")
	private WebElement samplesTable;

	@FindBy(id = "selectAllBtn")
	private WebElement selectAll;

	@FindBy(id = "selected-count-info")
	private WebElement selectedCountInfo;

	@FindBy(css = "tbody input[type=checkbox]")
	private List<WebElement> sampleCheckboxes;

	@FindBy(id = "mergeBtn")
	private WebElement mergeBtn;

	@FindBy(id = "copyBtn")
	private WebElement copyBtn;

	@FindBy(id = "moveBtn")
	private WebElement moveBtn;

	@FindBy(id = "removeBtn")
	private WebElement removeBtn;

	// This will be 'Previous', 1, 2, ..., 'Next'
	@FindBy(css = ".pagination li")
	private List<WebElement> pagination;

	public ProjectSamplesPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectSamplesPage initPage(WebDriver driver) {
		return PageFactory.initElements(driver, ProjectSamplesPage.class);
	}

	public static ProjectSamplesPage gotToPage(WebDriver driver, int projectId) {
		get(driver, RELATIVE_URL + projectId);
		return PageFactory.initElements(driver, ProjectSamplesPage.class);
	}

	public String getTitle() {
		return pageHeader.getText();
	}

	public int getNumberProjectsDisplayed() {
		return sampleCheckboxes.size();
	}

	public boolean isMergeBtnEnabled() {
		return mergeBtn.isEnabled();
	}

	public boolean isCopyBtnEnabled() {
		return copyBtn.isEnabled();
	}

	public boolean isMoveBtnEnabled() {
		return moveBtn.isEnabled();
	}

	public boolean isRemoveBtnEnabled() {
		return moveBtn.isEnabled();
	}

	// PAGINATION
	public boolean isPreviousBtnEnabled() {
		return !pagination.get(0).getAttribute("class").contains("disabled");
	}

	public boolean isNextBtnEnabled() {
		return !pagination.get(pagination.size() - 1).getAttribute("class").contains("disabled");
	}

	public int getPaginationCount() {
		// -2 because we ignore the previous and next buttons
		return pagination.size() - 2;
	}

	public String getSelectedInfoText() {
		return selectedCountInfo.getText();
	}

	// Actions
	public void selectSample(int row) {
		sampleCheckboxes.get(row).click();
	}

	public void selectSampleWithShift(int row) {
		Actions actions = new Actions(driver);
		actions.keyDown(Keys.SHIFT).click(sampleCheckboxes.get(row)).perform();
	}

	public void selectAllOrNone() {
		selectAll.click();
	}
}
