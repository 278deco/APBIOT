package apbiot.core.pems.events;

import apbiot.core.pems.ProgramEvent;
import marshmalliow.core.json.objects.JSONObject;

public class ExternalAPICredentialsAcquieredEvent extends ProgramEvent {

	public ExternalAPICredentialsAcquieredEvent(Object[] arguments) {
		super(arguments);
	}

	public JSONObject getServiceCredentials(String serviceName) {
		final JSONObject data = (JSONObject)getEventArgument(0);
		if(data != null) {
			final String keyName = serviceName+"_api";
			
			return data.containsKey(keyName) ? (JSONObject)data.get(keyName) : new JSONObject();
		}
		
		return new JSONObject();
	}
	
	@Override
	public EventPriority getPriority() {
		return EventPriority.HIGH;
	}

}
