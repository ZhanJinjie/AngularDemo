package qx.aieduserver.query.utils

import org.springframework.data.domain.PageRequest

data class Query(var pageRequest: PageRequest, var filters: List<QueryFilter> = listOf())