package qx.aieduserver.repositories

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import qx.aieduserver.models.Relation

@Repository
interface RelationRepository : MongoRepository<Relation, String> {
    fun findByRelationAndStartTypeAndStartId(relation: String, startType: String, startId: String, pageRequest: Pageable?): Page<Relation>
    fun deleteByRelationAndStartIdAndStartTypeAndEndIdAndEndType(relation: String, startId: String, startType: String, endId: String, endType: String)
    fun deleteByStartTypeAndStartId(startType: String, startId: String)
    fun deleteByEndTypeAndEndId(endType: String, endId: String)
}