package services.data.mapping;

import org.modelmapper.ModelMapper;

public class ModelMapperConfig {
    private static final ModelMapper modelMapper = new ModelMapper();

    private ModelMapperConfig() {} // Private constructor to prevent instantiation

    public static ModelMapper getModelMapper() {
        return modelMapper;
    }
}
