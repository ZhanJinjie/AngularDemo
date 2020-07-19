package qx.aieduserver

import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.snippet.Snippet

class CommonDocs {
    companion object {
        fun commonPagingDocs(): List<Snippet> {
            return mutableListOf<Snippet>(
                    relaxedResponseFields(
                            fieldWithPath("content").description("数据对象列表"),
                            fieldWithPath("numberOfElements").description("总数量"),
                            fieldWithPath("totalPages").description("总页数")
                    ),
                    pathParameters(
                            parameterWithName("page").description("页编号"),
                            parameterWithName("size").description("每页数据条数"),
                            parameterWithName("sorts").description("排序规则"),
                            parameterWithName("filters").description("过滤条件"))
            )
        }
    }
}