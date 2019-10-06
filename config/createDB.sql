create table news_blocks
(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    date TEXT,
    title TEXT,
    url TEXT,
    body TEXT,
    processing INTEGER,
    sent INTEGER,
    UNIQUE(title, url, body))

INSERT INTO news_blocks(is_processed, title, url)
SELECT 0,'Билеты на Чемпионат мира по танцевальному спорту', 'http://mosfarr.ru/%d0%b1%d0%b8%d0%bb%d0%b5%d1%82%d1%8b-%d0%bd%d0%b0-%d1%87%d0%b5%d0%bc%d0%bf%d0%b8%d0%be%d0%bd%d0%b0%d1%82-%d0%bc%d0%b8%d1%80%d0%b0-%d0%bf%d0%be-%d1%82%d0%b0%d0%bd%d1%86%d0%b5%d0%b2%d0%b0%d0%bb%d1%8c/'
WHERE NOT EXISTS(SELECT 1 FROM news_blocks WHERE title = 'Билеты на Чемпионат мира по танцевальному спорту' AND url = 'http://mosfarr.ru/%d0%b1%d0%b8%d0%bb%d0%b5%d1%82%d1%8b-%d0%bd%d0%b0-%d1%87%d0%b5%d0%bc%d0%bf%d0%b8%d0%be%d0%bd%d0%b0%d1%82-%d0%bc%d0%b8%d1%80%d0%b0-%d0%bf%d0%be-%d1%82%d0%b0%d0%bd%d1%86%d0%b5%d0%b2%d0%b0%d0%bb%d1%8c/')



 drop table news_blocks;

update news_blocks set id = null where date = '23.09.19';
select * from news_blocks;
delete from news_blocks where id is null;
update news_blocks set sent = 0 where date = '10.09.19';