package com.biit.ks.core.controllers.kafka;

import com.biit.kafka.consumers.EventListener;
import com.biit.kafka.events.Event;
import com.biit.kafka.events.EventCustomProperties;
import com.biit.kafka.logger.EventsLogger;
import com.biit.ks.core.controllers.kafka.converter.PdfFormPayload;
import com.biit.ks.core.exceptions.CategoryAlreadyExistsException;
import com.biit.ks.core.providers.CategorizationProvider;
import com.biit.ks.core.providers.FileEntryProvider;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.entities.FileEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;


@Controller
@ConditionalOnExpression("${spring.kafka.enabled:false}")
public class EventController {

    private static final String[] DOCUMENT_CATEGORIES = {"Events", "Document", "Reports"};

    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final FileEntryProvider fileEntryProvider;
    private final CategorizationProvider categorizationProvider;
    private List<Categorization> categorizations;

    public EventController(@Autowired(required = false) EventListener eventListener, FileEntryProvider fileEntryProvider,
                           CategorizationProvider categorizationProvider) {
        this.fileEntryProvider = fileEntryProvider;
        this.categorizationProvider = categorizationProvider;
        //Listen to a topic
        if (eventListener != null) {
            eventListener.addListener((event, offset, groupId, key, partition, topic, timeStamp) ->
                    eventHandler(event, groupId, key, partition, topic, timeStamp));
        }
    }

    @PostConstruct
    private void initCategories() {
        for (String category : DOCUMENT_CATEGORIES) {
            try {
                categorizationProvider.create(category, null);
            } catch (CategoryAlreadyExistsException ignore) {
                //Already exists.
            }
        }
        categorizations = categorizationProvider.get(Arrays.asList(DOCUMENT_CATEGORIES));
    }


    public void eventHandler(Event event, String groupId, String key, int partition, String topic, long timeStamp) {
        EventsLogger.debug(this.getClass(), "Received event '{}' on topic '{}', group '{}', key '{}', partition '{}' at '{}'",
                event, topic, groupId, key, partition, LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),
                        TimeZone.getDefault().toZoneId()));

        final String createdBy = event.getCustomProperties().get(EventCustomProperties.ISSUER.getTag()) != null
                ? event.getCustomProperties().get(EventCustomProperties.ISSUER.getTag())
                : event.getCreatedBy();

        try {
            final PdfFormPayload pdfFormPayload = event.getEntity(PdfFormPayload.class);
            final FileEntry fileEntry = fileEntryProvider.save(new CustomMultipartFile(pdfFormPayload.getPdfContent(),
                            generateFileName(pdfFormPayload, createdBy, event.getCustomProperty(EventCustomProperties.FACT_TYPE)), PDF_CONTENT_TYPE),
                    null, false, createdBy);

            //Set categories.
            fileEntry.setCategorizations(categorizations);
            fileEntryProvider.save(fileEntry);

            EventsLogger.info(this.getClass(), "Document '{}' with version '{}' type '{}' from '{}' stored. Categorized as '{}'!",
                    pdfFormPayload.getFormName(), pdfFormPayload.getFormVersion(), event.getCustomProperty(EventCustomProperties.FACT_TYPE),
                    createdBy, categorizations);
        } catch (Exception e) {
            EventsLogger.severe(this.getClass(), "Invalid event received!!\n" + event);
            EventsLogger.errorMessage(this.getClass(), e);
        }
    }

    private String generateFileName(PdfFormPayload pdfFormPayload, String createdBy, String eventType) {
        return pdfFormPayload.getFormName() + "_v" + pdfFormPayload.getFormVersion() + "-" + eventType + "-"
                + (pdfFormPayload.getCreatedBy() != null ? pdfFormPayload.getCreatedBy() : createdBy)
                + "-" + UUID.randomUUID() + ".pdf";
    }


}
