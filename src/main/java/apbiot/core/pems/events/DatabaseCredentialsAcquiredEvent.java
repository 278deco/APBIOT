package apbiot.core.pems.events;

import org.mariadb.r2dbc.util.HostAddress;

import apbiot.core.pems.ProgramEvent;
import marshmalliow.core.database.security.DBCredentials;

public class DatabaseCredentialsAcquiredEvent extends ProgramEvent {

	public DatabaseCredentialsAcquiredEvent(Object[] arguments) {
		super(arguments);
	}
	
	public String getHost() {
		return getEventArgument(String.class, 0);
	}
	
	public int getPort() {
		return getEventArgument(Integer.class, 1);
	}
	
	public HostAddress getHostAddress() {
		return new HostAddress(getHost(), getPort());
	}
	
	public String getUsername() {
		return getEventArgument(String.class, 2);
	}
	
	public String getPassword() {
		return getEventArgument(String.class, 3);
	}
	
	public String getDatabaseName() {
		return getEventArgument(String.class, 4);
	}
	
	public DBCredentials getCredentials() {
		return DBCredentials.builder()
				.host(getHost(), getPort())
				.username(getUsername())
				.password(getPassword())
				.database(getDatabaseName())
				.build();
	}

	@Override
	public EventPriority getPriority() {
		return EventPriority.HIGH;
	}
}
