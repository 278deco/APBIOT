package apbiot.core.modules;

import java.time.Duration;
import java.util.UUID;

import org.mariadb.r2dbc.util.HostAddress;

import apbiot.core.pems.Subscribe;
import apbiot.core.pems.SubscribeType;
import apbiot.core.pems.commands.LogIntoDatabaseAction;
import marshmalliow.core.database.DBFactory;
import marshmalliow.core.database.security.DBCredentials;

public class DatabaseCoreModule extends CoreModule {
	
	private HostAddress host;
	private String username;
	private String password;
	private String databaseName;
	
	public DatabaseCoreModule() {
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
		this.coreHealthy.set(true);
	}

	@Override
	public void launch() throws CoreModuleLaunchingException {
		this.coreRunning.set(true);
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
			this.coreHealthy.set(false);
			
			final CoreModuleLaunchingException thrownedE = new CoreModuleLaunchingException("Cannot initialize database connection");
			thrownedE.addSuppressed(e);
			throw thrownedE;
		}finally {
			this.coreRunning.set(false);
		}
	}

	@Override
	public void shutdown() throws CoreModuleShutdownException {
		try {
			DBFactory.get().closeAllConnections();
		}catch(RuntimeException e) {
			throw new CoreModuleShutdownException("Unexpected error while shutting down database connections", e);
		}
	}
	
	@Subscribe(type = SubscribeType.ACTION)
	public void onCredentialsReceived(LogIntoDatabaseAction action) {
		this.host = action.hostAddress();
		this.username = action.username();
		this.password = action.password();
		this.databaseName = action.databaseName();
	}

	@Override
	public CoreModuleType getType() {
		return BaseCoreModuleType.DB_FACTORY;
	}

}
