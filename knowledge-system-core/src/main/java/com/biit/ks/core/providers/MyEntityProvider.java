package com.biit.ks.core.providers;

import com.biit.server.providers.ElementProvider;
import com.biit.ks.persistence.entities.MyEntity;
import com.biit.ks.persistence.repositories.MyEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyEntityProvider extends ElementProvider<MyEntity, Long, MyEntityRepository> {

    @Autowired
    public MyEntityProvider(MyEntityRepository repository) {
        super(repository);
    }

    public Optional<MyEntity> findByName(String name) {
        return getRepository().findByName(name);
    }
}
