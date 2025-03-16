package com.mtrifonov.quarkus.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
	
	private Long bookId;
	@NotNull
	private String title;
	private String name;
	@NotNull
	private Integer authorId;
	@NotNull
	private Integer price;
	@NotNull
	private Integer amount;
}
