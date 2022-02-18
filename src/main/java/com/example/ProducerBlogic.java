package com.example;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Request;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProducerBlogic {
	@Autowired
	private RequestRepository requestRepository;
	@Autowired
	private KafkaTemplate<String, Request> producer;
	private String containerName;

	@Transactional(transactionManager = "transactionManager")
	public void send(String testName, int loopNum, long intervalMs, boolean ex, int exIndex) {
		for (int i = 1; i <= loopNum; i++) {
			var req = Request.builder().containerName(containerName)
					.sendDate(ConsumerBlogic.toDate(LocalDateTime.now())).testName(testName).loopIndex(i)
					.uuid(UUID.randomUUID()).build();
			this.doSend(req);
			try {
				TimeUnit.MILLISECONDS.sleep(intervalMs);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			if (ex && i == exIndex)
				throw new RuntimeException("an exception occured at index " + i);
		}
	}

	void doSend(Request req) {
		producer.send(ConsumerBlogic.TOPIC_NAME, req).addCallback(result -> {
			final RecordMetadata m;
			if (result != null) {
				m = result.getRecordMetadata();
				log.info("Produced record to topic {} partition {} @ offset {}", m.topic(), m.partition(), m.offset());
			}
		}, exception -> log.error("Failed to produce to kafka", exception));
		requestRepository.save(req);
	}
}
