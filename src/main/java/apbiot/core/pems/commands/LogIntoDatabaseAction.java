package apbiot.core.pems.commands;

import org.mariadb.r2dbc.util.HostAddress;

import apbiot.core.pems.Action;
import marshmalliow.core.database.security.DBCredentials;

public record LogIntoDatabaseAction(String host, int port, String username, String password, String databaseName) implements Action<Void> {
	
	public HostAddress hostAddress() {
		return new HostAddress(host, port);
	}

	public DBCredentials getCredentials() {
		return DBCredentials.builder()
				.host(hostAddress())
				.username(username)
				.password(password)
				.database(databaseName)
				.build();
	}

}
