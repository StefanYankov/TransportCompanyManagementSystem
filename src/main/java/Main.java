import data.models.vehicles.Bus;
import data.repositories.GenericRepository;
import data.repositories.SessionFactoryUtil;
import org.hibernate.SessionFactory;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        // Initialize SessionFactory and ExecutorService
        SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();
        ExecutorService executorService = Executors.newCachedThreadPool();

        // Create repositories for Bus and Truck
        GenericRepository<Bus, Long> busRepository = new GenericRepository<>(sessionFactory, executorService, Bus.class);

        // Create a Bus
        Bus bus = new Bus();
        bus.setRegistrationPlate("BUS-123");
        bus.setColour("BLUE");
        bus.setModel("Ikarus");
        bus.setVinNumber("VIN123456789");
        bus.setMaxPassengerCapacity(50);
        bus.setCurrentPassengerCapacity(30);


        // Save entities
        busRepository.add(bus);

        // Retrieve entities
        Optional<Bus> retrievedBus = busRepository.getById(1L);
        retrievedBus.ifPresent(b -> System.out.println("Retrieved Bus: " + b.getModel()));

        // Shutdown ExecutorService
        executorService.shutdown();
    }
}