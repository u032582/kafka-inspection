package com.example;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@SpringBootApplication
public class KafkaInspectionApplication {
	@Autowired
	private ProducerBlogic blogic;
	@Value("${app.topic-partitions}")
	private int partitions;
	@Value("${app.topic-replacas}")
	private int replicas;
	@Value("${app.bootstrap-servers}")
	private String bootstrapServers;
	@Value("${app.container-name}")
	private String containerName;
	@Value("${spring.profiles.active}")
	private String profile;

	public static void main(String[] args) {
		SpringApplication.run(KafkaInspectionApplication.class, args);
	}

	/**
	 * トピックを自動で生成するにはこのBeanを定義する必要がある.
	 */
	@Bean
	public KafkaAdmin admin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		return new KafkaAdmin(configs);
	}

	/**
	 * トピック生成.
	 * 
	 * @return
	 */
	@Bean
	public NewTopic topic() {
		return TopicBuilder.name(ConsumerBlogic.TOPIC_NAME).partitions(partitions).replicas(replicas).build();
	}

	/**
	 * プロデューサーの受付口
	 * 
	 * @param testName
	 * @param loopNum
	 * @param intervalMs
	 * @param ex
	 */
	@Transactional(transactionManager = "kafkaTransactionManager")
	@GetMapping("/sendrequest")
	public void crateRequest(@RequestParam(name = "testName", defaultValue = "testcase01") String testName,
			@RequestParam(name = "loopNum", defaultValue = "10") int loopNum,
			@RequestParam(name = "intervalMs", defaultValue = "10") int intervalMs,
			@RequestParam(name = "exceptionOccur", required = false, defaultValue = "false") Boolean ex,
			@RequestParam(name = "exceptionOccurIndex", required = false, defaultValue = "1") int exIndex) {
		blogic.send(testName, loopNum, intervalMs, ex, exIndex);
	}

}
