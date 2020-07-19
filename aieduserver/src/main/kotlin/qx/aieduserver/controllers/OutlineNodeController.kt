package qx.aieduserver.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import qx.aieduserver.dtos.OutlineNodeDto
import qx.aieduserver.models.Exercise
import qx.aieduserver.models.OutlineNode
import qx.aieduserver.query.utils.QueryUtils
import qx.aieduserver.services.OutlineNodeService

@RestController
@RequestMapping("outline-nodes")
class OutlineNodeController {
    @Autowired
    private lateinit var olSvc: OutlineNodeService

    @PostMapping
    fun create(@RequestBody req: OutlineNodeDto): String {
        return olSvc.create(req.toOutlineNode())
    }

    @PutMapping
    fun update(@RequestBody req: OutlineNodeDto) {
        olSvc.update(req.toOutlineNode())
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: String) {
        olSvc.delete(id)
    }

    @DeleteMapping("{id}/exercises/{exId}")
    fun deleteExercise(@PathVariable("id") id: String, @PathVariable("exId") exId: String) {
        olSvc.deleteExercise(id, exId)
    }

    @GetMapping
    fun get(): List<OutlineNodeDto> {
        return buildTreeFromList(olSvc.findAll())
    }

    private fun buildSubTree(parentDto: OutlineNodeDto, nodeList: MutableList<OutlineNode>) {
        val rmList = mutableListOf<OutlineNode>()
        nodeList.forEach { node ->
            if (node.parentId == parentDto.id) {
                parentDto.children!!.add(OutlineNodeDto(node.name, node.description, parentDto.id, id = node.id))
                rmList.add(node);
            }
        }
        nodeList.removeAll(rmList)
        parentDto.children!!.forEach { child ->
            buildSubTree(child, nodeList)
        }
    }

    private fun buildTreeFromList(nodeList: MutableList<OutlineNode>): List<OutlineNodeDto> {
        val rmList = mutableListOf<OutlineNode>()
        var roots = mutableListOf<OutlineNodeDto>()
        nodeList.forEach { node ->
            if (node.parentId == null) {
                roots.add(OutlineNodeDto(node.name, node.description, id = node.id))
                rmList.add(node)
            }
        }
        nodeList.removeAll(rmList)
        roots.forEach { dto ->
            buildSubTree(dto, nodeList)
        }
        return roots;
    }

    @GetMapping("{id}/exercises/{page}/{size}/{sorts}/{filters}")
    fun listExercises(@PathVariable("id") id: String, @PathVariable("page") page: Int, @PathVariable("size") size: Int, @PathVariable("sorts") sorts: String, @PathVariable("filters") filters: String): Page<Exercise>? {

        var query = QueryUtils.buildQuery(page, size, sorts, filters)
        return olSvc.listExercises(id, query);
    }

    @PostMapping("{id}/exercises")
    fun addExercises(@PathVariable("id") id: String, @RequestBody exIds: List<String>) {
        olSvc.addExercises(id, exIds)
    }

}