package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ToolExecution;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaToolParameter;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jmchilton.blend4j.galaxy.JobsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContentsProvenance;
import com.github.jmchilton.blend4j.galaxy.beans.JobDetails;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * A class used to collect provenance data from Galaxy that corresponds to a
 * specific output file generated by an analysis submission.
 * 
 *
 */
public class AnalysisProvenanceServiceGalaxy {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisProvenanceServiceGalaxy.class);
	private static final Set<String> PARAMETERS_TO_IGNORE = Sets.newHashSet("chromInfo", "dbkey", "async_datasets",
			"paramfile", "uuid", "__current_case__", "__index__", "__workflow_invocation_uuid__");
	private static final String ID_PARAM_KEY = "id";
	private static final Joiner KEY_JOINER = Joiner.on(IridaToolParameter.PARAMETER_NAME_SEPARATOR).skipNulls();
	private static final String JSON_TEXT_MAP_INDICATOR = JsonToken.START_OBJECT.asString(); // "{"
	private static final String JSON_TEXT_ARRAY_INDICATOR = JsonToken.START_ARRAY.asString(); // "["
	private static final String EMPTY_VALUE_PLACEHOLDER = null;
	private static final ObjectMapper mapper = new ObjectMapper();

	private final GalaxyHistoriesService galaxyHistoriesService;
	private final ToolsClient toolsClient;
	private final JobsClient jobsClient;

	public AnalysisProvenanceServiceGalaxy(final GalaxyHistoriesService galaxyHistoriesService,
			final ToolsClient toolsClient, final JobsClient jobsClient) {
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.toolsClient = toolsClient;
		this.jobsClient = jobsClient;
	}

	/**
	 * Get an empty value placeholder
	 * @return the placeholder for an empty value
	 */
	public static String emptyValuePlaceholder() {
		return EMPTY_VALUE_PLACEHOLDER;
	}

	/**
	 * Build up a provenance report for a specific file that's attached to the
	 * outputs of an analysis submission.
	 * 
	 * @param remoteAnalysisId
	 *            the identifier of the submission history that the output file
	 *            is attached to on the execution manager (i.e., Galaxy's
	 *            history id).
	 * @param analysisOutputFilename
	 *            the filename to build the report for. This should be the raw
	 *            basename of the file (i.e., only the filename + extension
	 *            part).
	 * @return the complete report for the file.
	 * @throws ExecutionManagerException
	 *             if the history contents could not be shown for the specified
	 *             file.
	 */
	public ToolExecution buildToolExecutionForOutputFile(final String remoteAnalysisId,
			final String analysisOutputFilename) throws ExecutionManagerException {
		final List<HistoryContents> historyContents = galaxyHistoriesService.showHistoryContents(remoteAnalysisId);
		// group the history contents by name. The names that we're interested
		// in starting from should match the filename of the output file.
		final Map<String, List<HistoryContents>> historyContentsByName = historyContents.stream().collect(
				Collectors.groupingBy(HistoryContents::getName));

		final List<HistoryContents> currentContents = historyContentsByName.get(analysisOutputFilename);
		if (currentContents == null || currentContents.isEmpty() || currentContents.size() > 1) {
			throw new ExecutionManagerException("Could not load a unique history contents for the specified filename ["
					+ analysisOutputFilename + "] in history with id [" + remoteAnalysisId + "]");
		}
		final HistoryContentsProvenance currentProvenance = galaxyHistoriesService.showProvenance(
				remoteAnalysisId, currentContents.get(0).getId());

		try {
			final Tool toolDetails = toolsClient.showTool(currentProvenance.getToolId());
	
			return buildToolExecutionForHistoryStep(toolDetails, currentProvenance, remoteAnalysisId);
		} catch (final RuntimeException e) {
			throw new ExecutionManagerException("Failed to build tool execution provenance.", e);
		}
	}

	/**
	 * Build up a complete *tree* of ToolExecution from Galaxy's history
	 * contents provenance objects. Recursively follows predecessors from the
	 * current history.
	 * 
	 * @param toolDetails
	 *            the details of the current tool to build up tool execution
	 *            details for.
	 * @param currentProvenance
	 *            the provenance that corresponds to the tool details.
	 * @param historyId
	 *            the Galaxy ID we should use to extract tool execution
	 *            information.
	 * @return the entire tree of ToolExecutions for the tool and its
	 *         provenance.
	 * @throws ExecutionManagerException
	 *             if we could not get the history contents provenance or the
	 *             tool details for a predecessor of the current tool details or
	 *             provenance.
	 */
	private ToolExecution buildToolExecutionForHistoryStep(final Tool toolDetails,
			final HistoryContentsProvenance currentProvenance, final String historyId) throws ExecutionManagerException {
		final Map<String, Set<String>> predecessors = getPredecessors(currentProvenance);
		final Map<String, Object> parameters = currentProvenance.getParameters();
		// remove keys from parameters that are Galaxy-related (and thus
		// ignorable), or keys that *match* input keys (as mentioned in
		// getPredecessors, the input keys are going to have a numeric
		// suffix and so don't equal the key that we want to remove from the
		// key set):
		/* @formatter:off */
		final Set<String> parameterKeys = parameters.keySet().stream()
				.filter(k -> !PARAMETERS_TO_IGNORE.contains(k))
				.filter(k -> !predecessors.keySet().stream().anyMatch(p -> k.contains(p)))
				.collect(Collectors.toSet());
		/* @formatter:on */

		final Map<String, Object> paramValues = new HashMap<>();
		for (final String parameterKey : parameterKeys) {
			paramValues.put(parameterKey, parameters.get(parameterKey));
		}

		final Set<ToolExecution> prevSteps = new HashSet<>();
		final String toolName = toolDetails.getName();
		final String toolVersion = toolDetails.getVersion();
		final String jobId = currentProvenance.getJobId();
		final JobDetails jobDetails = jobsClient.showJob(jobId);
		final String commandLine = jobDetails.getCommandLine();
		final Map<String, String> paramStrings = buildParamMap(paramValues);

		for (final String predecessorKey : predecessors.keySet()) {
			// arbitrarily select one of the predecessors from the set, then
			// recurse on that predecessor:
			final String predecessor = predecessors.get(predecessorKey).iterator().next();
			final HistoryContentsProvenance previousProvenance = galaxyHistoriesService.showProvenance(historyId,
					predecessor);
			final Tool previousToolDetails = toolsClient.showTool(previousProvenance.getToolId());

			final ToolExecution toolExecution = buildToolExecutionForHistoryStep(previousToolDetails,
					previousProvenance, historyId);
			prevSteps.add(toolExecution);
		}
		return new ToolExecution(prevSteps, toolName, toolVersion, jobId, paramStrings, commandLine);
	}

	/**
	 * Creates a key-value pair set of nodes that feed into the current workflow
	 * node by inspecting the parameters supplied to the current node in
	 * {@link HistoryContentsProvenance}.
	 * 
	 * @param historyContentsProvenance
	 *            the provenance node to find predecessors for.
	 * @return a key-value pair of all inputs, keyed on the name of the input
	 *         collection and values with IDs of input steps.
	 */
	private Map<String, Set<String>> getPredecessors(final HistoryContentsProvenance historyContentsProvenance) {
		final Map<String, Object> params = historyContentsProvenance.getParameters();
		final Map<String, Set<String>> predecessors = new HashMap<>();

		// iterate through the keys and see if any of them are a map. If any are
		// a map, then check to see if any have a key of 'id', that's an input
		// to this step in the workflow.
		for (final Map.Entry<String, Object> param : params.entrySet()) {
			// the keys in the map look like `freebayes_collection12`, and I
			// want to have all values that were inputs from freebayes to be in
			// the same set, so remove the numeric portion of the string. Phil
			// says that this going to change in the next version of Galaxy.
			final String paramKey = param.getKey().replaceAll("\\d+$", "");
			final Object paramValue = param.getValue();
			if (paramValue instanceof Map) {
				// cast to map, then check to see if any of the keys in the map
				// are "id":
				@SuppressWarnings("unchecked")
				final Map<String, Object> mapParam = (Map<String, Object>) paramValue;
				if (mapParam.containsKey(ID_PARAM_KEY)) {
					if (!predecessors.containsKey(paramKey)) {
						predecessors.put(paramKey, new HashSet<>());
					}
					predecessors.get(paramKey).add(mapParam.get(ID_PARAM_KEY).toString());
				}
			} else {
				logger.trace("Skipping parameter with key [" + paramKey + "]; not an ID parameter.");
			}
		}
		return predecessors;
	}

	/**
	 * Build a map of parameter keys to parameter values. This method recurses
	 * on itself when a value in the supplied map contains yet another
	 * collection.
	 * 
	 * @param valueMap
	 *            the key/value collection to write to translate to Map<String,
	 *            String>
	 * @param prefix
	 *            the prefix to use for the keys (as we recurse into nested
	 *            parameter values).
	 * @return a flat key/value collection of all parameters supplied to the in
	 *         valueMap.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, String> buildParamMap(final Map<String, Object> valueMap, final String... prefix) {
		final Map<String, String> paramStrings = new HashMap<>();
		final Set<String> valueMapKeys = valueMap.keySet().stream().filter(k -> !PARAMETERS_TO_IGNORE.contains(k))
				.collect(Collectors.toSet());
		for (final String valueMapKey : valueMapKeys) {
			// append the new key to the end of the prefixes, then create a key
			// for the flattened map using all of the prefixes.
			final String[] prefixes = Arrays.copyOf(prefix, prefix.length + 1);
			prefixes[prefix.length] = valueMapKey;
			final String key = KEY_JOINER.join(prefixes);
			final Object value = valueMap.get(valueMapKey);
			
			if (value == null) {
				// if the string is empty, put a pre-defined "empty" value into
				// the map.
				logger.trace("There's a key with a null value [" + key + "]");
				paramStrings.put(key, EMPTY_VALUE_PLACEHOLDER);
			} else {
				try {
					if (value instanceof Map) {
						// if we already have a map, straight up recurse on the
						// map.
						paramStrings.putAll(buildParamMap((Map<String, Object>) value, prefixes));
					} else if (value instanceof List) {
						// if it's a list, the contents are *probably*
						// Map<String, Object>, or List<String>
						
						// Check class of objects in List, if String just use toString() on List
						if (((List)value).size() == 0 || String.class.equals(((List)value).get(0).getClass())) {
							paramStrings.put(key, value.toString());
						} else {
							final List<Map<String, Object>> valueList = (List<Map<String, Object>>) value;
							for (final Map<String, Object> listMap : valueList) {
								paramStrings.putAll(buildParamMap(listMap, prefixes));
							}
						}
					} else if (value.toString().trim().startsWith(JSON_TEXT_MAP_INDICATOR)) {
						// if we have a JSON Map (something that has '{' as the
						// first character in the String), then parse it with
						// Jackson and then recurse on the parsed map.
						Map<String, Object> jsonValueMap = mapper.readValue(value.toString(), Map.class);
						paramStrings.putAll(buildParamMap(jsonValueMap, prefixes));
					} else if (value.toString().trim().startsWith(JSON_TEXT_ARRAY_INDICATOR)) {
						// if we have a JSON Array (something that has '[' as
						// the first character in the String), then parse it
						// with Jackson, then recurse on *each* of the parsed
						// maps inside the array.
						
						List list = mapper.readValue(value.toString(), List.class);
						
						// Check class of objects in List, if String just use toString() on List
						if (list.size() == 0 || String.class.equals(list.get(0).getClass())) {
							paramStrings.put(key, list.toString());
						} else {
							List<Map<String, Object>> listMap = (List<Map<String, Object>>)list;
							for (final Map<String, Object> jsonValueMap : listMap) {
								paramStrings.putAll(buildParamMap(jsonValueMap, prefixes));
							}
						}
					} else {
						// if none of those things, then we'll just assume it's
						// a string value that we can put into our map.
						paramStrings.put(key, value.toString());
					}
				} catch (final IOException e) {
					logger.error("Unable to parse key [" + key + "] with value (" + value
							+ ") using Jackson, defaulting to calling toString() on this parameter branch.", e);
					paramStrings.put(key, value.toString());
				} catch (final ClassCastException e) {
					logger.error("Unable to cast key [" + key + "] with value (" + value
							+ "), defaulting to calling toString() on this parameter branch.", e);
					paramStrings.put(key, value.toString());
				} catch (final RuntimeException e) {
					logger.error("Unable to handle key [" + key + "] with value (" + value
							+ ") for unknown reasons. defaulting to calling toString() on this parameter branch.", e);
					paramStrings.put(key, value.toString());
				}
			}
		}
		return paramStrings;
	}
}
