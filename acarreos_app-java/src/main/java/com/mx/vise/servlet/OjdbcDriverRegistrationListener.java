package com.mx.vise.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import com.mx.vise.util.ApplicationContextUtils;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;

import net.sf.ehcache.CacheManager;

import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class OjdbcDriverRegistrationListener implements ServletContextListener {

  private static final Logger LOGGER = LogManager.getLogger(OjdbcDriverRegistrationListener.class.getName());

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    LOGGER.debug("Inicia servicio");
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
      try {
        Driver driver = drivers.nextElement();
        DriverManager.deregisterDriver(driver);
      } catch (SQLException ex) {
        LOGGER.error("No fue posible eliminar el driver " + ex);
      }
    }
    try {
      AbandonedConnectionCleanupThread.shutdown();
    } catch (InterruptedException e) {
      LOGGER.warn("SEVERE problem cleaning up: " + e.getMessage());
    }
    try {
      EhCacheCacheManager customCacheManager = (EhCacheCacheManager) ApplicationContextUtils.getApplicationContext()
          .getBean("cacheManager");
      CacheManager cacheMgr = customCacheManager.getCacheManager();
      cacheMgr.shutdown();
    } catch (Exception e) {
      LOGGER.error("No fue posible terminar el cache ", e);
    }
    try {
      Thread t = getThreadByName("Thread-4");
      Method m = Thread.class.getDeclaredMethod("stop0", new Class[] { Object.class });
      m.setAccessible(true);
      m.invoke(t, new ThreadDeath());
    } catch (Exception e) {
      LOGGER.error("No fue posible detener el hilo Thread-4 ", e);
    }
    try {
      Thread t = getThreadByName("Thread-6");
      Method m = Thread.class.getDeclaredMethod("stop0", new Class[] { Object.class });
      m.setAccessible(true);
      m.invoke(t, new ThreadDeath());
    } catch (Exception e) {
      LOGGER.error("No fue posible detener el hilo Thread-6 ", e);
    }
    try {
      Thread t = getThreadByName("Thread-7");
      Method m = Thread.class.getDeclaredMethod("stop0", new Class[] { Object.class });
      m.setAccessible(true);
      m.invoke(t, new ThreadDeath());
    } catch (Exception e) {
      LOGGER.error("No fue posible detener el hilo Thread-7 ", e);
    }
    try {
      Thread t = getThreadByName("Thread-8");
      Method m = Thread.class.getDeclaredMethod("stop0", new Class[] { Object.class });
      m.setAccessible(true);
      m.invoke(t, new ThreadDeath());
    } catch (Exception e) {
      LOGGER.error("No fue posible detener el hilo Thread-8 ", e);
    }
  }

  public Thread getThreadByName(String threadName) {
    for (Thread t : Thread.getAllStackTraces().keySet()) {
      if (t.getName().equals(threadName))
        return t;
    }
    return null;
  }
}
