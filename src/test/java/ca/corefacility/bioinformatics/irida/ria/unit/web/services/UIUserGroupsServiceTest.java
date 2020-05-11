package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UserGroupTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUserGroupsService;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;

import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UIUserGroupsServiceTest {
	/*
	Mock Data
	 */
	private final User USER_1 = new User(1L, "user1", "user1@nowhere.com", "SDF123", "USER", "ONE", "7777");
	private final UserGroup GROUP_1 = new UserGroup("group 1");
	private final UserGroup GROUP_2 = new UserGroup("group 2");
	private final UserGroup GROUP_3 = new UserGroup("group 3");
	private final TableRequest TABLE_REQUEST = new TableRequest(0, 10, "createdDate", "asc", "");
	private final List<UserGroup> GROUPS = ImmutableList.of(GROUP_1, GROUP_2, GROUP_3);
	private UIUserGroupsService service;
	private UserGroupService userGroupService;

	@Before
	public void setUp() {
		userGroupService = mock(UserGroupService.class);
		MessageSource messageSource = mock(MessageSource.class);
		service = new UIUserGroupsService(userGroupService, messageSource);

		/*
		Mock the principal user
		 */
		USER_1.setSystemRole(Role.ROLE_ADMIN);
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal()).thenReturn(USER_1);

		when(userGroupService.search(any(), any())).thenReturn(getPagedUserGroups());
		when(userGroupService.read(GROUP_1.getId())).thenReturn(GROUP_1);
		when(messageSource.getMessage(anyString(), anyObject(), any())).thenReturn("DONE!");
	}

	@Test
	public void testGetUserGroups() {
		TableResponse<UserGroupTableModel> response = service.getUserGroups(TABLE_REQUEST);
		verify(userGroupService, times(1)).search(any(), any());
		assertEquals("Should be 3 user groups", Long.valueOf(3), response.getTotal());
	}

	@Test
	public void testDeleteUserGroup() {
		service.deleteUserGroup(GROUP_1.getId(), Locale.CANADA);
		verify(userGroupService, times(1)).read(GROUP_1.getId());
		verify(userGroupService, times(1)).delete(GROUP_1.getId());
	}

	private Page<UserGroup> getPagedUserGroups() {
		return new Page<>() {
			@Override
			public int getTotalPages() {
				return 0;
			}

			@Override
			public long getTotalElements() {
				return 3;
			}

			@Override
			public <U> Page<U> map(Function<? super UserGroup, ? extends U> converter) {
				return null;
			}

			@Override
			public int getNumber() {
				return 0;
			}

			@Override
			public int getSize() {
				return 0;
			}

			@Override
			public int getNumberOfElements() {
				return 0;
			}

			@Override
			public List<UserGroup> getContent() {
				return GROUPS;
			}

			@Override
			public boolean hasContent() {
				return false;
			}

			@Override
			public Sort getSort() {
				return null;
			}

			@Override
			public boolean isFirst() {
				return false;
			}

			@Override
			public boolean isLast() {
				return false;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean hasPrevious() {
				return false;
			}

			@Override
			public Pageable nextPageable() {
				return null;
			}

			@Override
			public Pageable previousPageable() {
				return null;
			}

			@Override
			public Iterator<UserGroup> iterator() {
				return GROUPS.iterator();
			}
		};
	}
}
