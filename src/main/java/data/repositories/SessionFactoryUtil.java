package data.repositories;

import data.models.vehicles.Bus;
import data.models.vehicles.Truck;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class SessionFactoryUtil {
    private static volatile SessionFactoryUtil instance;
    private final SessionFactory sessionFactory;
    private static final ThreadLocal<Session> threadLocalSession = new ThreadLocal<>();

    private SessionFactoryUtil() {
        // Load configuration from hibernate.properties
        Configuration configuration = new Configuration();

        //configuration.configure(); // This loads hibernate.properties from the classpath

        // Register annotated classes
        configuration.addAnnotatedClass(Bus.class);
        configuration.addAnnotatedClass(Truck.class);

        // Build the ServiceRegistry
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        // Build the SessionFactory
        this.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    public static SessionFactoryUtil getInstance() {
        if (instance == null) {
            synchronized (SessionFactoryUtil.class) {
                if (instance == null) {
                    instance = new SessionFactoryUtil();
                }
            }
        }
        return instance;
    }

    public Session getSession() {
        Session session = threadLocalSession.get();
        if (session == null || !session.isOpen()) {
            session = sessionFactory.openSession();
            threadLocalSession.set(session);
        }
        return session;
    }

    public void closeSession() {
        Session session = threadLocalSession.get();
        if (session != null) {
            session.close();
            threadLocalSession.remove();
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}