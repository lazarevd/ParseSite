package ru.laz.db;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class NewsBlockRepoCustomImpl implements NewsBlockRepoCustom {

    @PersistenceContext
    EntityManager em;
    @Override
    @Transactional(isolation= Isolation.SERIALIZABLE)
    public void insertF(NewsBlock nb) {
        NewsBlock nbl = em.find(NewsBlock.class, nb.getId());
        System.out.println(nbl);
        //em.detach(nbl);
        if (null == nbl) {
            System.out.println("Not Exist! " + nb.getId());
            em.persist(nb);
        } else {
            System.out.println("Exist " + nb.getId());
        }

    }
}
