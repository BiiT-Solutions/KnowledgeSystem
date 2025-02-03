package com.biit.ks.models;

import com.biit.ks.dto.TextDTO;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public interface ITextClient {

    Optional<TextDTO> get(UUID uuid);

    Optional<String> get(UUID uuid, String language);

    Optional<String> get(String textName, String language);

    Optional<String> get(String textName, Locale locale);
}
