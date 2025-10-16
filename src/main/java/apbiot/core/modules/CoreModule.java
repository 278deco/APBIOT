package apbiot.core.modules;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CoreModule implements Comparable<CoreModule> {
	
	//Instance UUID (for this instance only)
	protected final UUID instanceUuid;
	protected Thread coreThread;
	
	protected final AtomicBoolean coreRunning = new AtomicBoolean(false);
	protected final AtomicBoolean coreHealthy = new AtomicBoolean(false);
	
	protected CoreModule(UUID instanceUuid) {
		this.instanceUuid = instanceUuid;
	}
	
	public abstract void executeAssertion();
	
	public abstract void init() throws CoreModuleLoadingException;
	
	public void preLaunch() throws CoreModuleLaunchingException {}
	public abstract void launch() throws CoreModuleLaunchingException;
	public void postLaunch() throws CoreModuleLaunchingException {}
	
	public abstract void shutdown() throws CoreModuleShutdownException;
	
	public abstract CoreModuleType getType();

	public final boolean isCoreRunning() {
		return this.coreRunning.get();
	}
	
	public final boolean isCoreHealthy() {
		return this.coreHealthy.get();
	}
	
	public final UUID getUUID() {
		return instanceUuid;
	}
	
	@Override
	public int compareTo(CoreModule o) {
		return getType().getOrderPriority() - o.getType().getOrderPriority();
	}
	
}
