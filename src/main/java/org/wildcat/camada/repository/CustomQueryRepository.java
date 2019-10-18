package org.wildcat.camada.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcat.camada.entity.CustomQuery;

import java.util.List;

@Repository
public interface CustomQueryRepository extends CrudRepository<CustomQuery, Long> {
    List<CustomQuery> findAllByQueryIn(List<String> queryNames);
}
