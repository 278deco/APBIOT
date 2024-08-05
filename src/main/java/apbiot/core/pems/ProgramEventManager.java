package apbiot.core.pems;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import apbiot.core.pems.exceptions.EventDispatchException;

public class ProgramEventManager {

	private static volatile ProgramEventManager instance;
	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
	
	private final Set<EventListener> listeners;
	
	private ProgramEventManager() {
		this.listeners = ConcurrentHashMap.newKeySet();
	}
	
	public static ProgramEventManager get() {
		if(instance == null) {
			synchronized (ProgramEventManager.class) {
				if(instance == null) instance = new ProgramEventManager();
			}
		}

		return instance;
	}
	
	/**
	 * Dispatch a {@link ProgramEvent} to every listeners registered at the time of the call of this method.<br/>
	 * No verification are made before sending an event with specific arguments. If the arguments sent doesn't match required ones,
	 * some check needs to be performed in the event class itself. 
	 * @param event The event to be sent to every listeners
	 * @param eventArguments The argument populating the event. Can be null or empty.
	 * @throws EventDispatchException
	 * @see EventListener#onEventReceived(ProgramEvent, apbiot.core.pems.ProgramEvent.EventPriority)
	 */
	public void dispatchEvent(ProgramEventEnumerator event, Object[] eventArguments) throws EventDispatchException {
		try {
			LOCK.readLock().lock();
			
			final ProgramEvent peObj = buildEventClass(event, eventArguments);
			listeners.forEach(listener -> listener.onEventReceived(peObj, peObj.getPriority()));
		}finally {
			LOCK.readLock().unlock();
		}
	}
	
	
	public void dispatchEvent(ProgramEventEnumerator event) throws EventDispatchException {
		dispatchEvent(event, new Object[] {});
	}
	
	public void dispatchDedicatedEvent(ProgramEventEnumerator event, Object[] eventArguments, Class<? extends EventListener>[] dedicatedListeners) throws EventDispatchException {
		final Set<Class<? extends EventListener>> dedicatedListenersSet = new HashSet<>(dedicatedListeners.length);
		Collections.addAll(dedicatedListenersSet, dedicatedListeners);
		dispatchDedicatedEvent(event, eventArguments, dedicatedListenersSet);
	}

	public void dispatchDedicatedEvent(ProgramEventEnumerator event, Class<? extends EventListener>[] dedicatedListeners) throws EventDispatchException {
		dispatchDedicatedEvent(event, new Object[] {}, dedicatedListeners);
	}
	
	public void dispatchDedicatedEvent(ProgramEventEnumerator event, Object[] eventArguments, Set<Class<? extends EventListener>> dedicatedListeners) throws EventDispatchException {
		try {
			LOCK.readLock().lock();
			final ProgramEvent peObj = buildEventClass(event, eventArguments);
			
			listeners.forEach(listener -> {
				if(dedicatedListeners.contains(listener.getClass())) listener.onEventReceived(peObj, peObj.getPriority());
			});
		}finally {
			LOCK.readLock().unlock();
		}
	}
	
	public void dispatchDedicatedEvent(ProgramEventEnumerator event, Set<Class<? extends EventListener>> dedicatedListeners) throws EventDispatchException {
		dispatchDedicatedEvent(event, new Object[] {}, dedicatedListeners);
	}
	
	private ProgramEvent buildEventClass(ProgramEventEnumerator event, Object[] eventArguments) throws EventDispatchException {
		Constructor<? extends ProgramEvent> constructor;
		try {
			constructor = event.getEventClass().getConstructor(Object[].class);
			final ProgramEvent peObj = constructor.newInstance(new Object[] {eventArguments});
			
			return peObj;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | 
				IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new EventDispatchException("Couldn't dispatch the event for key "+event+"!");
		}
		
	}
	
	public boolean addNewListener(EventListener listener) {
		try {
			LOCK.writeLock().lock();
			return this.listeners.add(listener);
		}finally {
			LOCK.writeLock().unlock();
		}
	}
	
	public boolean removeListener(EventListener listener) {
		try {
			LOCK.writeLock().lock();
			return this.listeners.add(listener);
		}finally {
			LOCK.writeLock().unlock();
		}
	}
	
	public int getListenersNumber() {
		return this.listeners.size();
	}
}
