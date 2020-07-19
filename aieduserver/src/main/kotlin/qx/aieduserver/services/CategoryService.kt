package qx.aieduserver.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import qx.aieduserver.events.CategoryDeletedEvent
import qx.aieduserver.exceptions.ServiceException
import qx.aieduserver.models.Category
import qx.aieduserver.models.Exercise
import qx.aieduserver.repositories.CategoryRepository
import qx.aieduserver.repositories.ExerciseRepository

@Service
class CategoryService {
    @Autowired
    private lateinit var catRepo: CategoryRepository
    fun findAll(): List<Category> {

        return catRepo.findByParentIsNull();
    }

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    /**
     *
     */
    fun create(name: String, parentId: String?): String {
        var cat = Category(name)
        var saved: Category?
        if (null != parentId) {
            val parent = catRepo.findByIdOrNull(parentId)
            cat.levelKey = parent!!.levelKey + "." + parent.id
            cat.parent = parent
        } else {
            cat.levelKey = "0"
        }
        saved = catRepo.save(cat)

        if (null != saved.parent) {
            saved.parent!!.children.add(saved)
            catRepo.save(saved.parent!!)
        }
        return saved.id!!
    }

    @Autowired
    lateinit var exSvc: ExerciseService

    fun delete(id: String) {
        val cat = catRepo.findByIdOrNull(id) ?: return
        if (null != cat.parent) {
            cat.parent!!.children.remove(cat)
            catRepo.save(cat.parent!!)
        }
        for (child in cat.children) {
            delete(child.id!!)
        }
        catRepo.delete(cat)
        eventPublisher.publishEvent(CategoryDeletedEvent(cat))
    }

    fun update(id: String, name: String, parentId: String?) {
        val cat = catRepo.findByIdOrNull(id) ?: return
        cat.name = name
        val oldParent = cat.parent
        if (null == oldParent) {
            if (null != parentId) {
                val parent = catRepo.findByIdOrNull(parentId)
                parent!!.children.add(cat)
                cat.parent = parent
            }
        } else {
            if (oldParent.id != parentId) {
                oldParent.children.remove(cat)
                catRepo.save(oldParent)
                if (null != parentId) {
                    val parent = catRepo.findByIdOrNull(parentId)
                    cat.parent = parent
                    parent!!.children.add(cat)
                    catRepo.save(parent)
                } else {
                    cat.parent = null
                }
            }
            calculateLevelKeys(cat)
            catRepo.save(cat)
        }
    }

    fun calculateLevelKeys(cat: Category) {
        if (null != cat.parent) {
            cat.levelKey = cat.parent!!.levelKey + "." + cat.parent!!.id
        } else {
            cat.levelKey = "0"
        }
        catRepo.save(cat)
        for (child in cat.children) {
            calculateLevelKeys(child)
        }
    }


//    fun getExercises(id: String): List<Exercise> {
//        val cat = catRepo.findByIdOrNull(id)
//        return exSvc.findByCategoriesIdIn(listOf<String>(cat!!.id!!))
//    }

}