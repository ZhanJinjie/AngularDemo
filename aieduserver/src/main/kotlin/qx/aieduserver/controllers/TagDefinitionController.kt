package qx.aieduserver.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import qx.aieduserver.dtos.TagDefinitionDto
import qx.aieduserver.models.TagDefinition
import qx.aieduserver.query.utils.QueryUtils
import qx.aieduserver.services.TagDefinitionService

@RestController
@RequestMapping("/tagdefs")
class TagDefinitionController {
    @Autowired
    private lateinit var tagSvc: TagDefinitionService

    @PostMapping
    fun create(@RequestBody tag: TagDefRequest): String {
        return tagSvc.create(tag.name, tag.description, tag.categoryId)
    }

    @PutMapping
    fun update(@RequestBody tag: TagDefRequest) {
        tagSvc.update(tag.id!!, tag.name, tag.description, tag.categoryId)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: String) {
        tagSvc.delete(id)
    }

    @GetMapping
    fun get(): List<TagDefinitionDto> {
        val list: MutableList<TagDefinition> = tagSvc.findAll()
        val dtos = mutableListOf<TagDefinitionDto>()
        list.forEach { def ->
            dtos.add(TagDefinitionDto(
                    def.id,
                    def.name,
                    if (def.category != null) def.category!!.id else null,
                    if (def.category != null) def.category!!.name else null
            ))
        }
        return dtos
    }

    data class TagDefRequest(val name: String, val description: String?, val categoryId: String?, val id: String?)
}