package apbiot.core.pems;

import org.apache.logging.log4j.Level;

public abstract class LoggableProgramEvent extends ProgramEvent {
	
	public LoggableProgramEvent(Object[] arguments) {
		super(arguments);
	}
	
	public abstract String getLoggerMessage();
	public abstract LogPriority getLogPriority();
	
	public enum LogPriority {
		INFO(Level.INFO),
		WARNING(Level.WARN),
		ERROR(Level.ERROR),
		FATAL(Level.FATAL);
		
		private Level logLevel;
		private LogPriority(Level logLevel) {
			this.logLevel = logLevel;
		}
		
		public Level getLevel() {
			return logLevel;
		}
	}
}
