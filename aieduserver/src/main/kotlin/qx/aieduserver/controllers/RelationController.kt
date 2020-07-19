package qx.aieduserver.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import qx.aieduserver.models.Relation
import qx.aieduserver.services.RelationService

@RestController
@RequestMapping("relations")
class RelationController {
    @Autowired
    private lateinit var relSvc: RelationService

    @PostMapping
    fun create(@RequestBody relation: Relation): String {
        return relSvc.create(relation);
    }

    @PutMapping
    fun update(@RequestBody relation: Relation) {
        relSvc.update(relation);
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: String) {
        relSvc.delete(id);
    }
}