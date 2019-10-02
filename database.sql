delete from news_blocks

insert or replace into news_blocks (date, title, body) values ('2019-06-09 13:15', 'basic title', 'sample body')

select * from news_blocks

create table news_blocks
(users_id INTEGER IDENTITY,
    date TEXT,
    title TEXT,
    body TEXT,
    UNIQUE(date, title, body))
