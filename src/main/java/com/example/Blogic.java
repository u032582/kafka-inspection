package com.example;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Request;
import com.example.entity.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Blogic {
	public static final String TOPIC_NAME = "quickstart-events";
	@Autowired
	private RequestRepository requestRepository;
	@Autowired
	private ResultRepository resultRepository;
	@Autowired
	private KafkaTemplate<String, Request> producer;

	@Transactional(transactionManager = "transactionManager")
	@KafkaListener(topics = TOPIC_NAME, groupId = "test")
	public void consume(final ConsumerRecord<Long, Request> consumerRecord) {
		var req = consumerRecord.value();
		log.info("received partition={} {}", consumerRecord.partition(), req);
		var result = Result.builder().recieveDate(toDate(LocalDateTime.now())).sendDate(req.getSendDate())
				.uuid(req.getUuid()).testName(req.getTestName()).partition(consumerRecord.partition())
				.loopIndex(req.getLoopIndex()).leaderEpoch(consumerRecord.leaderEpoch().get())
				.timestamp(consumerRecord.timestamp()).build();
		resultRepository.save(result);
	}

	@Transactional(transactionManager = "transactionManager")
	public void send(Request req, boolean ex) {
		producer.send(TOPIC_NAME, req).addCallback(result -> {
			final RecordMetadata m;
			if (result != null) {
				m = result.getRecordMetadata();
				log.info("Produced record to topic {} partition {} @ offset {}", m.topic(), m.partition(), m.offset());
			}
		}, exception -> log.error("Failed to produce to kafka", exception));
		requestRepository.save(req);
		if (ex)
			throw new RuntimeException();
	}

	public static Date toDate(LocalDateTime localDateTime) {
		ZoneId zone = ZoneId.systemDefault();
		ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zone);
		Instant instant = zonedDateTime.toInstant();
		return Date.from(instant);
	}

}
