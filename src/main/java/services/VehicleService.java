package services;

import data.models.vehicles.Vehicle;
import data.repositories.IGenericRepository;
import jakarta.persistence.criteria.JoinType;
import services.common.exceptions.VehicleNotFoundException;
import services.data.dto.vehicles.VehicleCreateDto;
import services.data.dto.vehicles.VehicleUpdateDto;
import services.data.dto.vehicles.VehicleViewDto;
import services.data.mapping.mappings.VehicleMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class VehicleService {

    private final IGenericRepository<Vehicle, Long> vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(IGenericRepository<Vehicle, Long> vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    // ### CRUD Operations ###

    /**
     * Adds a new vehicle asynchronously using a DTO.
     *
     * @param createDto The DTO containing vehicle data.
     * @return A CompletableFuture representing the completion of the operation.
     */
    public CompletableFuture<Void> addVehicleAsync(VehicleCreateDto createDto) {
        return CompletableFuture.runAsync(() -> {
            Vehicle vehicle = vehicleMapper.toEntity(createDto);
            vehicleRepository.addAsync(vehicle).join();
        });
    }

    /**
     * Retrieves a vehicle by ID asynchronously and returns it as a DTO.
     *
     * @param id The ID of the vehicle.
     * @return A CompletableFuture containing the vehicle DTO, if found.
     * @throws VehicleNotFoundException If the vehicle is not found.
     */
    public CompletableFuture<VehicleViewDto> getVehicleByIdAsync(Long id) {
        return vehicleRepository.getByIdAsync(id)
                .thenApply(vehicleOpt -> vehicleOpt.map(vehicleMapper::toViewDto)
                        .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with ID: " + id)));
    }

    /**
     * Retrieves all vehicles asynchronously with pagination and sorting, and returns them as DTOs.
     *
     * @param page           The page number (starting from 0).
     * @param pageSize       The number of vehicles per page.
     * @param fetchRelations A map of relations to fetch (e.g., "transportCompany" -> JoinType.LEFT).
     * @param orderBy        The field to sort by.
     * @param ascending      Whether to sort in ascending order.
     * @return A CompletableFuture containing the list of vehicle DTOs.
     */
    public CompletableFuture<List<VehicleViewDto>> getAllVehiclesAsync(int page, int pageSize, Map<String, JoinType> fetchRelations, String orderBy, boolean ascending) {
        return vehicleRepository.getAllAsync(page, pageSize, fetchRelations, orderBy, ascending)
                .thenApply(vehicles -> vehicles.stream()
                        .map(vehicleMapper::toViewDto)
                        .toList());
    }

    /**
     * Updates a vehicle asynchronously using a DTO.
     *
     * @param updateDto The DTO containing updated vehicle data.
     * @return A CompletableFuture representing the completion of the operation.
     * @throws VehicleNotFoundException If the vehicle to update is not found.
     */
    public CompletableFuture<Void> updateVehicleAsync(VehicleUpdateDto updateDto) {
        return vehicleRepository.getByIdAsync(updateDto.getId())
                .thenCompose(vehicleOpt -> {
                    if (vehicleOpt.isPresent()) {
                        Vehicle vehicle = vehicleOpt.get();
                        vehicleMapper.updateEntityFromDto(updateDto, vehicle); // Update the existing entity
                        return vehicleRepository.updateAsync(vehicle);
                    } else {
                        return CompletableFuture.failedFuture(new VehicleNotFoundException("Vehicle not found with ID: " + updateDto.getId()));
                    }
                });
    }

    /**
     * Deletes a vehicle asynchronously.
     *
     * @param id The ID of the vehicle to delete.
     * @return A CompletableFuture representing the completion of the operation.
     */
    /**
     * Deletes a vehicle asynchronously.
     *
     * @param id The ID of the vehicle to delete.
     * @return A CompletableFuture representing the completion of the operation.
     * @throws VehicleNotFoundException If the vehicle to delete is not found.
     */
    public CompletableFuture<Void> deleteVehicleAsync(Long id) {
        return vehicleRepository.getByIdAsync(id)
                .thenCompose(vehicleOpt -> {
                    if (vehicleOpt.isPresent()) {
                        return vehicleRepository.deleteAsync(vehicleOpt.get());
                    } else {
                        return CompletableFuture.failedFuture(new VehicleNotFoundException("Vehicle not found with ID: " + id));
                    }
                });
    }

    // ### Custom Query Methods ###

    /**
     * Finds vehicles by criteria asynchronously and returns them as DTOs.
     *
     * @param conditions   A map of field-value pairs to filter by.
     * @param orderByField The field to sort by.
     * @param ascending    Whether to sort in ascending order.
     * @return A CompletableFuture containing the list of matching vehicle DTOs.
     */
    public CompletableFuture<List<VehicleViewDto>> findVehiclesByCriteriaAsync(Map<String, Object> conditions, String orderByField, boolean ascending) {
        return vehicleRepository.findByCriteriaAsync(conditions, orderByField, ascending)
                .thenApply(vehicles -> vehicles.stream()
                        .map(vehicleMapper::toViewDto)
                        .toList());
    }

    /**
     * Finds all vehicles with a specific registration plate asynchronously and returns them as DTOs.
     *
     * @param registrationPlate The registration plate to search for.
     * @return A CompletableFuture containing the list of matching vehicle DTOs.
     */
    public CompletableFuture<List<VehicleViewDto>> findVehiclesByRegistrationPlateAsync(String registrationPlate) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("registrationPlate", registrationPlate);
        return findVehiclesByCriteriaAsync(conditions, "registrationPlate", true);
    }

    /**
     * Finds all vehicles of a specific type asynchronously and returns them as DTOs.
     *
     * @param vehicleType The class type of the vehicle (e.g., Van.class, Bus.class).
     * @return A CompletableFuture containing the list of matching vehicle DTOs.
     */
    public CompletableFuture<List<VehicleViewDto>> findVehiclesByTypeAsync(Class<? extends Vehicle> vehicleType) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("class", vehicleType);
        return findVehiclesByCriteriaAsync(conditions, "registrationPlate", true);
    }
}
