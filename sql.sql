create table news_blocks
(id INTEGER NOT NULL PRIMARY KEY UNIQUE,
    date TEXT,
    title TEXT,
    url TEXT,
    body TEXT,
    processing INTEGER,
    sent INTEGER)
    
 drop table news_blocks;

delete from news_blocks


select sent, processing from news_blocks where id = -176176341
commit
--where id = -1913605742
 order by id;
delete from news_blocks where id = 0;
update news_blocks set sent = 0 where date = '27.11.19';
update news_blocks set processing = 0 where date = '10.09.19';
select * from news_blocks