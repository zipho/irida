package ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics;

/**
 * UI Response to to encapsulate basic usage statistics.
 */

public class BasicStats {
	private Long analysesRan;
	private Long projectsCreated;
	private Long samplesCreated;
	private Long usersCreated;
	private Long usersLoggedIn;

	public BasicStats(Long analysesRan, Long projectsCreated, Long samplesCreated, Long usersCreated,
			Long usersLoggedIn) {
		this.analysesRan = analysesRan;
		this.projectsCreated = projectsCreated;
		this.samplesCreated = samplesCreated;
		this.usersCreated = usersCreated;
		this.usersLoggedIn = usersLoggedIn;
	}

	public Long getAnalysesRan() {
		return analysesRan;
	}

	public void setAnalysesRan(Long analysesRan) {
		this.analysesRan = analysesRan;
	}

	public Long getProjectsCreated() {
		return projectsCreated;
	}

	public void setProjectsCreated(Long projectsCreated) {
		this.projectsCreated = projectsCreated;
	}

	public Long getSamplesCreated() {
		return samplesCreated;
	}

	public void setSamplesCreated(Long samplesCreated) {
		this.samplesCreated = samplesCreated;
	}

	public Long getUsersCreated() {
		return usersCreated;
	}

	public void setUsersCreated(Long usersCreated) {
		this.usersCreated = usersCreated;
	}

	public Long getUsersLoggedIn() {
		return usersLoggedIn;
	}

	public void setUsersLoggedIn(Long usersLoggedIn) {
		this.usersLoggedIn = usersLoggedIn;
	}
}
