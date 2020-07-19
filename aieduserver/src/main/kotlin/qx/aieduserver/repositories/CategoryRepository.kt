package qx.aieduserver.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import qx.aieduserver.models.Category
@Repository
interface CategoryRepository : MongoRepository<Category, String> {
    fun findByParentId(parentId:String):List<Category>
    fun findByParentIsNull(): List<Category>
}