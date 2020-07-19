package qx.aieduserver.models

import org.springframework.data.mongodb.core.mapping.DBRef

/**
 * @param refId 和tag相关的分类对象地址
 * @param type tag对象锁管理的分类对象的类型
 */
data class TagDefinition(
        var name: String,
        var description: String? = null,
        @DBRef
        var category: TagCategory? = null, var id: String? = null)