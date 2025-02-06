package com.mtrifonov.quarkus.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
	
	private long bookId;
	private String title;
	private String name;
	private int authorId;
	private int price;
	private int amount;
}
