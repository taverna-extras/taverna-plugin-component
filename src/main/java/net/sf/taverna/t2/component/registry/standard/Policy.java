package net.sf.taverna.t2.component.registry.standard;

import static java.lang.System.identityHashCode;
import static java.util.Arrays.asList;
import static uk.org.taverna.component.api.Privilege.DOWNLOAD;
import static uk.org.taverna.component.api.Privilege.EDIT;
import static uk.org.taverna.component.api.Privilege.VIEW;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.component.api.SharingPolicy;
import uk.org.taverna.component.api.PermissionCategory;
import uk.org.taverna.component.api.Permissions;
import uk.org.taverna.component.api.Permissions.Permission;
import uk.org.taverna.component.api.Privilege;

abstract class Policy implements SharingPolicy {
	public static final SharingPolicy PUBLIC = new Public();
	public static final SharingPolicy PRIVATE = new Private();

	Policy() {
	}

	public abstract Permission getPermission();

	public static SharingPolicy parsePolicy(Permissions perm) {
		if (perm == null || perm.getPermission().isEmpty())
			return PRIVATE;
		boolean pub = false;
		for (Permission p : perm.getPermission())
			pub |= p.getCategory().equals(PermissionCategory.PUBLIC);
		for (Permission p : perm.getPermission())
			if (p.getId() != null) {
				if (pub)
					return new GroupPublic(p.getId().toString(), p);
				else
					return new Group(p.getId().toString(), p);
			}
		return PUBLIC;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Policy))
			return false;
		return equals((Policy) o);
	}

	@Override
	public abstract int hashCode();

	protected abstract boolean equals(Policy p);

	private static Permission.Privilege priv(Privilege type) {
		Permission.Privilege priv = new Permission.Privilege();
		priv.setType(type);
		return priv;
	}

	static class Public extends Policy {
		@Override
		public String getName() {
			return "Public";
		}

		@Override
		public Permission getPermission() {
			Permission perm = new Permission();
			perm.setCategory(PermissionCategory.PUBLIC);
			perm.getPrivilege().addAll(asList(priv(VIEW), priv(DOWNLOAD)));
			return perm;
		}

		@Override
		protected boolean equals(Policy p) {
			return p instanceof Public;
		}

		@Override
		public int hashCode() {
			return identityHashCode(PUBLIC);
		}
	}

	static class Private extends Policy {
		@Override
		public String getName() {
			return "Private";
		}

		@Override
		public Permission getPermission() {
			return null;
		}

		@Override
		protected boolean equals(Policy p) {
			return p instanceof Private;
		}

		@Override
		public int hashCode() {
			return identityHashCode(PRIVATE);
		}
	}

	static class Group extends Policy {
		private String id;
		private List<Permission.Privilege> privileges;

		public Group(String id) {
			this.id = id;
			this.privileges = asList(priv(VIEW), priv(DOWNLOAD), priv(EDIT));
		}

		private Group(String id, Permission perm) {
			this.id = id;
			this.privileges = new ArrayList<>(perm.getPrivilege());
		}

		@Override
		public String getName() {
			return "Group(" + id + ")";
		}

		@Override
		public Permission getPermission() {
			Permission perm = new Permission();
			perm.setCategory(PermissionCategory.GROUP);
			perm.setId(Integer.parseInt(id));
			perm.getPrivilege().addAll(privileges);
			return perm;
		}

		@Override
		protected boolean equals(Policy p) {
			return (p instanceof Group) && id.equals(((Group) p).id);
		}

		private static final int BASEHASH = Group.class.hashCode();

		@Override
		public int hashCode() {
			return BASEHASH ^ id.hashCode();
		}
	}

	static class GroupPublic extends Group {
		public GroupPublic(String id) {
			super(id);
		}

		private GroupPublic(String id, Permission perm) {
			super(id, perm);
		}
	}
}
