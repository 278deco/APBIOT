package apbiot.core.modules;

import java.time.Duration;
import java.util.UUID;

import org.mariadb.r2dbc.util.HostAddress;

import apbiot.core.modules.exceptions.CoreModuleLaunchingException;
import apbiot.core.modules.exceptions.CoreModuleLoadingException;
import apbiot.core.modules.exceptions.CoreModuleShutdownException;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;
import apbiot.core.pems.events.DatabaseCredentialsAcquiredEvent;
import marshmalliow.core.database.DBFactory;
import marshmalliow.core.database.security.DBCredentials;

public class DatabaseCoreModule extends CoreModule {
	
	private HostAddress host;
	private String username;
	private String password;
	private String databaseName;
	
	protected DatabaseCoreModule() {
		super(UUID.randomUUID());
	}

	@Override
	public void executeAssertion() {
		try {
			Class.forName("marshmalliow.core.database.DBFactory");
		}catch(ClassNotFoundException e) {
			System.err.println("Cannot find reference for MarshmallIOw class DBFactory. Aborting launch...");
			System.exit(-1);
		}
	}

	@Override
	public void init() throws CoreModuleLoadingException {
		try {
			final DBCredentials credentials = DBCredentials.builder()
					.host(this.host)
					.username(this.username)
					.password(this.password)
					.database(this.databaseName)
					.withPool(true) //Hard-coded property
					.poolMaxIdleTime(Duration.ofMinutes(15L)) //Hard-coded property
					.poolMaxSize(16) //Hard-coded property
					.autoCommit(true) //Hard-coded property
					.allowMultiQueries(true) //Hard-coded property
					.build();
		
			DBFactory.newInstance(credentials);
		}catch(IllegalArgumentException e) {
			throw new CoreModuleLoadingException("An error occured while parsing credentials for database");
		}
	}

	@Override
	public void launch() throws CoreModuleLaunchingException {
	}

	@Override
	public void shutdown() throws CoreModuleShutdownException {
	}
	
	@Override
	public void onEventReceived(ProgramEvent e, EventPriority priority) {
		if(priority == EventPriority.HIGH) {
			if(e instanceof DatabaseCredentialsAcquiredEvent) {
				final DatabaseCredentialsAcquiredEvent event = ((DatabaseCredentialsAcquiredEvent)e);
				this.host = event.getHostAddress();
				this.username = event.getUsername();
				this.password = event.getPassword();
				this.databaseName = event.getDatabaseName();
			}
		}
		
	}

	@Override
	public CoreModuleType getType() {
		return CoreModuleType.DB_FACTORY;
	}

}
