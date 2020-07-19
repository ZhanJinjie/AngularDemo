package qx.aieduserver.models


/**
 * @author Parker
 * 试题数据对象

 * @param content 题目内容
 * @param answerMethod 试题的类型 danxuan,duoxuan,tiankong,jieda,zuotu,jisuan,zonghe
 * @param answer 试题答案内容
 * @param explain 试题解析
 * @param id 试题id
 *
 */
data class Exercise(
        var content: String?,
        var answerMethod: String? = null,
        var answer: String? = null,
        var explain: String? = null,
        var difficulty: Float? = null,
        var tags: MutableList<Tag>? = mutableListOf(),
        var id: String? = null)