package qx.aieduserver.dtos

import qx.aieduserver.models.OutlineNode

data class OutlineNodeDto(val name: String, var description: String? = null, var parentId: String? = null, var children: MutableList<OutlineNodeDto>? = mutableListOf(), var id: String? = null) {
    fun toOutlineNode(): OutlineNode {
        return OutlineNode(name, description, parentId, id)
    }
}