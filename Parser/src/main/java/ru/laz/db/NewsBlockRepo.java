package ru.laz.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NewsBlockRepo extends CrudRepository<NewsBlock,Integer>, NewsBlockRepoCustom {

    @Transactional(isolation= Isolation.SERIALIZABLE)
    List<NewsBlock> findBySentAndProcessing(int sent, int processing);

    @Transactional(isolation= Isolation.SERIALIZABLE)
    List<NewsBlock> findBySent(int sent);

    @Transactional(isolation= Isolation.SERIALIZABLE)
    List<NewsBlock> findByProcessing(int proc);

    List<NewsBlock> findByIdIn(List<Integer> ids);

    Iterable<NewsBlock> findAll();



}
