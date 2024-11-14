package com.biit.ks.persistence.repositories;

public interface IOpenSearchConfigurator {

    String getOpenSearchFileIndex();

    String getOpenSearchTextIndex();

    String getOpenSearchCategorizationsIndex();
}
