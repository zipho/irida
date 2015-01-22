package ca.corefacility.bioinformatics.irida.model.workflow.description;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Defines the input labels for a workflow.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class IridaWorkflowInput {

	@XmlElement(name = "sequenceReadsSingle")
	private String sequenceReadsSingle;

	@XmlElement(name = "reference")
	private String reference;

	public IridaWorkflowInput() {
	}

	/**
	 * Builds a new {@link IridaWorkflowInput} object with the given information.
	 * 
	 * @param sequenceReadsSingle
	 *            The label to use for a collection of single-end sequence
	 *            reads.
	 * @param reference
	 *            The label to use for a reference file.
	 */
	public IridaWorkflowInput(String sequenceReadsSingle, String reference) {
		this.sequenceReadsSingle = sequenceReadsSingle;
		this.reference = reference;
	}

	public String getSequenceReadsSingle() {
		return sequenceReadsSingle;
	}

	public String getReference() {
		return reference;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sequenceReadsSingle, reference);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflowInput) {
			IridaWorkflowInput other = (IridaWorkflowInput) obj;

			return Objects.equals(sequenceReadsSingle, other.sequenceReadsSingle)
					&& Objects.equals(reference, other.reference);
		}

		return false;
	}

	public String getSequenceReadsPaired() {
		return "sequence_reads_paired";
	}
}
