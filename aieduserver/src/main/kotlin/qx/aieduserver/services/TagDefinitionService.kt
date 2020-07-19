package qx.aieduserver.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import qx.aieduserver.events.TagDefinitionsDeletedEvent
import qx.aieduserver.models.TagDefinition
import qx.aieduserver.repositories.TagCategoryRepository
import qx.aieduserver.repositories.TagDefinitionRepository

@Service
class TagDefinitionService {
    @Autowired
    private lateinit var defRepo: TagDefinitionRepository

    @Autowired
    private lateinit var tagCatRepo: TagCategoryRepository
    fun create(name: String, description: String?, categoryId: String?): String {
        var tag = TagDefinition(name, description);
        if (null != categoryId && !categoryId.equals("0")) {
            val cat = tagCatRepo.findById(categoryId).get()
            tag.category = cat
        }
        defRepo.save(tag)
        return tag.id!!
    }

    fun update(id: String, name: String, description: String?, categoryId: String?) {
        var tag = defRepo.findById(id).get()
        if (!name.equals(tag.name)) {
            tag.name = name
        }

        if (description != tag.description) {
            tag.description = description
        }
        val cat = tag.category;

        var oldCatId = ""
        if (null != cat) {
            oldCatId = cat.id!!
        }
        if (oldCatId != categoryId) {
            if (null == categoryId || categoryId == "0") {
                tag.category = null
            } else {
                val newCat = tagCatRepo.findById(categoryId).get()
                tag.category = newCat
            }
        }
        defRepo.save(tag)
    }

    fun delete(id: String) {
        defRepo.deleteById(id)
        applicationEventPublisher.publishEvent(TagDefinitionsDeletedEvent(listOf(id)))
    }

    fun findAll(): MutableList<TagDefinition> {
        return this.defRepo.findAll()
    }

    @Autowired
    private lateinit var applicationEventPublisher: ApplicationEventPublisher
    fun deleteByCategory(categoryId: String) {
        val defs = defRepo.findByCategoryId(categoryId)
        defRepo.deleteByCategory(categoryId)
        val ids = mutableListOf<String>()
        defs.forEach { ids.add(it.id!!) }
        applicationEventPublisher.publishEvent(TagDefinitionsDeletedEvent(ids))
    }
}