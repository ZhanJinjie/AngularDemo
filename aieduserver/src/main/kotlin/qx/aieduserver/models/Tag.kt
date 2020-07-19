package qx.aieduserver.models

import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType

data class Tag(var name: String,
               @Field(targetType = FieldType.OBJECT_ID)
               var defId: String,
               @Field(targetType = FieldType.OBJECT_ID)
               var categoryId: String?)