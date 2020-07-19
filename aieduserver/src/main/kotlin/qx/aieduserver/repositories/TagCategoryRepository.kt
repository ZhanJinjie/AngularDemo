package qx.aieduserver.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import qx.aieduserver.models.Category
import qx.aieduserver.models.TagCategory

@Repository
interface TagCategoryRepository : MongoRepository<TagCategory, String>