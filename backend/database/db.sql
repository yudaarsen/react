CREATE DATABASE mirea_project
	ENCODING 'UTF-8';

\c mirea_project;

CREATE TABLE lang (
	code CHAR(2),
	description VARCHAR(30) NOT NULL,
	PRIMARY KEY(code)
);

CREATE TABLE status (
	code INTEGER,
	low INTEGER NOT NULL,
	high INTEGER NOT NULL,
	initial BOOLEAN DEFAULT FALSE,
	PRIMARY KEY (code)
);

CREATE TABLE statust (
	code INTEGER,
	lang CHAR(2),
	name VARCHAR(50),
	PRIMARY KEY (code, lang),
	FOREIGN KEY (lang) REFERENCES lang(code),
	FOREIGN KEY (code) REFERENCES status(code)
);

CREATE TABLE department (
	department_id INTEGER,
	PRIMARY KEY (department_id)
);

CREATE TABLE departmentt (
	department_id INTEGER,
	lang CHAR(2),
	dep_name VARCHAR(100) NOT NULL,
	PRIMARY KEY (department_id, lang),
	FOREIGN KEY (lang) REFERENCES lang(code),
	FOREIGN KEY (department_id) REFERENCES department(department_id)
);

CREATE TABLE employee (
	emp_id SERIAL,
	department_id INTEGER NOT NULL,
	email VARCHAR(255) UNIQUE,
	phone VARCHAR(15) UNIQUE,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	middle_name VARCHAR(50),
	passwrd CHAR(64) NOT NULL,
	PRIMARY KEY (emp_id),
	FOREIGN KEY (department_id) REFERENCES department(department_id)
);

CREATE TABLE category (
	category_id INTEGER,
	PRIMARY KEY (category_id)
);

CREATE TABLE categoryt (
	category_id INTEGER,
	lang CHAR(2),
	name VARCHAR(50) NOT NULL,
	PRIMARY KEY (category_id, lang),
	FOREIGN KEY (lang) REFERENCES lang(code),
	FOREIGN KEY (category_id) REFERENCES category(category_id)
);

CREATE TABLE dl_property (
	category_id INTEGER,
	department_id INTEGER,
	days INTEGER NOT NULL,
	PRIMARY KEY (category_id, department_id),
	FOREIGN KEY (category_id) REFERENCES category(category_id),
	FOREIGN KEY (department_id) REFERENCES department(department_id)
);

CREATE TABLE appeal (
	appeal_id SERIAL,
	category_id INTEGER,
	department_id INTEGER,
	employee_id INTEGER,
	status_code INTEGER NOT NULL,
	title VARCHAR(80) NOT NULL,
	text_body TEXT NOT NULL,
	apl_fname VARCHAR(50) NOT NULL,
	apl_lname VARCHAR(50) NOT NULL,
	apl_mname VARCHAR(50),
	apl_email VARCHAR(255) NOT NULL,
	create_date TIMESTAMP DEFAULT NOW(),
	deadline TIMESTAMP,
	PRIMARY KEY (appeal_id),
	FOREIGN KEY (category_id) REFERENCES category(category_id),
	FOREIGN KEY (department_id) REFERENCES department(department_id),
	FOREIGN KEY (employee_id) REFERENCES employee(emp_id),
	FOREIGN KEY (status_code) REFERENCES status(code)
);

CREATE TABLE appeal_comm (
	appeal_id INTEGER,
	create_date TIMESTAMP DEFAULT NOW(),
	author INTEGER,
	text_body TEXT,
	PRIMARY KEY (appeal_id, create_date, author),
	FOREIGN KEY (appeal_id) REFERENCES appeal(appeal_id),
	FOREIGN KEY (author) REFERENCES employee(emp_id)
);

CREATE TABLE attachment (
	appeal_id INTEGER,
	attach_num INTEGER,
	name VARCHAR(255) NOT NULL,
	path VARCHAR(255) NOT NULL,
	PRIMARY KEY (appeal_id, attach_num),
	FOREIGN KEY (appeal_id) REFERENCES appeal(appeal_id)
);

INSERT INTO lang VALUES ('RU', 'Russian'), ('EN', 'English');

INSERT INTO status VALUES (10, 10, 20, TRUE),
						  (20, 10, 10, FALSE),
						  (30, 20, 40, FALSE),
						  (40, 30, 30, FALSE),
						  (50, 10, 30, FALSE);
						  
INSERT INTO statust VALUES (10, 'RU', 'Назначение ответственного подразделения'),
						   (20, 'RU', 'Назначение ответственного исполнителя'),
						   (30, 'RU', 'В работе'),
						   (40, 'RU', 'Исполнена'),
						   (50, 'RU', 'Аннулирована'),
						   (10, 'EN', 'Assignment of the responsible unit'),
						   (20, 'EN', 'Assignment of the responsible executor'),
						   (30, 'EN', 'In process'),
						   (40, 'EN', 'Executed'),
						   (50, 'EN', 'Cancelled');

INSERT INTO department VALUES (1);

INSERT INTO departmentt VALUES (1, 'RU', 'IT');

INSERT INTO category VALUES (1), (2), (3);

INSERT INTO categoryt VALUES (1, 'RU', 'Жалоба'), (2, 'RU', 'Вопрос'), (3, 'RU', 'Благодарность');

INSERT INTO dl_property VALUES (1, 1, 5), (2, 1, 3), (3, 1, 10);

INSERT INTO employee VALUES (1, 1, 'test1@mail.ru', '9998883344', 'Александр', 'Воронов', 'Михайлович', encode(sha256('test'), 'hex')),
				(2, 1, 'test2@mail.ru', '9998883355', 'Иван', 'Морозов', 'Алексеевич', encode(sha256('test2'), 'hex')),
				(3, 1, 'test3@mail.ru', '9998883366', 'Мария', 'Иванова', 'Сергеевна', encode(sha256('test3'), 'hex'));
