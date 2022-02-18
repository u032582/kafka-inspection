package com.example;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Request;
import com.example.entity.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("consumer")
@Component
public class ConsumerBlogic {
	public static final String TOPIC_NAME = "quickstart-events";
	@Value("${app.container-name}")
	private String containerName;
	@Value("${app.is-fail-consumer}")
	private boolean isFailConsumer;

	@Autowired
	private ResultRepository resultRepository;

	private AtomicInteger counter = new AtomicInteger();

	@PostConstruct
	public void init() {
		log.info("container-name: {}", containerName);
	}

	@Transactional(transactionManager = "transactionManager")
	@KafkaListener(batch = "true", topics = TOPIC_NAME, groupId = "test")
	public void consume(final List<ConsumerRecord<Long, Request>> consumerRecords) {
		String recieveId = String.format("%s.%05d", containerName, counter.incrementAndGet());
		int recieveLength = consumerRecords.size();
		for (int i = 0; i < recieveLength; i++) {
			ConsumerRecord<Long, Request> consumerRecord = consumerRecords.get(i);
			if (isFailConsumer)
				if (counter.get() > 5)
					throw new RuntimeException("message index=" + i);
			var req = consumerRecord.value();
			log.info("received partition={} {}", consumerRecord.partition(), req);
			var result = Result.builder().containerName(containerName).recieveDate(toDate(LocalDateTime.now()))
					.sendDate(req.getSendDate()).uuid(req.getUuid()).testName(req.getTestName()).recieveId(recieveId)
					.recieveLength(recieveLength).recieveIndex(i + 1).partition(consumerRecord.partition())
					.loopIndex(req.getLoopIndex()).leaderEpoch(consumerRecord.leaderEpoch().get())
					.timestamp(consumerRecord.timestamp()).build();
			resultRepository.save(result);
		}
	}

	public static Date toDate(LocalDateTime localDateTime) {
		ZoneId zone = ZoneId.systemDefault();
		ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zone);
		Instant instant = zonedDateTime.toInstant();
		return Date.from(instant);
	}

}
