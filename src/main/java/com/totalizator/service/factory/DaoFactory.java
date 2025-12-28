package com.totalizator.service.factory;

import com.totalizator.dao.BetDao;
import com.totalizator.dao.CompetitionDao;
import com.totalizator.dao.Dao;
import com.totalizator.dao.UserDao;
import com.totalizator.dao.impl.BetDaoImpl;
import com.totalizator.dao.impl.BetTypeDaoImpl;
import com.totalizator.dao.impl.CompetitionDaoImpl;
import com.totalizator.dao.impl.UserDaoImpl;
import com.totalizator.model.BetType;
import com.totalizator.util.ConnectionPool;

/**
 * Factory class for creating DAO instances.
 * Uses Factory Method pattern.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public class DaoFactory {
    private static final DaoFactory instance = new DaoFactory();
    private final ConnectionPool connectionPool;

    /**
     * Private constructor for Singleton pattern.
     */
    private DaoFactory() {
        this.connectionPool = ConnectionPool.getInstance();
    }

    /**
     * Returns the singleton instance of DaoFactory.
     * 
     * @return DaoFactory instance
     */
    public static DaoFactory getInstance() {
        return instance;
    }

    /**
     * Creates UserDao instance.
     * 
     * @return UserDao instance
     */
    public UserDao getUserDao() {
        return new UserDaoImpl(connectionPool);
    }

    /**
     * Creates CompetitionDao instance.
     * 
     * @return CompetitionDao instance
     */
    public CompetitionDao getCompetitionDao() {
        return new CompetitionDaoImpl(connectionPool);
    }

    /**
     * Creates BetDao instance.
     * 
     * @return BetDao instance
     */
    public BetDao getBetDao() {
        return new BetDaoImpl(connectionPool, getUserDao(), getCompetitionDao());
    }

    /**
     * Creates BetTypeDao instance.
     * 
     * @return BetTypeDao instance
     */
    public Dao<BetType, Integer> getBetTypeDao() {
        return new BetTypeDaoImpl(connectionPool);
    }
}

