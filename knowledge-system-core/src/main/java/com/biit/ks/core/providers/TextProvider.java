package com.biit.ks.core.providers;

import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.entities.TextLanguages;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.repositories.TextRepository;
import org.springframework.stereotype.Service;

@Service
public class TextProvider extends CategorizedElementProvider<Text, TextRepository> {


    protected TextProvider(TextRepository textRepository) {
        super(textRepository);
    }


    public SearchWrapper<Text> search(String searchQuery, TextLanguages language, Integer from, Integer size) {
        return getRepository().search(searchQuery, language, from, size);
    }


    //Name is unique on texts
    public SearchWrapper<Text> get(String name) {
        final SearchWrapper<Text> texts = getRepository().get(name);
        if (texts.isEmpty()) {
            return new SearchWrapper<>();
        }
        return texts;
    }

}
