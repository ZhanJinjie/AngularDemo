package qx.aieduserver.models

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType

@Document
data class OutlineNode(var name: String, var description: String? = null,
                       @Field(targetType = FieldType.OBJECT_ID)
                       var parentId: String? = null,
                       var id: String? = null)