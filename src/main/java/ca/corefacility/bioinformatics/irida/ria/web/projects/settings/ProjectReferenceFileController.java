package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.security.Principal;

import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;


/**
 * Controller for ajax request dealing with project reference files.
 *
 */
@Controller
@RequestMapping(value = "/projects")
public class ProjectReferenceFileController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectReferenceFileController.class);
	private final ProjectService projectService;
	private final ReferenceFileService referenceFileService;
	private final ProjectControllerUtils projectControllerUtils;
	private final MessageSource messageSource;

	@Autowired
	public ProjectReferenceFileController(ProjectService projectService, ReferenceFileService referenceFileService,
			ProjectControllerUtils projectControllerUtils, MessageSource messageSource) {
		this.projectService = projectService;
		this.referenceFileService = referenceFileService;
		this.projectControllerUtils = projectControllerUtils;
		this.messageSource = messageSource;
	}

	/**
	 * Get the reference files page for a project
	 *
	 * @param model     model for the view
	 * @param principal currently logged in user
	 * @param projectId id of the project to get files for
	 * @return name of the reference files view
	 */
	@RequestMapping(value = "/{projectId}/settings/referenceFiles", method = RequestMethod.GET)
	public String getProjectReferenceFilesPage(final Model model, final Principal principal,
			@PathVariable long projectId) {
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

		model.addAttribute("project", project);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "referenceFiles");
		return "projects/settings/pages/references";
	}
}
