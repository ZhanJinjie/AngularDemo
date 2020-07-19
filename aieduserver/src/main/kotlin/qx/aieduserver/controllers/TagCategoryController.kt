package qx.aieduserver.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import qx.aieduserver.dtos.TagDefinitionDto
import qx.aieduserver.models.TagDefinition
import qx.aieduserver.models.TagCategory
import qx.aieduserver.services.TagCategoryService
import java.util.function.Consumer

@RestController
@RequestMapping("/tag-categories")
class TagCategoryController {
    @Autowired
    private lateinit var tcatSvc: TagCategoryService

    @GetMapping("{id}/tagdefs")
    fun getTagDefs(@PathVariable("id") id: String): List<TagDefinitionDto> {
        val dtos = mutableListOf<TagDefinitionDto>()
        tcatSvc.findTags(id).forEach { tag ->
            dtos.add(TagDefinitionDto(tag.id, tag.name, if (tag.category == null) null else tag.category!!.id))
        }
        return dtos
    }

    @GetMapping
    fun get(): MutableList<TagCategory> {
        return tcatSvc.findAll();
    }

    @PostMapping
    fun create(@RequestBody req: TagCategory): String {
        return tcatSvc.create(req.name);
    }

    @PutMapping
    fun update(@RequestBody cat: TagCategory) {
        tcatSvc.update(cat.id!!, cat)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: String) {
        tcatSvc.delete(id)
    }

}