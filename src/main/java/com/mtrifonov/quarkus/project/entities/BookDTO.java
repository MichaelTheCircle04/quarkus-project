package com.mtrifonov.quarkus.project.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
	
	private long bookId;
	private String title;
	private String name;
	private int authorId;
	private int price;
	private int amount;
}
