package apbiot.core.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import apbiot.core.exceptions.NonExistingFileInstanceException;
import apbiot.core.io.NoCloseInputStream;
import apbiot.core.modules.exceptions.CoreModuleLaunchingException;
import apbiot.core.modules.exceptions.CoreModuleLoadingException;
import apbiot.core.modules.exceptions.CoreModuleShutdownException;
import apbiot.core.pems.BaseProgramEventEnum;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;
import apbiot.core.pems.ProgramEventManager;
import marshmalliow.core.helpers.SecurityHelper;
import marshmalliow.core.json.io.JSONLexer;
import marshmalliow.core.json.io.JSONParser;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.security.AESKeySize;
import marshmalliow.core.security.EncryptionType;
import marshmalliow.core.security.FileCredentials;
import marshmalliow.core.security.SaltSize;

public class CredentialsCoreModule extends CoreModule {

	private static final String DEFAULT_ENCRYPTED_CREDENTIALS_FILE_NAME = ".encredentials";
	private static final String DEFAULT_CREDENTIALS_FILE_NAME = ".credentials";
	
	public boolean areCredentialsEncrypted = true;
	
	private JSONObject credentialsContent;
	private JSONObject dbCredentialsContent;
	private JSONObject externalApiCredentialsContent;
	private FileCredentials.Builder credentialsSettingsBuilder;
	
	public CredentialsCoreModule() {
		super(UUID.randomUUID());
	}

	@Override
	public void executeAssertion() {
		if(!Files.exists(Path.of(DEFAULT_CREDENTIALS_FILE_NAME)) && !Files.exists(Path.of(DEFAULT_ENCRYPTED_CREDENTIALS_FILE_NAME))) 
			throw new NonExistingFileInstanceException("Credentials file is missing in root directory");
	}

	@Override
	public void init() throws CoreModuleLoadingException {
		this.areCredentialsEncrypted = Files.exists(Path.of(DEFAULT_ENCRYPTED_CREDENTIALS_FILE_NAME));
		
		if(areCredentialsEncrypted) {
		credentialsSettingsBuilder = FileCredentials.builder()
				.encryptionType(EncryptionType.AES_GCM_TAG_128)
				.keySize(AESKeySize.SIZE_256)
				.vectorSize(SaltSize.BYTE_12);
		}
	}

	@Override
	public void preLaunch() throws CoreModuleLaunchingException {
		InputStream stream = null;
		BufferedReader reader = null;
		JSONLexer lexer = null;
		JSONObject content = null;
		try {
			if(areCredentialsEncrypted) {				
				Scanner inputScanner = null;
				String keyInput;
				
				try {
					System.out.println("A Credentials Key is required for this program to run:");
					
					inputScanner = new Scanner(new NoCloseInputStream(System.in));
					keyInput = inputScanner.next();
					
				}finally {
					if(inputScanner != null) inputScanner.close();
					inputScanner = null;
				}
				
				final SecretKey key = new SecretKeySpec(HexFormat.of().parseHex(keyInput), "AES");
				final FileCredentials credSettings = this.credentialsSettingsBuilder.key(key).build();
				final Cipher cipher = Cipher.getInstance(credSettings.getType().getEncryption());
				
				stream = Files.newInputStream(Path.of(DEFAULT_ENCRYPTED_CREDENTIALS_FILE_NAME));
				reader = SecurityHelper.decryptWithAESGCM(cipher, stream, credSettings, 128);
				lexer = new JSONLexer(reader);
				final JSONParser parser = new JSONParser(lexer);
				
				content = (JSONObject) parser.parse();
			}else {
				reader = Files.newBufferedReader(Path.of(DEFAULT_CREDENTIALS_FILE_NAME));
				lexer = new JSONLexer(reader);
				final JSONParser parser = new JSONParser(lexer);
				
				content = (JSONObject) parser.parse();
			}
			
			if(content != null) {
				this.dbCredentialsContent = (JSONObject)content.get("database");
				
				this.externalApiCredentialsContent = new JSONObject(content.entrySet()
						.stream()
						.filter(entry -> entry.getKey().contains("_api"))
						.collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue)));
				
				this.credentialsContent = new JSONObject(content.entrySet()
						.stream()
						.filter(entry -> !entry.getKey().contains("_api") && !entry.getKey().contains("database"))
						.collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue)));
			}
		}catch(InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException | 
				NullPointerException | IllegalArgumentException | IOException e) {
			throw new CoreModuleLaunchingException("An unexpected exception was caught during pre-launching of CoreModule "+this.getType().getName(), e);
		}finally {
			try { 
				if(stream != null) stream.close();
				if(reader != null) reader.close();
				if(lexer != null) lexer.close();
			} catch (IOException e) {
				throw new CoreModuleLaunchingException("An unexpected exception was caught during pre-launching of CoreModule "+this.getType().getName(), e);
			}
		}
	}
	
	@Override
	public void launch() throws CoreModuleLaunchingException { 
		if(this.credentialsContent != null) {
			ProgramEventManager.get().dispatchDedicatedEvent(
				BaseProgramEventEnum.CLIENT_INSTANCE_TOKEN_ACQUIERED, 
				new Object[] {this.credentialsContent.get("client_token")}, 
				Set.of(DiscordCoreModule.class));
		}
		
		if(this.dbCredentialsContent != null) {
			ProgramEventManager.get().dispatchDedicatedEvent(
					BaseProgramEventEnum.DATABASE_CREDENTIALS_ACQUIERED, 
					new Object[] {this.dbCredentialsContent.get("host"),this.dbCredentialsContent.get("port"),this.dbCredentialsContent.get("username"),this.dbCredentialsContent.get("password"),this.dbCredentialsContent.get("database_name")}, 
					Set.of(DatabaseCoreModule.class));
		}
		
		if(this.externalApiCredentialsContent != null) {
			ProgramEventManager.get().dispatchEvent(
				BaseProgramEventEnum.EXTERNAL_API_CREDENTIALS_ACQUIERED, 
				new Object[] {this.externalApiCredentialsContent});
		}
	}

	@Override
	public void postLaunch() throws CoreModuleLaunchingException {
		this.credentialsContent.clear();
		this.dbCredentialsContent.clear();
		this.externalApiCredentialsContent.clear();
	}
	
	@Override
	public void shutdown() throws CoreModuleShutdownException {
		
	}
	
	@Override
	public void onEventReceived(ProgramEvent e, EventPriority priority) {

	}

	@Override
	public CoreModuleType getType() {
		return CoreModuleType.CREDENTIALS_HOLDER;
	}

}
