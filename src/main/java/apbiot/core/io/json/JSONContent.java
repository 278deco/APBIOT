package apbiot.core.io.json;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 
 * @author 278deco
 * @deprecated 5.0
 * @param <K>
 * @param <V>
 */
public class JSONContent<K, V> extends ConcurrentHashMap<K, V> {

	private static final long serialVersionUID = 4595856122081667102L;

	private final AtomicBoolean contentModified = new AtomicBoolean(false);
	
	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		final boolean result = super.replace(key, oldValue, newValue);
		if(result) this.contentModified.set(true);
		return result;
	}
	
	@Override
	public V replace(K key, V value) {
		final V replacedValue = super.replace(key, value);
		if(replacedValue != null) this.contentModified.set(true);
		return replacedValue;
	}
	
	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		this.contentModified.set(true);
		super.replaceAll(function);
	}
	
	@Override
	public boolean remove(Object key, Object value) {
		final boolean result = super.remove(key, value);
		if(result) this.contentModified.set(true);
		return result;
	}
	
	@Override
	public V remove(Object key) {
		final V removedKey = super.remove(key);
		if(removedKey != null) this.contentModified.set(true);
		return removedKey;
	}
	
	@Override
	public V putIfAbsent(K key, V value) {
		final V previousKey = super.putIfAbsent(key, value);
		if(previousKey == null) this.contentModified.set(true);
		return previousKey;
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		this.contentModified.set(true);
		super.putAll(m);
	}
	
	@Override
	public V put(K key, V value) {
		final V previousValue = super.put(key, value);
		if(previousValue == null || !previousValue.equals(value)) this.contentModified.set(true);
		return previousValue;
	}
	
	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		this.contentModified.set(true);
		return super.merge(key, value, remappingFunction);
	}
	
	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		this.contentModified.set(true);
		return super.computeIfPresent(key, remappingFunction);
	}
	
	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		this.contentModified.set(true);
		return super.computeIfAbsent(key, mappingFunction);
	}
	
	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		this.contentModified.set(true);
		return super.compute(key, remappingFunction);
	}
	
	@Override
	public void clear() {
		this.contentModified.set(true);
		super.clear();
	}

	public void setContentModified(boolean value) {
		this.contentModified.set(value);
	}
	
	public boolean isContentModified() {
		return this.contentModified.get();
	}
	
}
