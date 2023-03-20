package com.projek.projek.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "view")
public class View {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private Long id;
	
	@Column
	private Long rect;
	@Column
	private String cmt;
	
	@ManyToOne
	@JoinColumn(name = "image_id")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Image image;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	
}
