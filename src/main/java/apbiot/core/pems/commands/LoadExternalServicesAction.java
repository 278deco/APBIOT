package apbiot.core.pems.commands;

import apbiot.core.pems.Action;
import marshmalliow.core.json.objects.JSONObject;

public record LoadExternalServicesAction(JSONObject serviceConfiguration) implements Action<Void> {
	
	public JSONObject specificServiceConfiguration(String serviceName) {
		if(serviceConfiguration != null && !serviceConfiguration.isEmpty()) {
			final String keyName = serviceName+"_api";
			
			return serviceConfiguration.containsKey(keyName) ? serviceConfiguration.getJSONObject(keyName) : new JSONObject();
		}
		
		return new JSONObject();
	}

}
