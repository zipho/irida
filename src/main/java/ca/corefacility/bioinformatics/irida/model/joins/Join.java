package ca.corefacility.bioinformatics.irida.model.joins;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.Timestamped;

/**
 * Interface that the join classes should extend. Classes that extend this can
 * add additional fields that can be persisted in the database. Implementations
 * will have a "subject" and "object" that are the 2 fields being joined, then a
 * creation timestamp.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * 
 * @param <SubjectType>
 *            the type of the owning object
 * @param <ObjectType>
 *            the type of the owned object
 */
public interface Join<SubjectType extends IridaThing, ObjectType extends IridaThing>
		extends Timestamped {
	/**
	 * Get the owning object in the relationship.
	 * 
	 * @return the owning object of the relationship.
	 */
	public SubjectType getSubject();

	/**
	 * Set the owning object in the relationship.
	 * 
	 * @param subject
	 *            the owning object in the relationship.
	 */
	public void setSubject(SubjectType subject);

	/**
	 * Get the owned object in the relationship.
	 * 
	 * @return the owned object of the relationship.
	 */
	public ObjectType getObject();

	/**
	 * Set the owned object in the relationship.
	 * 
	 * @param object
	 *            the owned object in the relationship.
	 */
	public void setObject(ObjectType object);
}
