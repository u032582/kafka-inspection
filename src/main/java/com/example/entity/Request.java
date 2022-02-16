package com.example.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = @Index(columnList = "uuid"), uniqueConstraints = { @UniqueConstraint(columnNames = { "uuid" }) })
public class Request {
	@Id
	@SequenceGenerator(name = "requests_id_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resuests_id_seq")
	private long id;
	@Column(columnDefinition = "VARCHAR")
	private UUID uuid;

	private long loopIndex;

	@Column(columnDefinition = "TEXT")
	private String testName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date sendDate;

}
