package com.app.config;

import com.google.gson.Gson;
import com.api.common.ErrorCode;
import com.api.common.utils.Validator;
import lombok.Getter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

@Getter
public class ConfigMapper {

    private Gson mapper;

    public ConfigMapper(Gson mapper) {
        this.mapper = mapper;
    }

    public Configuration getConfig(String path) throws Exception {
        Configuration result = null;
        mapper = new Gson();
        InputStream ioStream = this.getClass()
            .getClassLoader()
            .getResourceAsStream(path);
        Reader reader = new InputStreamReader(ioStream, "UTF-8");

        result = mapper.fromJson(reader, Configuration.class);
        Validator.of(result).validate(Configuration::getEndpoint, Objects::nonNull, new ConfigurationException("The endpoint property is required and returned null. Please add this property to config.json.", ErrorCode.CONFIGURATION_ERROR))
                    .validate(Configuration::getThreadCount, Objects::nonNull, new ConfigurationException("The thread_count property is required and returned null. Please add this property to config.json.", ErrorCode.CONFIGURATION_ERROR)).get();
        return result;
    }
}
