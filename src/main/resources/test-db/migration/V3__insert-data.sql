INSERT INTO "authors"("name")
VALUES ('Гайто Газданов'),
	   ('Аркадий Гайдар');

INSERT INTO "books"("title", "price", "amount", "author_id")
VALUES ('Вечер у Клэр', 250, 5, 1), 
	   ('Возвращение Будды', 500, 5, 1),
	   ('Тимур и его команда', 700, 6, 2),
	   ('Чук и Гек', 850, 8, 2);