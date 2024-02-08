drop table if exists comments, requests, bookings, items, users;
DROP sequence if exists users_id_seq, requests_id_seq, items_id_seq, bookings_id_seq, comments_id_seq;
create sequence users_id_seq;
create sequence requests_id_seq;
create sequence items_id_seq;
create sequence bookings_id_seq;
create sequence comments_id_seq;


create table users (
  id BIGINT not null default nextval('users_id_seq'),
  name VARCHAR(255) not null,
  email VARCHAR(512) not null,
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id)
);

create table requests (
	id BIGINT not null default nextval('requests_id_seq'),
	description varchar(1024),
	requestor_id bigint references users (id) on delete cascade not null,
	constraint pk_request primary key (id)
);

create table items (
	id BIGINT not null default nextval('items_id_seq'),
	name varchar(255) not null,
	description varchar(1024),
	is_available boolean not null,
	owner_id bigint references users (id) on delete cascade not null,
	request_id bigint references requests (id) on delete cascade,
	constraint pk_item primary key (id)
);

create table bookings (
	id BIGINT not null default nextval('bookings_id_seq'),
	start_date timestamp not null,
	end_date timestamp not null check(start_date < end_date),
	item_id bigint references items (id) on delete cascade not null,
	booker_id bigint references users (id) on delete cascade not null,
	status varchar(50) not null,
	constraint pk_booking primary key (id)
);

create table comments (
	id BIGINT not null default nextval('comments_id_seq'),
	text varchar(1024) not null,
	item_id bigint references items (id) on delete cascade not null,
	author_id bigint references users (id) on delete cascade not null,
	created timestamp not null,
	constraint uq_comment unique (item_id, author_id),
	constraint pk_comment primary key (id)
);