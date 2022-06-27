package apbiot.core.builder;

import java.time.Duration;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public final class TimedMessage {
	
	private Mono<Message> data;
	
	/**
	 * Constructor for TimedMessage
	 * @param message - a message instance
	 * @see discord4j.core.object.entity.Message
	 */
	public TimedMessage(Message message) {
		this.data = Mono.just(message);
	}
	
	/**
	 * Constructor for TimedMessage
	 * @param message - a mono message instance
	 * @see discord4j.core.object.entity.Message
	 * @see reactor.core.publisher.Mono
	 */
	public TimedMessage(Mono<Message> message) {
		this.data = message;
	}
	
	/**
	 * Used to create a timer that will delete a message
	 * @param time - a duration before the message deletion
	 * @see java.time.Duration
	 * @param executeImmediately - a boolean which define if the countdown is starting at the call of the function or not
	 * @return a message within a mono
	 */
	public Mono<Message> setDelayedDelete(Duration time, boolean executeImmediately) {
		if(executeImmediately) {
			delete(time).subscribe();
			return data;
		}else {
			return delete(time);
		}
	}
	
	/**
	 * Used to create a timer that will send the message
	 * @param chan - the channel where the message will be send
	 * @param time - a duration before the message sending
	 * @see java.time.Duration
	 * @return a MessageChannel within a mono
	 */
	public Mono<MessageChannel> setDelayedSend(Mono<MessageChannel> chan, Duration time) {
		if(time.isNegative()) {
			return chan.doOnSuccess(v -> v.createMessage(data.block().getContent()).block());
		}else {
			return chan.delayElement(time, Schedulers.boundedElastic()).doOnSuccess(v -> v.createMessage(data.block().getContent()).block());
		}
	}
	
	/**
	 * Private method to delete the message with the timer
	 * @param time - a duration before the message deletion
	 * @return a Message within a mono
	 */
	private Mono<Message> delete(Duration time) {
		if(time.isNegative()) {
			return data.doOnSuccess(v -> v.delete().block());
		}else {
			return data.delayElement(time, Schedulers.boundedElastic()).doOnSuccess(v -> safeDelete(v));
		}
	}
	
	private void safeDelete(Message msg) {
		try {
			msg.delete().block();
		}catch(Exception e) {
		}
	}
}
