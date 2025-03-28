# quarkus-project
### Цели:
- На базовом уровне познакомиться с фреймворком Quarkus
- Изучить механизм миграций баз данных Flyway
- Изучить возможности инеграции Redis в проект на Quarkus
- Получить практический опыт работы с JOOQ

### Концепция:
На фронте должно быть две страницы, условно: все книги и все авторы. На странице с книгами можно попытаться вбить в строку поиска какое-то слово. Результатом будут
все книги в которых либо название либо имя автора содержат в себе введенное слово. Кроме того существуют endpoint'ы для получения книги по id, всех
книг по id автора и просто всех книг. На странице с авторами также можно использовать строку поиска обращение к которой вернет всех авторов имя которых 
содержит введенное слово. Также существуют endpoint'ы для получения автора по id, получения всех авторов которые написали книги содержащие в название какое-то
слово и просто всех авторов.
Помимо прочего я решил самостоятельно написать примитивные классы для осуществления пагинации. Далее я дам краткое описание каждого. 
Есть класс PageInformation, он содержит: номер страницы (по умолчанию 0), размер страницы (по умолчанию 0) и массив строк sort (по умолчанию ["unsorted"]), 
массив строк состоит из названия полей, по которым должна осуществляться сорировка, а в конце указано направление (asc, desc). Я думал над тем, чтобы
реализовать возможность выбора направления сорировки для каждого отдельного поля (условно ["title", "desc", "price", "asc"]), но отказался от этой идеи,
в силу того что практическая польза от этого мне показалась сомнительной. Также есть класс Paginator который с помощью ряда статических методов
принимая объект PageInformation возвращает объект класса Pageable. Этот класс в свою очередь используется в репозитории для осуществления пагинации
единственное отличие его от PageInformation состоит в том, что вместо массива строк представляющего поля для сорировки он содержит объект класса List
в котором хранятся экземпляры класса SortField необходимые для сортировки при использовании JOOQ. Далее, после того как выбираются необходимые данные 
в сервисе из результата создается объект Page. Этот класс содержит в себе некоторую метаинформацию о странице, так то: ее размер, номер, наличие следующий страницы,
наличие предыдующей, общее колличество страниц и элементов, а также сам контент. Информации о колличестве элементов получается при помощи объекта реализуюшего интерфейс
TotalElementcCacheService. Этот класс позволяет по условию выборки получить закешериванное колличество элементов найденных в соответствии с этим условием. Если значение 
этого условия не закешериванно, то оно вычисляется и добавляется в кеш. Если вносятся изменения в таблицу, то кеш сбрасывается. В силу того, что внесение изменения в 
отдельную таблицу может оказать влияние на неограниченное и заранее неизвестное колличество условий кеш всегда сбрасывается. Это может ставить под сомнение в целом
целесообразность наличия кеша как такового, но в силу того, что конкретно в данном сервисе колличетство операций чтения из базы по моему рассчету должно значительно превышать
количество операций изменения, я все-таки решил что оставить кеш имеет смысл.

### Компоненты:
- DTO-шки
- BaseExceptionHandler - обработчик ошибок, я написал его для того чтобы вывести более
подробную информацию о некоторых возникающих в процессе обработки запроса ошибках
- ***Resource - endpoint'ы в которых определен mapping и обработка пользовательских запросов
- ***Service - сервисы в которых подготавливается запрос к базе, а потом формируется объект Page
- Paginator - подготавливает объекты Pageable 
- ***Repository - объекты для доступа к базе
