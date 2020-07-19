package qx.aieduserver.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import qx.aieduserver.models.TagDefinition
import qx.aieduserver.models.TagCategory
import qx.aieduserver.repositories.TagCategoryRepository
import qx.aieduserver.repositories.TagDefinitionRepository

@Service
class TagCategoryService {
    @Autowired
    private lateinit var repo: TagCategoryRepository

    @Autowired
    private lateinit var catDefRepo: TagDefinitionRepository
    fun findAll(): MutableList<TagCategory> {
        return repo.findAll()
    }

    fun findTags(id: String): List<TagDefinition> {
        if ("0".equals(id)) {
            return catDefRepo.findByCategoryIdIsNull()
        }
        return catDefRepo.findByCategoryId(id)
    }

    fun create(name: String): String {
        val cat = TagCategory(null, name)
        repo.save(cat)
        return cat.id!!
    }

    fun update(id: String, cat: TagCategory) {
        val saved = repo.findByIdOrNull(id)
        saved!!.name = cat.name
        repo.save(saved)
    }

    @Autowired
    private lateinit var tagDefSvc: TagDefinitionService
    fun delete(id: String) {
        //先删除子项
        tagDefSvc.deleteByCategory(id)
        //再删除自己
        repo.deleteById(id)
    }
}