package data.repositories;

import data.models.vehicles.Bus;
import data.models.vehicles.Truck;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class SessionFactoryUtil {
    private static SessionFactory sessionFactory;

    /**
     * Returns the SessionFactory instance, initializing it if necessary.
     *
     * @return The SessionFactory instance.
     * @throws RuntimeException If the SessionFactory cannot be initialized.
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Load configuration from hibernate.properties or hibernate.cfg.xml
                Configuration configuration = new Configuration();

                // Register annotated classes
                configuration.addAnnotatedClass(Bus.class);
                configuration.addAnnotatedClass(Truck.class);

                // Apply settings and build the ServiceRegistry
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();

                // Build the SessionFactory
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize SessionFactory", e);
            }
        }
        return sessionFactory;
    }

    /**
     * Shuts down the SessionFactory and releases all resources.
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}