package ca.corefacility.bioinformatics.irida.ria.web.services;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.JobError;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTAnalysis;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Used to create {@link DataTablesResponse}s for all pages that use `analyses-table.js`.
 */
@Component
public class AnalysesListingService {
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService iridaWorkflowsService;
	private UpdateAnalysisSubmissionPermission updateAnalysisPermission;
	private MessageSource messageSource;

	@Autowired
	public AnalysesListingService(AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService, UpdateAnalysisSubmissionPermission updateAnalysisPermission,
			MessageSource messageSource) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.iridaWorkflowsService = iridaWorkflowsService;
		this.updateAnalysisPermission = updateAnalysisPermission;
		this.messageSource = messageSource;
	}

	/**
	 * Get a {@link DataTablesResponse} for {@link AnalysisSubmission}s based upon the {@link User}, and the {@link Project}
	 *
	 * @param params  {@link DataTablesParams}
	 * @param locale  {@link Locale}
	 * @param user    {@link User}
	 * @param project {@link Project}
	 * @return {@link DataTablesResponse}
	 * @throws IridaWorkflowNotFoundException If the requested workflow doesn't exist
	 * @throws ExecutionManagerException      If the submission cannot be read properly
	 */
	public DataTablesResponse getPagedSubmissions(DataTablesParams params, Locale locale, User user, Project project)
			throws IridaWorkflowNotFoundException, ExecutionManagerException {
		/*
		Check the DataTableParams to see if any search conditions are present
		 */
		Map<String, String> searchMap = params.getSearchMap();
		AnalysisState state = searchMap.containsKey("analysisState") ?
				AnalysisState.valueOf(searchMap.get("analysisState")) :
				null;
		String name = searchMap.getOrDefault("name", null);
		/*
		Workflow Ids are a special consideration.
		The actual ids need to be look up based on the name passed.
		 */
		Set<UUID> workflowIds = null;
		if (searchMap.containsKey("workflow")) {
			AnalysisType workflowType = AnalysisTypes.fromString(searchMap.get("workflow"));
			Set<IridaWorkflow> workflows = iridaWorkflowsService.getAllWorkflowsByType(workflowType);
			workflowIds = workflows.stream().map(IridaWorkflow::getWorkflowIdentifier).collect(Collectors.toSet());
		}

		Page<AnalysisSubmission> page;
		PageRequest pageRequest = new PageRequest(params.getCurrentPage(), params.getLength(), params.getSort());

		if (user != null) {
			//if user is set, get submissions for the user
			page = analysisSubmissionService
					.listSubmissionsForUser(params.getSearchValue(), name, state, user, workflowIds, pageRequest);
		} else if (project != null) {
			// if the project is set, get submissions for the project
			page = analysisSubmissionService
					.listSubmissionsForProject(params.getSearchValue(), name, state, workflowIds, project, pageRequest);
		} else {
			// if neither is set, get admin page
			page = analysisSubmissionService
					.listAllSubmissions(params.getSearchValue(), name, state, workflowIds, pageRequest);
		}

		/*
		IRIDA DataTables response expects and object that implements the DataTablesResponseModel interface.
		 */
		List<DataTablesResponseModel> data = new ArrayList<>();
		for (AnalysisSubmission submission : page.getContent()) {
			// Each AnalysisSubmission needs to be converted into a DTAnalysis.
			data.add(createDataTablesAnalysis(submission, locale));
		}

		return new DataTablesResponse(params, page, data);
	}

	/**
	 * Convert a {@link AnalysisSubmission} to a {@link DTAnalysis} to be used by DataTables on the client.
	 *
	 * @param submission {@link AnalysisSubmission}
	 * @param locale     {@link Locale}
	 * @return {@link DTAnalysis}
	 * @throws IridaWorkflowNotFoundException If the requested workflow doesn't exist
	 * @throws ExecutionManagerException      If the submission cannot be read properly
	 */
	private DTAnalysis createDataTablesAnalysis(AnalysisSubmission submission, Locale locale)
			throws IridaWorkflowNotFoundException, ExecutionManagerException {
		Long id = submission.getId();
		String name = submission.getName();
		String submitter = submission.getSubmitter().getLabel();
		Date createdDate = submission.getCreatedDate();
		float percentComplete = 0;
		AnalysisState analysisState = submission.getAnalysisState();
		JobError error = null;
		if (analysisState.equals(AnalysisState.ERROR)) {
			error = getFirstJobError(submission);
		} else {
			percentComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(submission.getId());
		}

		String workflowType = iridaWorkflowsService.getIridaWorkflow(submission.getWorkflowId()).getWorkflowDescription()
				.getAnalysisType().toString();
		String workflow = messageSource.getMessage("workflow." + workflowType + ".title", null, locale);
		String state = messageSource.getMessage("analysis.state." + analysisState
				.toString(), null, locale);
		Long duration = 0L;
		if (analysisState.equals(AnalysisState.COMPLETED)) {
			duration = getDurationInMilliseconds(submission.getCreatedDate(), submission.getAnalysis().getCreatedDate());
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean updatePermission = updateAnalysisPermission.isAllowed(authentication, submission);

		return new DTAnalysis(id, name, submitter, percentComplete, createdDate, workflow, state, error, duration,
				updatePermission);
	}

	private JobError getFirstJobError(AnalysisSubmission submission) throws ExecutionManagerException {
		return analysisSubmissionService.getFirstJobError(submission.getId());
	}

	/**
	 * Get the milliseconds between two {@link Date}s
	 *
	 * @param start {@link Date}
	 * @param end   {@link Date}
	 * @return {@link Long} milliseconds
	 */
	private Long getDurationInMilliseconds(Date start, Date end) {
		Instant startInstant = start.toInstant();
		Instant endInstant = end.toInstant();
		Duration duration = Duration.between(startInstant, endInstant)
				.abs();
		return duration.toMillis();
	}
}
