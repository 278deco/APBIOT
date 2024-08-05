package apbiot.core.pems;

import apbiot.core.pems.events.CommandErrorEvent;
import apbiot.core.pems.events.CommandListBuildEvent;
import apbiot.core.pems.events.CommandReceivedEvent;
import apbiot.core.pems.events.CommandsListParsedEvent;
import apbiot.core.pems.events.ConfigurationLoadedEvent;
import apbiot.core.pems.events.CoreModuleInitializationEvent;
import apbiot.core.pems.events.CoreModuleLaunchEvent;
import apbiot.core.pems.events.CoreModuleShutdownEvent;
import apbiot.core.pems.events.CoreModulesReadyEvent;
import apbiot.core.pems.events.DatabaseCredentialsAcquiredEvent;
import apbiot.core.pems.events.DirectoriesLoadedEvent;
import apbiot.core.pems.events.ExternalAPICredentialsAcquieredEvent;
import apbiot.core.pems.events.FileRegistrationEvent;
import apbiot.core.pems.events.InstanceConnectedEvent;
import apbiot.core.pems.events.InstanceDisconnectedEvent;
import apbiot.core.pems.events.InstanceTokenAcquieredEvent;
import apbiot.core.pems.events.ProgramStoppingEvent;

public enum BaseProgramEventEnum implements ProgramEventEnumerator {
	
	CORE_MODULE_INIT_EVENT(CoreModuleInitializationEvent.class),
	CORE_MODULE_LAUNCH_EVENT(CoreModuleLaunchEvent.class),
	CORE_MODULE_SHUTDOWN_EVENT(CoreModuleShutdownEvent.class),
	CORE_MODULES_READY_EVENT(CoreModulesReadyEvent.class),
	
	CONFIGURATION_LOADED_EVENT(ConfigurationLoadedEvent.class),
	DIRECTORIES_LOADED_EVENT(DirectoriesLoadedEvent.class),
	FILES_REGISTRATION_EVENT(FileRegistrationEvent.class),
	
	EXTERNAL_API_CREDENTIALS_ACQUIERED(ExternalAPICredentialsAcquieredEvent.class),
	DATABASE_CREDENTIALS_ACQUIERED(DatabaseCredentialsAcquiredEvent.class),
	CLIENT_INSTANCE_TOKEN_ACQUIERED(InstanceTokenAcquieredEvent.class),
	
	CLIENT_INSTANCE_CONNECTED(InstanceConnectedEvent.class),
	CLIENT_INSTANCE_DISCONNECTED(InstanceDisconnectedEvent.class),
	
	COMMAND_LIST_BUILD(CommandListBuildEvent.class),
	COMMAND_LIST_PARSED(CommandsListParsedEvent.class),
	COMMAND_RECEIVED(CommandReceivedEvent.class),
	COMMMAND_ERROR(CommandErrorEvent.class),
	
	SHUTTING_DOWN_PROGRAM(ProgramStoppingEvent.class),
	
	UNDEFINED(null);
	
	private Class<? extends ProgramEvent> eventClass;
	private BaseProgramEventEnum(Class<? extends ProgramEvent> cls) {
		this.eventClass = cls;
	}
	
	public Class<? extends ProgramEvent> getEventClass() {
		return eventClass;
	}
}
