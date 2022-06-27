package apbiot.core.builder;

import java.time.Duration;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.MessageData;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public final class TimedResponse {
	
	private Mono<MessageData> data;
	
	/**
	 * Constructor for TimedMessage
	 * @param message - a message instance
	 * @see discord4j.core.object.entity.Message
	 */
	public TimedResponse(MessageData message) {
		this.data = Mono.just(message);
	}
	
	/**
	 * Constructor for TimedMessage
	 * @param message - a mono message instance
	 * @see discord4j.core.object.entity.Message
	 * @see reactor.core.publisher.Mono
	 */
	public TimedResponse(Mono<MessageData> message) {
		this.data = message;
	}
	
	/**
	 * Used to create a timer that will delete a message
	 * @param time - a duration before the message deletion
	 * @see java.time.Duration
	 * @param executeImmediately - a boolean which define if the countdown is starting at the call of the function or not
	 * @return a message within a mono
	 */
	public Mono<MessageData> setDelayedDelete(MessageChannel chan, Duration time) {
		delete(chan, time).subscribe();
		return data;
	}
	
	/**
	 * Private method to delete the message with the timer
	 * @param time - a duration before the message deletion
	 * @return a Message within a mono
	 */
	private Mono<MessageData> delete(MessageChannel chan, Duration time) {
		if(time.isNegative()) {
			return data.doOnSuccess(v -> safeDelete(chan, v));
		}else {
			return data.delayElement(time, Schedulers.boundedElastic()).doOnSuccess(v -> safeDelete(chan, v));
		}
	}
	
	private void safeDelete(MessageChannel chan, MessageData data) {
		try {
			chan.getMessageById(Snowflake.of(data.id())).block().delete().block();
		}catch(Exception e) {
		}
	}
}
