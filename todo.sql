CREATE TABLE IF NOT EXISTS comment (
    id bigint not null auto_increment,
    reated_at datetime(6),
    modified_at datetime(6),
    content varchar(255) not null,
    username varchar(255) not null,
    todo_id bigint,
    primary key (id),
    foreign key (todo_id) references todo(id)
);

CREATE TABLE IF NOT EXISTS todo (
    id bigint not null auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    content varchar(255),
    title varchar(255) not null,
    weather varchar(255),
    primary key (id)
);

CREATE TABLE IF NOT EXISTS user (
    id bigint not null auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    email varchar(255) not null,
    password varchar(255) not null,
    role enum ('ADMIN','USER') not null,
    username varchar(255) not null,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS user_todo (
    id bigint not null auto_increment,
    todo_id bigint,
    user_id bigint,
    primary key (id),
    foreign key (user_id) references user(id),
    foreign key (todo_id) references todo(id)
);