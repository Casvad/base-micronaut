package com.carlos.models

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "example_entity")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
data class ExampleEntity (
    @Id
    @SequenceGenerator(name = "example_entity_id_seq_generator", sequenceName = "example_entity_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "example_entity_id_seq_generator")
    val id: Long = 0,

    @NotBlank
    @field:Size(max = 25)
    @field:JsonProperty("name")
    @Column(name = "name", nullable = false)
    val name: String = "",

    @field:JsonProperty("index")
    @Column(name = "index", nullable = true)
    val index: Int = 0,

    @field:JsonProperty("additional_information")
    @Type(type = "jsonb")
    @Column(name = "additional_information", nullable = true)
    val additionalInformation: Map<String, Any?> = mapOf(),


    @field:JsonProperty("created_at")
    @Column(name = "created_at", nullable = false)
    @JsonFormat
        (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
)