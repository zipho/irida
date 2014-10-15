package ca.corefacility.bioinformatics.irida.utils.model;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLink;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteRepository;

/**
 * Testing entity for {@link RemoteRepository}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class RemoteIdentifiableTestEntity extends IdentifiableTestEntity implements RemoteResource {
	private List<RESTLink> links;

	@Override
	public String getIdentifier() {
		return this.getId().toString();
	}

	@Override
	public void setIdentifier(String identifier) {
		this.setId(Long.parseLong(identifier));
	}

	@Override
	public List<RESTLink> getLinks() {
		return links;
	}

	@Override
	public void setLinks(List<RESTLink> links) {
		this.links = links;
	}

}
