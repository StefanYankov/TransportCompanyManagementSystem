import data.models.vehicles.Bus;
import data.models.vehicles.CargoType;
import data.models.vehicles.Truck;
import data.repositories.GenericRepository;
import data.repositories.SessionFactoryUtil;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        // Initialize SessionFactory and ExecutorService
        var sessionFactoryUtil = SessionFactoryUtil.getInstance();
        var sessionFactory = sessionFactoryUtil.getSessionFactory();
        ExecutorService executorService = Executors.newCachedThreadPool();

        // Create repositories for Bus and Truck
        GenericRepository<Bus, Long> busRepository = new GenericRepository<>(sessionFactory, executorService, Bus.class);
        GenericRepository<Truck, Long> truckRepository = new GenericRepository<>(sessionFactory, executorService, Truck.class);

        // Create a Bus
        Bus bus = new Bus();
        bus.setRegistrationPlate("BUS-123");
        bus.setVinNumber("VIN123456789");
        bus.setIssueDate(LocalDate.now());
        bus.setMaxPassengerCapacity(50);
        bus.setCurrentPassengerCapacity(30);

        // Create a Truck
        Truck truck = new Truck();
        truck.setRegistrationPlate("TRUCK-456");
        truck.setVinNumber("VIN987654321");
        truck.setIssueDate(LocalDate.now());
        truck.setMaxCargoCapacityKg(10000);
        truck.setCurrentCargoCapacityKg(5000);
        truck.setCargoType(CargoType.REGULAR);

        // Save entities
        busRepository.add(bus);
        truckRepository.add(truck);

        // Retrieve entities
        var retrievedBus = busRepository.getById(bus.getId());
        var retrievedTruck = truckRepository.getById(truck.getId());

        System.out.println("Retrieved Bus: " + retrievedBus);
        System.out.println("Retrieved Truck: " + retrievedTruck);

        // Shutdown ExecutorService
        executorService.shutdown();

        // Close the session (optional, depending on your use case)
        sessionFactoryUtil.closeSession();
    }
}