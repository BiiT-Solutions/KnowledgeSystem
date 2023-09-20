package com.biit.ks.core.controllers;


import com.biit.ks.core.converters.MyEntityConverter;
import com.biit.ks.core.converters.models.MyEntityConverterRequest;
import com.biit.ks.core.exceptions.MyEntityNotFoundException;
import com.biit.ks.core.providers.MyEntityProvider;
import com.biit.ks.core.models.MyEntityDTO;
import com.biit.ks.persistence.entities.MyEntity;
import com.biit.ks.persistence.repositories.MyEntityRepository;
import com.biit.server.controller.BasicElementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MyEntityController extends BasicElementController<MyEntity, MyEntityDTO, MyEntityRepository,
        MyEntityProvider, MyEntityConverterRequest, MyEntityConverter> {

    @Autowired
    protected MyEntityController(MyEntityProvider provider, MyEntityConverter converter) {
        super(provider, converter);
    }

    @Override
    protected MyEntityConverterRequest createConverterRequest(MyEntity myEntity) {
        return new MyEntityConverterRequest(myEntity);
    }

    public MyEntityDTO getByName(String name) {
        return getConverter().convert(new MyEntityConverterRequest(getProvider().findByName(name).orElseThrow(() ->
                new MyEntityNotFoundException(this.getClass(),
                        "No MyEntity with name '" + name + "' found on the system."))));
    }
}
