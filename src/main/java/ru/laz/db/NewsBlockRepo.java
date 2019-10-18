package ru.laz.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsBlockRepo extends CrudRepository<NewsBlock,Integer>, NewsBlockRepoCustom {

    List<NewsBlock> findBySentAndProcessing(int sent, int processing);

    List<NewsBlock> findBySent(int sent);

    List<NewsBlock> findByProcessing(int proc);

    List<NewsBlock> findByIdIn(List<Integer> ids);

    Iterable<NewsBlock> findAll();



}
