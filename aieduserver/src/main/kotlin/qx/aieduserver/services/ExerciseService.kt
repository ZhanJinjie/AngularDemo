package qx.aieduserver.services

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.*
import org.springframework.stereotype.Service
import qx.aieduserver.BusinessException
import qx.aieduserver.models.Exercise
import qx.aieduserver.models.ExerciseSimilar
import qx.aieduserver.models.Relation
import qx.aieduserver.query.utils.Query
import qx.aieduserver.repositories.ExerciseRepository
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Eq.valueOf
import org.springframework.data.mongodb.core.aggregation.BooleanOperators.And.and
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Cond.`when`
import org.springframework.data.mongodb.core.query.*
import org.springframework.data.mongodb.core.query.Update.*
import org.springframework.data.mongodb.core.query.Query.*
import org.springframework.data.mongodb.core.update
import qx.aieduserver.events.ExerciseDeletedEvent
import qx.aieduserver.events.TagDefinitionsDeletedEvent

@Service
class ExerciseService {
    @Autowired
    private lateinit var outlineNodeService: OutlineNodeService

    @Autowired
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var exRepo: ExerciseRepository
    fun create(exercise: Exercise, outlineNodeId: String?): String {
        val saved = exRepo.save(exercise)
        if (outlineNodeId != null) {
            outlineNodeService.addExercises(outlineNodeId, arrayListOf(saved.id!!))
        }
        return saved.id!!
    }

    fun update(exercise: Exercise) {
        exRepo.save(exercise)
    }

    fun delete(id: String) {
        exRepo.deleteById(id)
        applicationEventPublisher.publishEvent(ExerciseDeletedEvent(id))
    }

    @EventListener
    private fun onTagDefsDeleted(event: TagDefinitionsDeletedEvent) {
        val defIds = mutableListOf<ObjectId>()
        event.tagDefIds.forEach {
            defIds.add(ObjectId(it))
        }

        mongoTemplate.update<Exercise>()
                .matching(Criteria("tags.defId").`in`(defIds))
                .apply(Update().pull("tags", query(Criteria("defId").`in`(defIds))))
                .upsert()
    }

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate;

    fun list(query: Query): Page<Exercise> {
        val stages = mutableListOf<AggregationOperation>(
                Aggregation.lookup("tagDefinition", "tags.defId", "_id", "tagDef")
        )

        if (!query.filters.isEmpty()) {
            val c = Criteria()
            query.filters.forEach { filter ->
                when (filter.field) {
                    "tags" -> c.and("tagDef._id").all(filter.args.map {
                        ObjectId(it)
                    })
                    "content" -> c.and("content").regex(filter.args[0])
                    "answerMethod" -> c.and("answerMethod").isEqualTo(filter.args[0])
                    "id" ->
                        try {
                            c.and("_id").isEqualTo(ObjectId(filter.args[0]))
                        } catch (ex: Exception) {
                            throw BusinessException("ERROR_INVALID_OBJECT_ID", "编号格式不正确")
                        }

                }
            }
            stages.add(Aggregation.match(c))
        }
        val countStages = mutableListOf<AggregationOperation>()
        countStages.addAll(stages)
        countStages.add(Aggregation.count().`as`("count"))
        val aggCount = Aggregation.newAggregation(Exercise::class.java,
                countStages
        )
        stages.add(Aggregation.skip(query.pageRequest.offset))
        stages.add(Aggregation.limit(query.pageRequest.pageSize.toLong()))
        val agg = Aggregation.newAggregation(Exercise::class.java,
                stages
        )
        val total = mongoTemplate.aggregate(aggCount, "exercise", Map::class.java)
        val result = mongoTemplate.aggregate(agg, "exercise", Exercise::class.java)
        val exercises = result.mappedResults
        val page: PageImpl<Exercise>
        page = PageImpl<Exercise>(exercises, query.pageRequest, if (total.mappedResults.size == 0) 0L else total.mappedResults[0]["count"].toString().toLong())
        return page
    }

    @Autowired
    private lateinit var tmpl: MongoTemplate
    fun findAll(query: Query): Page<Exercise>? {
        return exRepo.findAll(query.pageRequest)
    }

    @Autowired
    private lateinit var rlSvc: RelationService;
    fun updateSimilar(id: String, targetId: String, similar: Int) {
        if (0 != similar) {
            val rela = Relation("exercise-similar", id, "exercise", targetId, "exercise")
            rlSvc.create(rela)
            rela.intAttr = similar
            rlSvc.update(rela)
            val relb = Relation("exercise-similar", targetId, "exercise", id, "exercise")
            rlSvc.create(relb)
            relb.intAttr = similar
            rlSvc.update(relb)
        } else {
            rlSvc.deleteByRelationAndStartIdAndStartTypeAndEndIdAndEndType("exercise-similar", id, "exercise", targetId, "exercise")
            rlSvc.deleteByRelationAndStartIdAndStartTypeAndEndIdAndEndType("exercise-similar", targetId, "exercise", id, "exercise")
        }

    }


    fun listSimilar(id: String, query: Query): Page<ExerciseSimilar> {
        val stages = mutableListOf<AggregationOperation>(
                lookup("relation", "_id", "endId", "relation"),
                project("id", "content", "answerMethod", "answer", "explain", "tags").and(filter("relation")
                        .`as`("item").by(
                                and(
                                        valueOf("item.relation").equalToValue("exercise-similar"),
                                        valueOf("item.startId").equalToValue(ObjectId(id)
                                        )
                                ))).`as`("relation"),
                unwind("relation", true),
                match(Criteria().and("_id").ne(ObjectId(id))),
                addFields().addField("similar")
                        .withValue(`when`(valueOf("relation.relation").equalToValue("exercise-similar"))
                                .then(Fields.field("relation.intAttr"))
                                .otherwise(0)).build()
        )

        if (query.filters.isNotEmpty()) {
            val c = Criteria()
            query.filters.forEach { filter ->
                when (filter.field) {
                    "tags" -> c.and("tags.defId").all(filter.args.map {
                        ObjectId(it)
                    })
                    "content" -> c.and("content").regex(filter.args[0])
                    "answerMethod" -> c.and("answerMethod").isEqualTo(filter.args[0])
                    "id" ->
                        try {
                            c.and("_id").isEqualTo(ObjectId(filter.args[0]))
                        } catch (ex: Exception) {
                            throw BusinessException("ERROR_INVALID_OBJECT_ID", "编号格式不正确")
                        }

                }
            }
            stages.add(Aggregation.match(c))
        }
        val countStages = mutableListOf<AggregationOperation>()
        countStages.addAll(stages)
        countStages.add(Aggregation.count().`as`("count"))
        val aggCount = Aggregation.newAggregation(Exercise::class.java,
                countStages
        )
        stages.add(sort(Sort.by("similar").descending()))
        stages.add(Aggregation.skip(query.pageRequest.offset))
        stages.add(Aggregation.limit(query.pageRequest.pageSize.toLong()))
        val agg = Aggregation.newAggregation(Exercise::class.java,
                stages
        )
        val total = mongoTemplate.aggregate(aggCount, "exercise", Map::class.java)
        val result = mongoTemplate.aggregate(agg, "exercise", Exercise::class.java)
        val exercises = result.mappedResults
        val tlist = mutableListOf<ExerciseSimilar>()

        for ((index, value) in exercises.withIndex()) {
            val raw = (result.rawResults["results"] as ArrayList<*>)[index] as org.bson.Document
            tlist.add(ExerciseSimilar(value, raw["similar"] as Int))
        }
        val page = PageImpl<ExerciseSimilar>(tlist, query.pageRequest, if (total.mappedResults.size == 0) 0L else total.mappedResults[0]["count"].toString().toLong())
        return page
    }
}