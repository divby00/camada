package org.wildcat.camada.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcat.camada.persistence.entity.CustomQuery;

import java.util.List;

@Repository
public interface CustomQueryRepository extends CrudRepository<CustomQuery, Long> {
    List<CustomQuery> findAllByQueryInOrderByZorder(List<String> queryNames);
}
