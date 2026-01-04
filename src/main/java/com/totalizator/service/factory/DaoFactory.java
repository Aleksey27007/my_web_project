package com.totalizator.service.factory;

import com.totalizator.dao.*;
import com.totalizator.dao.impl.*;
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

    public BookmakerDao getBookmakerDao() {
        return new BookmakerDaoImpl(connectionPool);
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

