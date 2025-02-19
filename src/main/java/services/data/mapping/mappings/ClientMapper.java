package services.data.mapping.mappings;

import data.models.Client;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import services.data.dto.clients.ClientCreateDto;
import services.data.dto.clients.ClientUpdateDto;
import services.data.dto.clients.ClientViewDto;
import services.data.mapping.ModelMapperConfig;


public class ClientMapper {

    private static final ModelMapper modelMapper = ModelMapperConfig.getModelMapper();
    private final EntityManager entityManager;

    public ClientMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
        configureMappings();
    }

    private void configureMappings() {

        modelMapper.typeMap(ClientCreateDto.class, Client.class)
                .addMappings(mapper -> {
                    mapper.map(ClientCreateDto::getName, Client::setName);
                    mapper.map(ClientCreateDto::getTelephone, Client::setTelephone);
                    mapper.map(ClientCreateDto::getEmail, Client::setEmail);
                });

        modelMapper.typeMap(ClientUpdateDto.class, Client.class)
                .addMappings(mapper -> {
                    mapper.map(ClientUpdateDto::getId, Client::setId);
                    mapper.map(ClientUpdateDto::getName, Client::setName);
                    mapper.map(ClientUpdateDto::getTelephone, Client::setTelephone);
                    mapper.map(ClientUpdateDto::getEmail, Client::setEmail);
                });

        modelMapper.typeMap(Client.class, ClientViewDto.class)
                .addMappings(mapper -> {
                    mapper.map(Client::getName, ClientViewDto::setName);
                    mapper.map(Client::getTelephone, ClientViewDto::setTelephone);
                    mapper.map(Client::getEmail, ClientViewDto::setEmail);
                    mapper.map(Client::getId, ClientViewDto::setId);
                });
    }

    public ClientViewDto toViewDto(Client entity) {
        return modelMapper.map(entity, ClientViewDto.class);
    }

    public Client toEntity(ClientCreateDto dto) {
        return modelMapper.map(dto, Client.class);
    }

    public void updateEntityFromDto(ClientUpdateDto dto, Client entity) {
        modelMapper.map(dto, entity);
    }
}