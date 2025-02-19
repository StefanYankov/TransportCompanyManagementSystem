package services;

import data.models.transportservices.TransportCargoService;
import data.repositories.IGenericRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.common.exceptions.DatabaseEntityNotFoundException;
import services.common.exceptions.ExceptionMessages;
import services.data.dto.transportservices.TransportCargoServiceCreateDto;
import services.data.dto.transportservices.TransportCargoServiceUpdateDto;
import services.data.dto.transportservices.TransportCargoServiceViewDto;
import services.data.mapping.mappings.TransportCargoServiceMapper;


import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;


public class TransportCargoServiceService {

    private static final Logger logger = LoggerFactory.getLogger(TransportCargoServiceService.class);
    private final IGenericRepository<TransportCargoService, Long> transportCargoServiceRepository;

    public TransportCargoServiceService(IGenericRepository<TransportCargoService, Long> transportCargoServiceRepository) {
        this.transportCargoServiceRepository = transportCargoServiceRepository;
    }

    public CompletableFuture<TransportCargoServiceViewDto> getTransportCargoServiceByIdAsync(Long id) {
        return transportCargoServiceRepository.getByIdAsync(id)
                .thenApply(optionalEntity -> optionalEntity
                        .map(TransportCargoServiceMapper::toViewDto)
                        .orElseThrow(() ->
                                new DatabaseEntityNotFoundException(MessageFormat
                                        .format(ExceptionMessages.TRANSPORT_CARGO_SERVICE_NOT_FOUND, id))));
    }

    public CompletableFuture<Void> addTransportCargoServiceAsync(TransportCargoServiceCreateDto dto) {
        TransportCargoService entity = TransportCargoServiceMapper.toEntity(dto);
        return transportCargoServiceRepository.addAsync(entity);
    }

    public CompletableFuture<Void> updateTransportCargoServiceAsync(Long id, TransportCargoServiceUpdateDto dto) {
        return transportCargoServiceRepository.getByIdAsync(id)
                .thenCompose(optionalEntity -> {
                    if (optionalEntity.isPresent()) {
                        TransportCargoService entity = optionalEntity.get();
                        TransportCargoServiceMapper.updateEntityFromDto(dto, entity);
                        return transportCargoServiceRepository.updateAsync(entity);
                    } else {
                        return CompletableFuture.completedFuture(null);
                    }
                });
    }

    public CompletableFuture<Void> deleteTransportCargoServiceAsync(Long id) {
        return transportCargoServiceRepository.getByIdAsync(id)
                .thenCompose(optionalEntity -> {
                    if (optionalEntity.isPresent()) {
                        return transportCargoServiceRepository.deleteAsync(optionalEntity.get());
                    } else {
                        throw new DatabaseEntityNotFoundException(
                                String.format(ExceptionMessages.TRANSPORT_CARGO_SERVICE_NOT_FOUND, id));
                    }
                });
    }

}
