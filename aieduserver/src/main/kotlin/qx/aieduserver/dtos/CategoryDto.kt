package qx.aieduserver.dtos

 data class CategoryDto(val id: String?, val name: String, val children: List<CategoryDto>)