package qx.aieduserver.services

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import qx.aieduserver.models.Exercise
import qx.aieduserver.models.OutlineNode
import qx.aieduserver.query.utils.Query
import qx.aieduserver.repositories.OutlineNodeRepository
import qx.aieduserver.models.Relation
import qx.aieduserver.repositories.ExerciseRepository
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.aggregation.ConvertOperators
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo


@Service
class OutlineNodeService {
    @Autowired
    private lateinit var repo: OutlineNodeRepository
    fun findAll(): MutableList<OutlineNode> {
        return repo.findAll()
    }

    fun create(data: OutlineNode): String {
        return repo.insert(data).id!!
    }

    fun update(data: OutlineNode) {
        repo.save(data)
    }

    fun delete(id: String) {
        repo.deleteById(id)
    }

    @Autowired
    private lateinit var rlSvc: RelationService

    fun addExercises(id: String, exIds: List<String>) {
        exIds.forEach { exId ->
            rlSvc.create(Relation("contain", id, "outlineNode", exId, "exercise"))
        }
    }

    @Autowired
    private lateinit var exRepo: ExerciseRepository
    fun listExercises(id: String, query: Query): Page<Exercise>? {
        val stages = mutableListOf<AggregationOperation>(
                match(Criteria().andOperator(Relation::relation isEqualTo "contain",
                        Relation::startType isEqualTo "outlineNode",
                        Relation::startId isEqualTo ObjectId(id),
                        Relation::endType isEqualTo "exercise"
                )),
                lookup("exercise", "endId", "_id", "exs"),
                unwind("exs"),
                replaceRoot("exs"),
                lookup("tagDefinition", "tags.defId", "_id", "tagDef")
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
                }
            }
            stages.add(match(c))
        }
        val countStages = mutableListOf<AggregationOperation>()
        countStages.addAll(stages)
        countStages.add(count().`as`("count"))
        val aggCount = newAggregation(Exercise::class.java,
                countStages
        )
        stages.add(skip(query.pageRequest.offset))
        stages.add(limit(query.pageRequest.pageSize.toLong()))
        val agg = newAggregation(Exercise::class.java,
                stages
        )
        val total = mongoTemplate.aggregate(aggCount, "relation", Map::class.java)
        val result = mongoTemplate.aggregate(agg, "relation", Exercise::class.java)
        val exercises = result.mappedResults
        val page: PageImpl<Exercise>
        page = PageImpl<Exercise>(exercises, query.pageRequest, if (total.mappedResults.size == 0) 0L else total.mappedResults[0]["count"].toString().toLong())
        return page
    }

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    fun deleteExercise(id: String, exId: String) {
        rlSvc.deleteByRelationAndStartIdAndStartTypeAndEndIdAndEndType("contain", id, "outlineNode", exId, "exercise")
    }

}