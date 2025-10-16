package apbiot.core.pems;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import apbiot.core.pems.exceptions.EventDispatchException;

public class EventBus {

	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
	
	private final Map<Class<?>, List<SubscriberMethod>> subs = new HashMap<>();
	
	public void register(Object listener) {
		try {
			LOCK.writeLock().lock();
			final List<Method> methods = getAllAnnotatedMethods(listener.getClass());
			for(final Method method : methods) {
				final Class<?>[] params = method.getParameterTypes();
				
				if(params.length != 1 || !Event.class.isAssignableFrom(params[0])) {
					throw new IllegalArgumentException("Method "+method+" has @Subscribe annotation but must have exactly one parameter of type Event.");
				}
				
				final Class<?> eventType = params[0];
				method.setAccessible(true);
				
				subs.computeIfAbsent(eventType, k -> new ArrayList<>()).add(new SubscriberMethod(listener, method));
				
			}
		} finally {
			LOCK.writeLock().unlock();
		}
	}
	
	private List<Method> getAllAnnotatedMethods(Class<?> clazz) {
		final List<Method> methods = new ArrayList<>();
		while (clazz != null && clazz != Object.class) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.isAnnotationPresent(Subscribe.class) && method.getAnnotation(Subscribe.class).type() == SubscribeType.EVENT) {
					method.setAccessible(true);
					methods.add(method);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return methods;
	}
	
	public void unregister(Object listener) {
		try {
			LOCK.writeLock().lock();
			for(final List<SubscriberMethod> methods : subs.values()) {
				methods.removeIf(sub -> sub.listener.equals(listener));
			}
		} finally {
			LOCK.writeLock().unlock();
		}
	}
	
	/**
	 * Dispatch a {@link Event} to every listeners registered at the time of the call of this method.<br/>
	 * @param event The event to be sent to every listeners
	 * @throws EventDispatchException
	 */
	public void dispatchEvent(Event event) throws EventDispatchException {
		try {
			LOCK.readLock().lock();
			
			for(Map.Entry<Class<?>, List<SubscriberMethod>> entry : subs.entrySet()) {
				if(entry.getKey().isAssignableFrom(event.getClass())) {
					for(final SubscriberMethod sub : entry.getValue()) {
						try {
							sub.method.invoke(sub.listener, event);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new EventDispatchException("Couldn't dispatch event "+event+" to listener "+sub.listener+"!", e);
						}
					}
				}
			}

		} finally {
			LOCK.readLock().unlock();
		}
	}
	
	public int getListenersNumber(Class<? extends Event> eventClass) {
		final List<SubscriberMethod> methods = subs.get(eventClass);
		return methods != null ? methods.size() : 0;
	}
	
	private static class SubscriberMethod {
		private final Object listener;
		private final Method method;
		
		protected SubscriberMethod(Object listener, Method method){
			this.listener = listener;
			this.method = method;
		}
		
	}
}
