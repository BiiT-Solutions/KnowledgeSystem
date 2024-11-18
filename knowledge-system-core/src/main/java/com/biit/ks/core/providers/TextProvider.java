package com.biit.ks.core.providers;

import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.entities.TextLanguages;
import com.biit.ks.persistence.repositories.TextRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TextProvider extends CategorizedElementProvider<Text, TextRepository> {


    protected TextProvider(TextRepository textRepository) {
        super(textRepository);
    }


    public List<Text> search(String searchQuery, TextLanguages language, Integer from, Integer size) {
        return getRepository().search(searchQuery, language, from, size);
    }


    //Name is unique on texts
    public Optional<Text> get(String name) {
        final List<Text> texts = getRepository().get(name);
        if (texts.isEmpty()) {
            return Optional.empty();
        }
        return texts.stream().findFirst();
    }


}
