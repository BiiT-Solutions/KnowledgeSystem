package com.biit.ks.persistence.repositories;

import java.util.List;

public interface IOpenSearchConfigurator {

    String getOpenSearchFileIndex();

    String getOpenSearchTextIndex();

    String getOpenSearchCategorizationsIndex();

    List<String> getAllOpenSearchIndexes();
}
