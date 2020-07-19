package qx.aieduserver.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import qx.aieduserver.dtos.CategoryDto
import qx.aieduserver.models.Category
import qx.aieduserver.models.Exercise
import qx.aieduserver.services.CategoryService

@RestController
@RequestMapping("/categories")
class CategoryController {
    @Autowired
    private lateinit var catSvc: CategoryService;

    @GetMapping
    fun get(): List<CategoryDto> {
        val categories: List<Category> = catSvc.findAll();
        return buildTree(categories)
    }

    fun buildTree(cats: List<Category>?): List<CategoryDto> {
        val dtos = mutableListOf<CategoryDto>()
        if (null == cats) {
            return dtos;
        }
        for (cat in cats) {
            dtos.add(CategoryDto(cat.id, cat.name, buildTree(cat.children)))
        }
        return dtos
    }

    data class CreateCategoryRequest(val name: String, val parentId: String?)


    @PostMapping
    fun create(@RequestBody category: CreateCategoryRequest): String {
        return catSvc.create(category.name, category.parentId)
    }

    data class UpdateCategoryRequest(val name: String, val parentId: String?)

    @PutMapping
    fun update(@RequestBody request: UpdateCategoryRequest) {
        catSvc.update(request.parentId!!, request.name, request.parentId)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: String) {
        catSvc.delete(id)
    }
}