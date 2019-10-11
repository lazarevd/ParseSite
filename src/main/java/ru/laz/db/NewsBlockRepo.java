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


    List<NewsBlock> findBySent(int sent);


    List<NewsBlock> findByProcessing(int proc);

    Iterable<NewsBlock> findAll();



}
