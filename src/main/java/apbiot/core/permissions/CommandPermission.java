package apbiot.core.permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import discord4j.rest.util.Permission;

public class CommandPermission {
		public static final CommandPermission EMPTY = builder().setNoPermissionRequired(true).build();
	
		private final List<Permissions> permissions;
		private final boolean restrictivePermissions;
		private final boolean isDeveloperCommand;
		private final boolean noPermissionRequired;
		
		private final Optional<String> permissionErrorMessage;
		
		private CommandPermission(CommandPermission.Builder builder) {
			this.permissions = builder.permissions;
			this.restrictivePermissions = builder.setRestrictivePermissions;
			this.isDeveloperCommand = builder.isDeveloperCommand;
			this.noPermissionRequired = builder.noPermissionRequired;
			
			this.permissionErrorMessage = Optional.ofNullable(builder.permissionErrorMessage);
		}
		
		public boolean areNoPermissionsRequired() {
			return noPermissionRequired;
		}
		
		public List<Permissions> getPermissions() {
			return Collections.unmodifiableList(this.permissions);
		}
		
		public Optional<String> getPermissionErrorMessage() {
			return permissionErrorMessage;
		}
		
		public boolean isDeveloperCommand() {
			return isDeveloperCommand;
		}
		
		public boolean areRestrictivePermissions() {
			return restrictivePermissions;
		}

		public static CommandPermission.Builder builder() {
			return new CommandPermission.Builder();
		}
		
		@Override
		public boolean equals(Object obj) {
		return obj instanceof CommandPermission && areEquals((CommandPermission)obj);
		}
		
		private boolean areEquals(CommandPermission perm) {
			return perm.isDeveloperCommand == this.isDeveloperCommand &&
					perm.restrictivePermissions == this.restrictivePermissions &&
					perm.permissions.equals(this.permissions);
		}
		
		public static class Builder {
			private final List<Permissions> permissions;
			
			/**
			 * If true, all users with this/these permission(s) WON'T be able to use the command</br>
			 * If false, all users with this/these permission(s) WILL be able to use the command
			 */
			private boolean setRestrictivePermissions;
			private boolean isDeveloperCommand;
			private boolean noPermissionRequired;
			
			private String permissionErrorMessage;
			
			private Builder() {
				this.permissions = new ArrayList<>();
			}
			
			public Builder setNoPermissionRequired(boolean value) {
				this.noPermissionRequired = value;
				
				return this;
			}
			
			public Builder setDevelopperCommand(boolean value) {
				this.isDeveloperCommand = value;
				
				return this;
			}
			
			public Builder setRestrictivePermissions(boolean value) {
				this.setRestrictivePermissions = value;
				
				return this;
			}
			
			public Builder setPermissionErrorMessage(String msg) {
				this.permissionErrorMessage = msg;
				
				return this;
			}
			
			public Builder addPermission(Permission perm) {
				this.permissions.add(new Permissions(perm));
				
				return this;
			}
			
			public Builder addPermission(Permissions perm) {
				this.permissions.add(perm);
				
				return this;
			}
			
			public Builder addPermissions(Permissions... perms) {
				for(Permissions p : perms) permissions.add(p);
				
				return this;
			}
			
			public Builder addPermissions(Permission... perms) {
				for(Permission p : perms) permissions.add(new Permissions(p));
				
				return this;
			}
			
			public CommandPermission build() {
				return new CommandPermission(this);
			}
		}
	}