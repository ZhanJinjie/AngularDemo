package qx.aieduserver.dtos

import qx.aieduserver.models.Tag

data class SimilarExerciseDto(
        var content: String?,
        var answerMethod: String? = null,
        var answer: String? = null,
        var explain: String? = null,
        var tags: MutableList<Tag>? = mutableListOf(),
        var similar: Int,
        var id: String? = null
)