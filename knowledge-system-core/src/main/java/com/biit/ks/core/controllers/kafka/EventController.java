package com.biit.ks.core.controllers.kafka;

/*-
 * #%L
 * Knowledge System (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.kafka.consumers.EventListener;
import com.biit.kafka.events.Event;
import com.biit.kafka.events.EventCustomProperties;
import com.biit.kafka.logger.EventsLogger;
import com.biit.ks.core.controllers.kafka.converter.PdfFormPayload;
import com.biit.ks.core.exceptions.CategoryAlreadyExistsException;
import com.biit.ks.core.providers.CategorizationProvider;
import com.biit.ks.core.providers.FileEntryProvider;
import com.biit.ks.core.providers.ThumbnailProvider;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.entities.FileEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final ThumbnailProvider thumbnailProvider;

    private List<Categorization> categorizations;

    public EventController(@Autowired(required = false) EventListener eventListener, FileEntryProvider fileEntryProvider,
                           CategorizationProvider categorizationProvider, ThumbnailProvider thumbnailProvider) {
        this.fileEntryProvider = fileEntryProvider;
        this.categorizationProvider = categorizationProvider;
        this.thumbnailProvider = thumbnailProvider;
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
        categorizations = new ArrayList<>(categorizationProvider.get(Arrays.asList(DOCUMENT_CATEGORIES)).getData());
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
            FileEntry fileEntry = fileEntryProvider.save(new CustomMultipartFile(pdfFormPayload.getPdfContent(),
                            generateFileName(pdfFormPayload, createdBy, event.getCustomProperty(EventCustomProperties.FACT_TYPE)), PDF_CONTENT_TYPE),
                    null, false, createdBy);

            //Set categories.
            fileEntry.setCategorizations(categorizations);
            fileEntry = fileEntryProvider.save(fileEntry);

            EventsLogger.info(this.getClass(), "Document '{}' with version '{}' type '{}' from '{}' stored. Categorized as '{}'!",
                    pdfFormPayload.getFormName(), pdfFormPayload.getFormVersion(), event.getCustomProperty(EventCustomProperties.FACT_TYPE),
                    createdBy, categorizations);

            //Create thumbnail.
            try {
                thumbnailProvider.setThumbnail(fileEntry);
                EventsLogger.info(this.getClass(), "Thumbnail for '{}' with version '{}' from '{}' created.",
                        pdfFormPayload.getFormName(), pdfFormPayload.getFormVersion(), createdBy);
            } catch (IOException e) {
                KnowledgeSystemLogger.errorMessage(this.getClass(), e);
                fileEntry.setThumbnailUrl(null);
            }


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
