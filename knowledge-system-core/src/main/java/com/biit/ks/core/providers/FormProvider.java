package com.biit.ks.core.providers;


import com.biit.ks.persistence.entities.Form;
import com.biit.ks.persistence.repositories.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class FormProvider {

    private final FormRepository formRepository;


    @Autowired
    public FormProvider(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    private FormRepository getRepository() {
        return formRepository;
    }

    public Optional<Form> get(Long id) {
        return getRepository().findById(id);
    }

    public List<Form> getAll() {
        return getRepository().findAll();
    }

    public Optional<Form> getByName(String name, Integer version) {
        if (version == 0) {
            return getRepository().findByNameOrderByVersionDesc(name).stream().findFirst();
        } else {
            return getRepository().findByNameAndVersion(name, version);
        }
    }

    public Form save(Form reverse) {
        return null;
    }

    public Form saveFormFromJson(String value, String email) {
        final Form form = new Form();
        final String name = getNameFromJson(value);
        form.setName(name);
        final Integer version = getVersionFromJson(value);
        form.setVersion(version);
        form.setOrganizationId(getOrganizationIdFromJson(value));
        form.setCreatedAt(getCreationTimeFromJson(value));
        form.setValue(value);
        form.setDescription(getDescriptionFromJson(value));
        final Optional<Form> optionalForm = getRepository().findByNameAndVersion(name, version);
        if (optionalForm.isPresent()) {
            form.setId(optionalForm.get().getId());
            form.setUpdatedBy(email);
        } else {
            form.setCreatedBy(email);
        }
        return getRepository().save(form);
    }

    private String getNameFromJson(String json) {
        String subString = json.substring(json.indexOf("label"), json.indexOf("hidden"));
        subString = subString.replace('"', ' ').trim();
        subString = subString.substring(subString.indexOf(":") + 1, subString.indexOf(",")).trim();
        return subString;
    }

    private Integer getVersionFromJson(String json) {
        String subString = json.substring(json.indexOf("version"), json.indexOf("organizationId"));
        subString = subString.replace('"', ' ').trim();
        subString = subString.substring(subString.indexOf(":") + 1, subString.indexOf(",")).trim();
        return Integer.parseInt(subString);
    }

    private String getOrganizationIdFromJson(String json) {
        String subString = json.substring(json.indexOf("version"), json.indexOf("flows"));
        subString = subString.substring(subString.indexOf("organizationId"), subString.indexOf("description"));
        subString = subString.replace('"', ' ').trim();
        subString = subString.substring(subString.indexOf(":") + 1, subString.indexOf(",")).trim();
        return subString;
    }

    private String getDescriptionFromJson(String json) {
        String subString = json.substring(json.indexOf("organizationId"), json.indexOf("flows"));
        subString = subString.substring(subString.indexOf("description"));
        subString = subString.replace('"', ' ').trim();
        subString = subString.substring(subString.indexOf(":") + 1, subString.indexOf(",")).trim();
        return subString;
    }

    private LocalDateTime getCreationTimeFromJson(String json) {
        String subString = json.substring(json.indexOf("creationTime"), json.indexOf("updateTime"));
        subString = subString.replace('"', ' ').trim();
        subString = subString.substring(subString.indexOf(":") + 1, subString.length() - 2).trim();
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(subString, DateTimeFormatter.ofPattern("MMM dd, yyyy H:m:s a"));
            return dateTime;
        } catch (DateTimeParseException e) {
            dateTime = LocalDateTime.parse(subString, DateTimeFormatter.ofPattern("MMM d, yyyy H:m:s a"));
            return dateTime;
        }
    }
}
