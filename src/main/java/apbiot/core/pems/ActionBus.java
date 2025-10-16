package apbiot.core.pems;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import apbiot.core.pems.exceptions.ActionDispatchException;

public class ActionBus {

	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
	
	private final Map<Class<?>, SubscriberMethod> subs = new HashMap<>();
	
	public void register(Object listener) {
		try {
			LOCK.writeLock().lock();
			final List<Method> methods = getAllAnnotatedMethods(listener.getClass());
			for(final Method method : methods) {
				final Class<?>[] params = method.getParameterTypes();
				
				if(params.length != 1 || !Action.class.isAssignableFrom(params[0])) {
					throw new IllegalArgumentException("Method "+method+" has @Subscribe annotation but must have exactly one parameter of type Action.");
				}
				
				final Class<?> eventType = params[0];
				method.setAccessible(true);
				
				subs.put(eventType, new SubscriberMethod(listener, method));
				
			}
		} finally {
			LOCK.writeLock().unlock();
		}
	}
	
	private List<Method> getAllAnnotatedMethods(Class<?> clazz) {
		final List<Method> methods = new ArrayList<>();
		while (clazz != null && clazz != Object.class) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.isAnnotationPresent(Subscribe.class) && method.getAnnotation(Subscribe.class).type() == SubscribeType.ACTION) {
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
			subs.remove(listener);
		} finally {
			LOCK.writeLock().unlock();
		}
	}
	
	public <C extends Action<R>, R> R dispatchAction(Action<R> action) throws ActionDispatchException {
		try {
			LOCK.readLock().lock();
			
			final SubscriberMethod method = subs.get(action.getClass());
			if(method != null) {
				return method.handle(action);
			}else {
				throw new ActionDispatchException("No action handler registered for action "+action.getClass());
			}
		} finally {
			LOCK.readLock().unlock();
		}
	}
	
	public boolean hasHandler(Class<? extends Action<?>> actionType) {
		try {
			LOCK.readLock().lock();
			return subs.containsKey(actionType);
		} finally {
			LOCK.readLock().unlock();
		}
	}

	private static class SubscriberMethod {
		private final Object listener;
		private final Method method;
		
		protected SubscriberMethod(Object listener, Method method){
			this.listener = listener;
			this.method = method;
		}
		
		@SuppressWarnings("unchecked")
		public <C extends Action<R>, R> R handle(C action) {
			try {
				return (R) method.invoke(listener, action);
			}catch (Exception e) {
				throw new RuntimeException("Error while invoking action handler method "+method+" on listener "+listener, e);
			}
		}
	}
}
