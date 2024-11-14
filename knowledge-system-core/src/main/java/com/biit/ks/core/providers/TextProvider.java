package com.biit.ks.core.providers;

import com.biit.ks.core.providers.pools.OpenSearchElementPool;
import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.entities.TextLanguages;
import com.biit.ks.persistence.repositories.TextRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TextProvider extends CategorizedElementProvider<Text, TextRepository> {


    protected TextProvider(OpenSearchElementPool<Text> openSearchElementPool, TextRepository textRepository) {
        super(openSearchElementPool, textRepository);
    }


    public List<Text> search(String searchQuery, TextLanguages language, Integer from, Integer size) {
        return getRepository().search(searchQuery, language, from, size);
    }


}
