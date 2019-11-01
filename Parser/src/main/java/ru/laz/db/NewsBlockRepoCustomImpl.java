package ru.laz.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class NewsBlockRepoCustomImpl implements NewsBlockRepoCustom {

    private static final Logger logger = LoggerFactory.getLogger(NewsBlockRepoCustomImpl.class);
    @PersistenceContext
    EntityManager em;
    @Override
    @Transactional(isolation= Isolation.SERIALIZABLE)
    public void insertF(NewsBlock nb) {
        NewsBlock nbl = em.find(NewsBlock.class, nb.getId());
        //em.detach(nbl);
        if (null == nbl) {
            logger.debug("Not Exist! " + nb.getId());
            em.persist(nb);
        } else {
            logger.debug("Exist " + nb.getId());
        }

    }
}
