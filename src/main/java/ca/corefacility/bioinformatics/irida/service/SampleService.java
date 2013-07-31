package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
/**
 * A service class for working with samples.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface SampleService extends CRUDService<Long, Sample> {

    /**
     * Add a {@link SequenceFile} to a {@link Sample}.
     *
     * @param sample     the {@link Sample} that the {@link SequenceFile} belongs to.
     * @param sampleFile the {@link SequenceFile} that we're adding.
     * @return the {@link Relationship} created between the two entities.
     */
    public Join<Sample, SequenceFile> addSequenceFileToSample(Sample sample, SequenceFile sampleFile);

    /**
     * Get a specific instance of a {@link Sample} that belongs to a {@link Project}. If the {@link Sample} is not
     * associated to the {@link Project} (i.e., no {@link Relationship} is shared between the {@link Sample} and
     * {@link Project}, then an {@link EntityNotFoundException} will be thrown.
     *
     * @param project    the {@link Project} to get the {@link Sample} for.
     * @param identifier the {@link Identifier} of the {@link Sample}
     * @return the {@link Sample} as requested
     * @throws EntityNotFoundException if no {@link Relationship} exists between {@link Sample} and {@link Project}.
     */
    public Sample getSampleForProject(Project project, Long identifier) throws EntityNotFoundException;
    
    /**
     * Get the list of {@link Sample} that belongs to a specific project.
     * 
     * @param p the {@link Project} to get samples for.
     * @return the collection of samples for the {@link Project}.
     */
    public List<Join<Project, Sample>> getSamplesForProject(Project p);

    /**
     * Move an instance of a {@link SequenceFile} associated with a {@link Sample} to its parent {@link Project}.
     *
     * @param sample       the {@link Sample} from which we're moving the {@link SequenceFile}.
     * @param sequenceFile the {@link SequenceFile} that we're moving.
     * @return the new relationship between the {@link Project} and {@link SequenceFile}.
     */
    public void removeSequenceFileFromSample(Sample sample, SequenceFile sequenceFile);
}
