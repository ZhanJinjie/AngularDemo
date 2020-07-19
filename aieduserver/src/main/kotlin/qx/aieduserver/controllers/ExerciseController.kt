package qx.aieduserver.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import qx.aieduserver.dtos.UpdateSimilarRequest
import qx.aieduserver.models.Exercise
import qx.aieduserver.models.ExerciseSimilar
import qx.aieduserver.models.Tag
import qx.aieduserver.query.utils.QueryUtils
import qx.aieduserver.services.ExerciseService

@RestController
@RequestMapping("/exercises")
class ExerciseController {

    @Autowired
    lateinit var exSvc: ExerciseService

    @GetMapping("{page}/{size}/{sorts}/{filters}")
    fun get(@PathVariable("page") page: Int, @PathVariable("size") size: Int, @PathVariable("sorts") sorts: String, @PathVariable("filters") filters: String): Page<Exercise>? {
        var query = QueryUtils.buildQuery(page, size, sorts, filters)
        return exSvc.list(query)
    }

    data class CreateExerciseRequest(var content: String?,
                                     var answerMethod: String? = null,
                                     var answer: String? = null,
                                     var explain: String? = null,
                                     var dificulty: Float? = 0f,
                                     var tags: MutableList<Tag>? = mutableListOf(),
                                     var outlineNodeId: String?) {
        fun toExercise() = Exercise(content, answerMethod, answer, explain, dificulty, tags)
    }

    @PostMapping
    fun create(@RequestBody request: CreateExerciseRequest): String {
        return exSvc.create(request.toExercise(), request.outlineNodeId)
    }

    @PutMapping
    fun update(@RequestBody exercise: Exercise) {
        exSvc.update(exercise)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: String) {
        exSvc.delete(id)
    }

    @PutMapping("{id}/similar")
    fun saveSimilar(@PathVariable("id") id: String, @RequestBody req: UpdateSimilarRequest) {
        exSvc.updateSimilar(id, req.targetId, req.similar)
    }

    @GetMapping("{id}/similar/{page}/{size}/{sorts}/{filters}")
    fun listSimilar(@PathVariable("id") id: String, @PathVariable("page") page: Int,
                    @PathVariable("size") size: Int,
                    @PathVariable("sorts") sorts: String,
                    @PathVariable("filters") filters: String): Page<ExerciseSimilar> {
        val query = QueryUtils.buildQuery(page, size, sorts, filters)
        return exSvc.listSimilar(id, query)
    }

}