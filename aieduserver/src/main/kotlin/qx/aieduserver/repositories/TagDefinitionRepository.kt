package qx.aieduserver.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import qx.aieduserver.models.TagDefinition

@Repository
interface TagDefinitionRepository : MongoRepository<TagDefinition, String> {
    fun findByCategoryId(catId: String): List<TagDefinition>
    fun findByCategoryIdIsNull(): List<TagDefinition>
    fun deleteByCategory(categoryId: String)
}