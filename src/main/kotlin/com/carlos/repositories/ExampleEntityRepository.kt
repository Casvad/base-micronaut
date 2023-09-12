package com.carlos.repositories

import com.carlos.models.ExampleModel
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface ExampleEntityRepository : CrudRepository<ExampleModel, Long> {
}