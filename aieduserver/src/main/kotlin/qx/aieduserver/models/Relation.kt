package qx.aieduserver.models

import org.springframework.data.mongodb.core.mapping.*

/**
 * 表示2个不同对象的关系
 * @param relation 关系名称
 * @param startId 起点id
 * @param startType 起点的类型
 * @param endId 终点id
 * @param endType 终点类型
 * @param intAttr 关系整数属性
 * @param id 关系对象的id
 */
@Document
data class Relation(var relation: String,
                    @Field(targetType = FieldType.OBJECT_ID)
                    var startId: String,
                    var startType: String,
                    @Field(targetType = FieldType.OBJECT_ID)
                    var endId: String,
                    var endType: String,
                    var intAttr: Int? = 0,
                    var id: String? = null)