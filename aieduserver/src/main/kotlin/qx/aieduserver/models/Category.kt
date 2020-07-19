package qx.aieduserver.models

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document


/**
 * @author Parker
 * 知识点树状分类索引
 * @param name 分类名称
 * @param parent 上级分类
 * @param levelKey 包含所有上级分类id的索引
 * @param id id
 */
@Document
data class Category(var name: String,
                    @DBRef(lazy = true)
                    var parent: Category? = null,
                    @DBRef(lazy = true)
                    var children: MutableList<Category> = mutableListOf(),
                    var levelKey: String? = "0", var id: String? = null) {

}