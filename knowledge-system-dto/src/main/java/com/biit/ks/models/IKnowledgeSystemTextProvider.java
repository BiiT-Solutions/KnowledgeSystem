package com.biit.ks.models;

import com.biit.ks.dto.exceptions.TextNotFoundException;

import java.util.Locale;

public interface IKnowledgeSystemTextProvider {

    String get(String textName, Locale language) throws TextNotFoundException;
}
