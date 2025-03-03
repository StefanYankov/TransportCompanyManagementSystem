package services.IO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IServiceSerializer} for serializing and deserializing DTOs.
 *
 * @param <TService>   The type of the service.
 * @param <TViewDTO>   The type of the view DTO.
 * @param <TCreateDTO> The type of the "create" DTO.
 */
public class ServiceSerializer<TService, TViewDTO, TCreateDTO> implements IServiceSerializer<TService, TViewDTO, TCreateDTO> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceSerializer.class);

    private final TService service;
    private final Class<TViewDTO> viewDtoClass;
    private final Class<TCreateDTO> createDtoClass;
    private final Gson gson;

    /**
     * Constructor for ServiceSerializer.
     *
     * @param service        The service instance.
     * @param viewDtoClass   The class of the view DTO.
     * @param createDtoClass The class of the "create" DTO.
     */
    public ServiceSerializer(TService service, Class<TViewDTO> viewDtoClass, Class<TCreateDTO> createDtoClass) {
        this.service = service;
        this.viewDtoClass = viewDtoClass;
        this.createDtoClass = createDtoClass;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serializeToJson(List<TViewDTO> dtos) {
        if (dtos == null) {
            logger.error("Attempted to serialize null DTO list");
            throw new IllegalArgumentException("DTO list cannot be null");
        }
        logger.info("Serializing {} DTOs to JSON", dtos.size());
        return gson.toJson(dtos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TCreateDTO deserializeFromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            logger.error("Attempted to deserialize null or empty JSON string");
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        logger.debug("Deserializing JSON: {}", json);
        return gson.fromJson(json, createDtoClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TCreateDTO> deserializeListFromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            logger.error("Attempted to deserialize list from null or empty JSON string");
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        logger.info("Deserializing list from JSON");
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        List<TCreateDTO> dtos = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            String singleJson = jsonArray.get(i).toString();
            logger.debug("Deserializing element {}: {}", i, singleJson);
            TCreateDTO dto = deserializeFromJson(singleJson);
            dtos.add(dto);
        }

        logger.info("Successfully deserialized {} DTOs", dtos.size());
        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serializeToBinary(List<TViewDTO> dtos, String filePath) throws Exception {
        if (dtos == null) {
            logger.error("Attempted to serialize null DTO list to binary");
            throw new IllegalArgumentException("DTO list cannot be null");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.error("Attempted to serialize to null or empty file path");
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(dtos);
            logger.debug("Binary serialization completed for file: {}", filePath);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TCreateDTO deserializeFromBinary(String filePath) throws Exception {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (TCreateDTO) ois.readObject();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TCreateDTO> deserializeListFromBinary(String filePath) throws Exception {
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.error("Attempted to deserialize list from null or empty file path");
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        logger.info("Deserializing list from binary file: {}", filePath);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            List<TViewDTO> viewDtos = (List<TViewDTO>) ois.readObject();
            List<TCreateDTO> createDtos = new ArrayList<>();
            for (TViewDTO viewDto : viewDtos) {
                // Convert view DTO to JSON and deserialize to create DTO
                String json = gson.toJson(viewDto);
                TCreateDTO createDto = deserializeFromJson(json);
                createDtos.add(createDto);
                logger.debug("Converted and deserialized: {}", createDto);
            }
            logger.info("Successfully deserialized {} DTOs from binary", createDtos.size());
            return createDtos;
        }
    }
}