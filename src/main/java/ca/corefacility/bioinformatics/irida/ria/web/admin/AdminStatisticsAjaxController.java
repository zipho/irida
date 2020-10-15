package ca.corefacility.bioinformatics.irida.ria.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller to handle ajax requests for the Admin Panel statistics page.
 */

@RestController
@RequestMapping("/ajax/statistics")
public class AdminStatisticsAjaxController {

	@Autowired
	public AdminStatisticsAjaxController() {
	}

	/**
	 * Get usage statistics for projects, samples, analyses, and users
	 * for the provided time period
	 *
	 * @param timePeriod The time period for which to retrieve usage stats for
	 * @return dto with usage stats
	 */
	@RequestMapping(value = "/basic", method = RequestMethod.GET)
	public ResponseEntity getAdminStatistics(Long timePeriod) {
		return ResponseEntity.ok("Retrieved statistics on load");
	}

	/**
	 * Get updated usage statistics for projects for the provided time period
	 *
	 * @param timePeriod The time period for which to retrieve updated project usage stats for
	 * @return dto with updated project usage stats
	 */
	@RequestMapping(value = "/projects", method = RequestMethod.GET)
	public ResponseEntity getAdminProjectStatistics(Long timePeriod) {
		return ResponseEntity.ok("Retrieved stats for projects created in the last " + timePeriod);
	}

	/**
	 * Get updated usage statistics for users for the provided time period
	 *
	 * @param timePeriod The time period for which to retrieve updated user usage stats for
	 * @return dto with updated user usage stats
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ResponseEntity getAdminUserStatistics(Long timePeriod) {
		return ResponseEntity.ok("Retrieved stats for users logged in the last " + timePeriod);
	}

	/**
	 * Get updated usage statistics for analyses for the provided time period
	 *
	 * @param timePeriod The time period for which to retrieve updated analyses usage stats for
	 * @return dto with updated analyses usage stats
	 */
	@RequestMapping(value = "/analyses", method = RequestMethod.GET)
	public ResponseEntity getAdminAnalysesStatistics(Long timePeriod) {
		return ResponseEntity.ok("Retrieved stats for analyses run in the last " + timePeriod);
	}

	/**
	 * Get updated usage statistics for samples for the provided time period
	 *
	 * @param timePeriod The time period for which to retrieve updated sample usage stats for
	 * @return dto with updated sample usage stats
	 */
	@RequestMapping(value = "/sample", method = RequestMethod.GET)
	public ResponseEntity getAdminSampleStatistics(Long timePeriod) {
		return ResponseEntity.ok("Retrieved stats for samples created in the last " + timePeriod);
	}
}