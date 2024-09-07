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

import apbiot.core.exceptions.CoreModuleLaunchingException;
import apbiot.core.exceptions.CoreModuleLoadingException;
import apbiot.core.exceptions.CoreModuleShutdownException;
import apbiot.core.exceptions.NonExistingFileInstanceException;
import apbiot.core.io.NoCloseInputStream;
import apbiot.core.pems.BaseProgramEventEnum;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;
import apbiot.core.pems.ProgramEventManager;
import marshmalliow.core.builder.DotenvManager;
import marshmalliow.core.helpers.SecurityHelper;
import marshmalliow.core.io.JSONLexer;
import marshmalliow.core.io.JSONParser;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.security.AESKeySize;
import marshmalliow.core.security.EncryptionType;
import marshmalliow.core.security.FileCredentials;
import marshmalliow.core.security.SaltSize;

public class CredentialsCoreModule extends CoreModule {

	private static final Path DEFAULT_ENCRYPTED_CREDENTIALS_FILE_NAME = Path.of(".encredentials");
	private static final Path DEFAULT_CREDENTIALS_FILE_NAME = Path.of(".credentials");
	
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

		try {
			boolean assertPresent = false;
			if(Files.exists(DEFAULT_ENCRYPTED_CREDENTIALS_FILE_NAME) && Files.size(DEFAULT_ENCRYPTED_CREDENTIALS_FILE_NAME) != 0)
				assertPresent = true;
			
			if(Files.exists(DEFAULT_CREDENTIALS_FILE_NAME) && Files.size(DEFAULT_CREDENTIALS_FILE_NAME) != 0)
				assertPresent = true;
			
			if(!assertPresent) throw new IOException();
		} catch (IOException e) {
			throw new NonExistingFileInstanceException("Credentials file is missing in root directory or is empty");
		}
	}

	@Override
	public void init() throws CoreModuleLoadingException {
		this.coreHealthy.set(true);
		this.coreRunning.set(true);
		try {
			this.areCredentialsEncrypted = Files.exists(DEFAULT_ENCRYPTED_CREDENTIALS_FILE_NAME);
			
			if(areCredentialsEncrypted) {
			credentialsSettingsBuilder = FileCredentials.builder()
					.encryptionType(EncryptionType.AES_GCM_TAG_128)
					.keySize(AESKeySize.SIZE_256)
					.vectorSize(SaltSize.BYTE_12);
			}
		}finally {
			this.coreRunning.set(false);
		}
	}

	@Override
	public void preLaunch() throws CoreModuleLaunchingException {
		this.coreRunning.set(true);
		InputStream stream = null;
		BufferedReader reader = null;
		JSONLexer lexer = null;
		JSONObject content = null;
		try {
			if(areCredentialsEncrypted) {		
				String keyInput = DotenvManager.get().getEnv("CREDENTIALS_FILE_KEY"); //You can provided the key as an environment variable
				
				if(keyInput == null || keyInput.isEmpty()) { //If the key is not provided as an environment variable, ask for it
					Scanner inputScanner = null;
					
					try {
						System.out.println("A Credentials Key is required for this program to run:");
						
						inputScanner = new Scanner(new NoCloseInputStream(System.in));
						keyInput = inputScanner.next();
						
					}finally {
						if(inputScanner != null) inputScanner.close();
						inputScanner = null;
					}
				}
				
				final SecretKey key = new SecretKeySpec(HexFormat.of().parseHex(keyInput), "AES");
				final FileCredentials credSettings = this.credentialsSettingsBuilder.key(key).build();
				final Cipher cipher = Cipher.getInstance(credSettings.getType().getEncryption());
				
				stream = Files.newInputStream(DEFAULT_ENCRYPTED_CREDENTIALS_FILE_NAME);
				reader = SecurityHelper.decryptWithAESGCM(cipher, stream, credSettings, 128);
				lexer = new JSONLexer(reader);
				final JSONParser parser = new JSONParser(lexer);
				
				content = (JSONObject) parser.parse();
			}else {
				reader = Files.newBufferedReader(DEFAULT_CREDENTIALS_FILE_NAME);
				lexer = new JSONLexer(reader);
				final JSONParser parser = new JSONParser(lexer);
				
				content = (JSONObject) parser.parse();
			}
			
			if(content != null) {
				this.dbCredentialsContent = content.get("database", JSONObject.class);
				
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
			this.coreHealthy.set(false);
			throw new CoreModuleLaunchingException("An unexpected exception was caught during pre-launching of CoreModule "+this.getType().getName(), e);
		}finally {
			this.coreRunning.set(false);
			try { 
				if(stream != null) stream.close();
				if(reader != null) reader.close();
				if(lexer != null) lexer.close();
			} catch (IOException e) {
				this.coreHealthy.set(false);
				throw new CoreModuleLaunchingException("An unexpected exception was caught during pre-launching of CoreModule "+this.getType().getName(), e);
			}
		}
	}
	
	@Override
	public void launch() throws CoreModuleLaunchingException {
		this.coreRunning.set(true);
		try {
			if(this.credentialsContent != null) {
				ProgramEventManager.get().dispatchDedicatedEvent(
					BaseProgramEventEnum.CLIENT_INSTANCE_TOKEN_ACQUIERED, 
					new Object[] {this.credentialsContent.get("client_token")}, 
					Set.of(DiscordCoreModule.class));
				
				ProgramEventManager.get().dispatchEvent(
						BaseProgramEventEnum.CACHE_CREDENTIALS_ACQUIERED,
						new Object[] {this.credentialsContent.get("cache_key")});
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
		}finally {
			this.coreRunning.set(false);
		}
	}

	@Override
	public void postLaunch() throws CoreModuleLaunchingException {
		this.coreRunning.set(true);
		try {
			if(this.credentialsContent != null) {
				this.credentialsContent.clear();
				this.credentialsContent = null;
			}
			if(this.dbCredentialsContent != null) {
				this.dbCredentialsContent.clear();
				this.dbCredentialsContent = null;
			}
			if(this.externalApiCredentialsContent != null) {
				this.externalApiCredentialsContent.clear();
				this.externalApiCredentialsContent = null;
			}
		}finally {
			this.coreRunning.set(false);
		}
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
