CREATE TABLE people (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(10) NOT NULL,
    phoneNumber VARCHAR(15) NOT NULL);
    
insert into people(name,phoneNumber) value('김빨강','01011112222');
insert into people(name,phoneNumber) value('김주황','01022223333');
insert into people(name,phoneNumber) value('김노랑','01033334444');
insert into people(name,phoneNumber) value('김연두','01044445555');
insert into people(name,phoneNumber) value('김초록','01055556666');
insert into people(name,phoneNumber) value('김파랑','01066667777');
insert into people(name,phoneNumber) value('김보라','01077778888');

select * from people;