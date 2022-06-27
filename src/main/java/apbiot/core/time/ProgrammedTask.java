package apbiot.core.time;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @deprecated 4.0
 * @see org.quartz.Scheduler;
 * @author 278deco
 */
public class ProgrammedTask {

	private ScheduledExecutorService ses;
	private long initTime, interval;
	private Runnable task;
	
	public ProgrammedTask(ZonedDateTime startingTime, long interval, Runnable t) {
		this.initTime = ZonedDateTime.now().until(startingTime.truncatedTo(ChronoUnit.SECONDS),ChronoUnit.SECONDS);
		this.interval = interval;
		this.task = t;
		this.ses = Executors.newScheduledThreadPool(1);
		
	}
	
	public void schedule() {
		ses.scheduleAtFixedRate(this.task, this.initTime, this.interval, TimeUnit.SECONDS);
	}
	
}
