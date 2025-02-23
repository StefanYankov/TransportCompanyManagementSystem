package data.repositories;

import data.models.Client;
import data.models.TransportCompany;
import data.models.employee.Dispatcher;
import data.models.employee.Driver;
import data.models.employee.Qualification;
import data.models.employee.Employee;
import data.models.transportservices.Destination;
import data.models.transportservices.TransportCargoService;
import data.models.transportservices.TransportPassengersService;
import data.models.transportservices.TransportService;
import data.models.vehicles.*;
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
                configuration.addAnnotatedClass(Employee.class);
                configuration.addAnnotatedClass(Dispatcher.class);
                configuration.addAnnotatedClass(Qualification.class);
                configuration.addAnnotatedClass(Driver.class);

                configuration.addAnnotatedClass(Destination.class);
                configuration.addAnnotatedClass(TransportService.class);
                configuration.addAnnotatedClass(TransportPassengersService.class);
                configuration.addAnnotatedClass(TransportCargoService.class);

                configuration.addAnnotatedClass(Vehicle.class);
                configuration.addAnnotatedClass(TransportCargoVehicle.class);
                configuration.addAnnotatedClass(TransportPeopleVehicle.class);
                configuration.addAnnotatedClass(Truck.class);
                configuration.addAnnotatedClass(Bus.class);
                configuration.addAnnotatedClass(Van.class);

                configuration.addAnnotatedClass(Client.class);
                configuration.addAnnotatedClass(TransportCompany.class);


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