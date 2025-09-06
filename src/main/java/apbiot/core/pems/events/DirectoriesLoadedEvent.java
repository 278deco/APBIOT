package apbiot.core.pems.events;

import java.util.Set;

import apbiot.core.pems.ProgramEvent;
import marshmalliow.core.objects.Directory;

/**
 * @deprecated since 6.2.3, user {@link DirectoryManagerReadyEvent} instead.
 */
@Deprecated(since = "6.2.3", forRemoval = true)
public class DirectoriesLoadedEvent extends ProgramEvent {

	public DirectoriesLoadedEvent(Object[] arguments) {
		super(arguments);
	}

	@SuppressWarnings("unchecked")
	public Set<Directory> getDirectories() {
		final Object obj = getEventArgument(0);
		
		return obj != null ? (Set<Directory>)obj : Set.of();
	}
	
	@Override
	public EventPriority getPriority() {
		return EventPriority.INTERMEDIATE;
	}

}
