package qx.aieduserver.dtos

import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType

data class TagDefinitionDto(var id: String?, var name: String,
                            var categoryId: String? = null,
                            var categoryName: String? = null)