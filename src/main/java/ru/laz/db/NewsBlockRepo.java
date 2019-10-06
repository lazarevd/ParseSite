package ru.laz.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface NewsBlockRepo extends CrudRepository<NewsBlock,Integer> {

    @Modifying
    @Transactional
    @Query(value = "insert or replace into news_blocks (date, title, url, body, sent) values (:date, :title, :url, :body, :sent)", nativeQuery = true)
    void insertOrIgnore(@Param("date") String date, @Param("title") String title, @Param("url") String url, @Param("body") String body, @Param("sent") int sent);

    @Query( "select nb from NewsBlock nb where sent = :sent" )
    List<NewsBlock> findBySent(int sent);

    Iterable<NewsBlock> findAll();



}
