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

public class DaoFactory {
    private static final DaoFactory instance = new DaoFactory();
    private final ConnectionPool connectionPool;

    private DaoFactory() {
        this.connectionPool = ConnectionPool.getInstance();
    }

    public static DaoFactory getInstance() {
        return instance;
    }

    public UserDao getUserDao() {
        return new UserDaoImpl(connectionPool);
    }

    public CompetitionDao getCompetitionDao() {
        return new CompetitionDaoImpl(connectionPool);
    }

    public BetDao getBetDao() {
        return new BetDaoImpl(connectionPool, getUserDao(), getCompetitionDao());
    }

    public Dao<BetType, Integer> getBetTypeDao() {
        return new BetTypeDaoImpl(connectionPool);
    }
}

