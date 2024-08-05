package apbiot.core.io.json;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.objects.Tuple;
import discord4j.core.object.presence.Activity.Type;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.presence.Status;
import discord4j.gateway.intent.IntentSet;
import marshmalliow.core.json.JSONFile;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.objects.Directory;
import marshmalliow.core.security.FileCredentials;

public class JSONClientConfiguration extends JSONFile {

	protected static final Logger LOGGER = LogManager.getLogger(JSONClientConfiguration.class);
	
	public JSONClientConfiguration(Directory dir, String name) {
		super(dir, name);
	}
	
	public JSONClientConfiguration(Directory dir, String name, FileCredentials credentials) {
		super(dir, name, credentials);
	}
	
	public String getPrefix() {
		return getContentAsObject().get("prefix", String.class);
	}
	
	public String getVersion() {
		return getContentAsObject().get("version", String.class);
	}
	
	public ZoneId getTimezone() {
		try {
			return ZoneId.of(getContentAsObject().get("timezone", String.class));
		}catch(DateTimeException e) {
			LOGGER.warn("Couldn't parse configuration's property 'timezone'. Invalid property's name.");
			return ZoneId.of("UCT");
		}
	}
	
	public Tuple<TimeUnit, Integer> getBackupTime() {
		try {
			final JSONObject backupObj = getContentAsObject().get("backup_time", JSONObject.class);
			
			return Tuple.of(TimeUnit.valueOf(backupObj.get("unit", String.class)), backupObj.get("value", Integer.class));
		}catch(Exception e) {
			LOGGER.warn("Couldn't parse configuration's property 'backup_time'. Invalid property's name.");
			return Tuple.of(TimeUnit.HOURS, 1);
		}
	}
	
	public Locale getMainLanguage() {
		return new Locale(getContentAsObject().get("main_language", String.class));
	}
	
	public Locale getSecondaryLanguage() {
		return new Locale(getContentAsObject().get("secondary_language", String.class));
	}
	
	public ClientPresence getClientPresence() {
		try {
			final JSONObject clientObjPresence = getContentAsObject().get("discord_status", JSONObject.class);
		
			final ClientActivity activity = ClientActivity.of(Type.valueOf(clientObjPresence.get("activity", String.class)), clientObjPresence.get("text", String.class), clientObjPresence.get("url", String.class));
			return ClientPresence.of(Status.valueOf(clientObjPresence.get("status", String.class)), activity);
		}catch(Exception e) {
			LOGGER.warn("Couldn't parse configuration's property 'discord_status'. Invalid property's name.");
			return ClientPresence.of(Status.IDLE, null);
		}
	}
	
	public IntentSet getIntentSet() {
		try {
			switch (getContentAsObject().get("intents", String.class).toUpperCase()) {
			case "ALL":
				return IntentSet.all();
			case "NON_PRIVILEGED":
				return IntentSet.nonPrivileged();
			default:
				return IntentSet.none();
			}
		}catch(NullPointerException e) {
			return null;
		}
	}

}
