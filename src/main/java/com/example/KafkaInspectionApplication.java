package com.example;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Request;

@RestController
@SpringBootApplication
public class KafkaInspectionApplication {
	@Autowired
	private Blogic blogic;

	public static void main(String[] args) {
		SpringApplication.run(KafkaInspectionApplication.class, args);
	}

	@Transactional(transactionManager = "kafkaTransactionManager")
	@GetMapping("/sendrequest")
	public void crateRequest(@RequestParam(name = "testName", defaultValue = "testcase01") String testName,
			@RequestParam(name = "loopNum", defaultValue = "10") int loopNum,
			@RequestParam(name = "intervalMs", defaultValue = "10") int intervalMs,
			@RequestParam(name = "exceptionOccur", required = false, defaultValue = "false") Boolean ex) {

		IntStream.rangeClosed(1, loopNum).forEach(val -> {
			var req = Request.builder().sendDate(Blogic.toDate(LocalDateTime.now())).testName(testName).loopIndex(val)
					.uuid(UUID.randomUUID()).build();
			blogic.send(req, ex);
			try {
				TimeUnit.MICROSECONDS.sleep(intervalMs);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
	}

}
