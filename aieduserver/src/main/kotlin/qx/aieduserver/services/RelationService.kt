package qx.aieduserver.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

import org.springframework.stereotype.Service
import qx.aieduserver.events.ExerciseDeletedEvent
import qx.aieduserver.models.Relation
import qx.aieduserver.repositories.RelationRepository

@Service
class RelationService {
    @Autowired
    private lateinit var relRepo: RelationRepository
    fun create(relation: Relation): String {
        val matcher = ExampleMatcher.matching().withIgnorePaths("intAttr")
        val example = Example.of(relation, matcher)
        val one = relRepo.findOne(example)
        if (one.isPresent) {
            relation.id = one.get().id
            return one.get().id!!
        }
        relRepo.insert((relation))
        return relation.id!!
    }

    @EventListener
    fun onExerciseDeleted(event: ExerciseDeletedEvent) {
        relRepo.deleteByStartTypeAndStartId("exercise", event.exerciseId);
        relRepo.deleteByEndTypeAndEndId("exercise", event.exerciseId);
    }

    fun delete(id: String) {
        relRepo.deleteById(id);
    }

    fun update(relation: Relation) {
        relRepo.save((relation))
    }

    fun findByRelationAndStartTypeAndStartId(relation: String, startType: String, startId: String, pageRequest: PageRequest): Page<Relation> {
        return relRepo.findByRelationAndStartTypeAndStartId(relation, startType, startId, pageRequest)
    }

    fun deleteByRelationAndStartIdAndStartTypeAndEndIdAndEndType(relation: String, startId: String, startType: String, endId: String, endType: String) {
        relRepo.deleteByRelationAndStartIdAndStartTypeAndEndIdAndEndType(relation, startId, startType, endId, endType)
    }
}