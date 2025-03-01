package services.data.mapping.mappers;

import data.models.Client;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import services.data.dto.clients.ClientCreateDTO;
import services.data.dto.clients.ClientUpdateDTO;
import services.data.dto.clients.ClientViewDTO;

public class ClientMapper {
    private final ModelMapper modelMapper;

    public ClientMapper() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE);
        configureMappings();
    }

    private void configureMappings() {
        modelMapper.createTypeMap(ClientCreateDTO.class, Client.class)
                .addMappings(mapper -> mapper.skip(Client::setId));
        modelMapper.createTypeMap(Client.class, ClientViewDTO.class)
                .addMapping(Client::getId, ClientViewDTO::setId)
                .addMapping(Client::getName, ClientViewDTO::setName)
                .addMapping(Client::getTelephone, ClientViewDTO::setTelephone)
                .addMapping(Client::getEmail, ClientViewDTO::setEmail);
        modelMapper.createTypeMap(ClientUpdateDTO.class, Client.class)
                .addMapping(ClientUpdateDTO::getId, Client::setId)
                .addMapping(ClientUpdateDTO::getName, Client::setName)
                .addMapping(ClientUpdateDTO::getTelephone, Client::setTelephone)
                .addMapping(ClientUpdateDTO::getEmail, Client::setEmail);
    }

    public Client toEntity(ClientCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("ClientCreateDTO must not be null");
        return modelMapper.map(dto, Client.class);
    }

    public Client toEntity(ClientUpdateDTO dto, Client existing) {
        if (dto == null) throw new IllegalArgumentException("ClientUpdateDTO must not be null");
        modelMapper.map(dto, existing);
        return existing;
    }

    public ClientViewDTO toViewDTO(Client client) {
        if (client == null) return null;
        return modelMapper.map(client, ClientViewDTO.class);
    }
}