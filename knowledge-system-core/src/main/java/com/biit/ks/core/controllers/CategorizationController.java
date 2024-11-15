package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.CategorizationConverter;
import com.biit.ks.core.converters.models.CategorizationConverterRequest;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.models.CategorizationDTO;
import com.biit.ks.core.providers.CategorizationProvider;
import com.biit.ks.core.providers.CategorizedElementProvider;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.ks.persistence.repositories.CategorizationRepository;
import com.biit.ks.persistence.repositories.CategorizedElementRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class CategorizationController extends OpenSearchElementController<Categorization, CategorizationDTO, CategorizationRepository,
        CategorizationProvider, CategorizationConverterRequest, CategorizationConverter> {
    private static final int SIZE = 10;

    private final CategorizationProvider categorizationProvider;

    private final List<CategorizedElementProvider<? extends CategorizedElement<?>, ? extends CategorizedElementRepository<?>>> providersWithCategories;

    public CategorizationController(CategorizationProvider categorizationProvider, CategorizationConverter categorizationConverter,
                                    List<CategorizedElementProvider<? extends CategorizedElement<?>, ? extends CategorizedElementRepository<?>>>
                                            providersWithCategories) {
        super(categorizationProvider, categorizationConverter);
        this.categorizationProvider = categorizationProvider;
        this.providersWithCategories = providersWithCategories;
    }


    @Override
    protected CategorizationConverterRequest createConverterRequest(Categorization categorization) {
        return new CategorizationConverterRequest(categorization);
    }


    public CategorizationDTO create(String categorization, String creatorName) {
        return create(new CategorizationDTO(categorization), creatorName);
    }


    @Override
    public CategorizationDTO create(CategorizationDTO dto, String creatorName) {
        validate(dto);
        return convert(getProvider().create(reverse(dto), creatorName));
    }


    @Override
    public Collection<CategorizationDTO> create(Collection<CategorizationDTO> categorizationDTOS, String creatorName) {
        final List<CategorizationDTO> results = new ArrayList<>();
        categorizationDTOS.forEach(fileEntryDTO -> create(fileEntryDTO, creatorName));
        return results;
    }

    public CategorizationDTO get(String categorization) {
        final Categorization fileEntry =
                getProvider().get(categorization).orElseThrow(() -> new FileNotFoundException(this.getClass(),
                        "No category with name '" + categorization + "'."));

        return convert(fileEntry);
    }


    @Scheduled(cron = "@midnight")
    public void deleteOrphanCategories() {
        int loop = 0;
        List<Categorization> categorizations = categorizationProvider.getAll(0, SIZE);
        while (!categorizations.isEmpty()) {
            for (Categorization categorization : categorizations) {
                //Check if a category is not used.
                boolean used = false;
                for (CategorizedElementProvider<? extends CategorizedElement<?>, ? extends CategorizedElementRepository<?>> provider
                        : providersWithCategories) {
                    if (!provider.searchByCategory(categorization, 0, 1).isEmpty()) {
                        used = true;
                        break;
                    }
                }
                if (used) {
                    continue;
                }
                //Remove it.
                categorizationProvider.delete(categorization);
            }
            loop++;
            categorizations = categorizationProvider.getAll(loop * SIZE, SIZE);
        }
    }
}
