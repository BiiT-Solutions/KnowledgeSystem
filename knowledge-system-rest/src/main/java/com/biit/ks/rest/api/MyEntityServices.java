package com.biit.ks.rest.api;

import com.biit.server.rest.BasicServices;
import com.biit.ks.core.controllers.MyEntityController;
import com.biit.ks.core.converters.MyEntityConverter;
import com.biit.ks.core.converters.models.MyEntityConverterRequest;
import com.biit.ks.core.providers.MyEntityProvider;
import com.biit.ks.core.models.MyEntityDTO;
import com.biit.ks.persistence.entities.MyEntity;
import com.biit.ks.persistence.repositories.MyEntityRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/entities")
public class MyEntityServices extends BasicServices<MyEntity, MyEntityDTO, MyEntityRepository,
        MyEntityProvider, MyEntityConverterRequest, MyEntityConverter, MyEntityController> {

    public MyEntityServices(MyEntityController controller) {
        super(controller);
    }
}
