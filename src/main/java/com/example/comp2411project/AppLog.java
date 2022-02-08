package com.example.comp2411project;

import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import cn.edu.scau.biubiusuisui.log.FXPlusLoggerFactory;
import cn.edu.scau.biubiusuisui.log.IFXPlusLogger;

public class AppLog {
    private static AppLog instance = new AppLog();

    private static final IFXPlusLogger logger = FXPlusLoggerFactory.getLogger(FXBaseController.class);

    public static AppLog getInstance() {
        return instance;
    }

    public void log(String logs){
        logger.info(logs);
    }

    public void error(String logs){
        logger.error(logs);
    }

    public void warn(String logs){
        logger.warn(logs);
    }

    public void debug(String find, Object... objs) {
        logger.debug(String.format(find, objs));
    }
}
