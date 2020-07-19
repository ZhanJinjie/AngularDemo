package qx.aieduserver.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import qx.aieduserver.models.Exercise
import qx.aieduserver.models.OutlineNode

@Repository
interface OutlineNodeRepository: MongoRepository<OutlineNode, String> {

}