package com.totalizator.service.factory;

import com.totalizator.service.BetService;
import com.totalizator.service.CompetitionService;
import com.totalizator.service.UserService;
import com.totalizator.service.impl.BetServiceImpl;
import com.totalizator.service.impl.CompetitionServiceImpl;
import com.totalizator.service.impl.UserServiceImpl;

public class ServiceFactory {
    private static final ServiceFactory instance = new ServiceFactory();
    
    private final UserService userService;
    private final CompetitionService competitionService;
    private final BetService betService;
    
    private ServiceFactory() {
        this.userService = new UserServiceImpl();
        this.competitionService = new CompetitionServiceImpl();
        this.betService = new BetServiceImpl();
    }
    
    public static ServiceFactory getInstance() {
        return instance;
    }
    
    public UserService getUserService() {
        return userService;
    }
    
    public CompetitionService getCompetitionService() {
        return competitionService;
    }
    
    public BetService getBetService() {
        return betService;
    }
}

