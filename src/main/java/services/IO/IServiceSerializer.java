package services.IO;

import java.util.List;

/**
 * Interface for serializing and deserializing DTOs using a service.
 *
 * @param <TService>   The type of the service.
 * @param <TViewDTO>   The type of the "view" DTO.
 * @param <TCreateDTO> The type of the "create" DTO.
 */
public interface IServiceSerializer<TService, TViewDTO, TCreateDTO> {

    /**
     * Serializes a list of view DTOs to JSON.
     *
     * @param dtos The list of view DTOs to serialize.
     * @return The JSON representation of the DTOs.
     */
    String serializeToJson(List<TViewDTO> dtos);

    /**
     * Deserializes a JSON string to a "create" DTO.
     *
     * @param json The JSON string to deserialize.
     * @return The deserialized create DTO.
     */
    TCreateDTO deserializeFromJson(String json);


    /**
     * Deserializes a list of JSON strings to a "create" DTOs.
     *
     * @param json The JSON string to deserialize.
     * @return The list of deserialized create DTO.
     */
    List<TCreateDTO> deserializeListFromJson(String json);


    /**
     * Serializes a list of view DTOs to a binary file.
     *
     * @param  dtos     The list of view DTOs to serialize.
     * @param  filePath The path to the binary file.
     * @throws Exception If an error occurs during serialization.
     */
    void serializeToBinary(List<TViewDTO> dtos, String filePath) throws Exception;

    /**
     * Deserializes a binary file to a "create" DTO.
     *
     * @param  filePath The path to the binary file.
     * @return The deserialized create DTO.
     * @throws Exception If an error occurs during deserialization.
     */
    TCreateDTO deserializeFromBinary(String filePath) throws Exception;

    /**
     * Deserializes a binary file to a list of "create" DTOs.
     *
     * @param  filePath The path to the binary file.
     * @return The deserialized list of create DTOs.
     * @throws Exception If an error occurs during deserialization.
     */
    List<TCreateDTO> deserializeListFromBinary(String filePath) throws Exception;
}