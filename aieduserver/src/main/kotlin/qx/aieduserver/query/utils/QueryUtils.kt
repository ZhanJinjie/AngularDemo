package qx.aieduserver.query.utils

import com.alibaba.fastjson.JSONObject
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class QueryUtils {
    companion object {
        fun buildQuery(page: Int, size: Int, sorts: String, filters: String): Query {
            var pageable: PageRequest;
            var filterRequests: List<QueryFilter>? = null;
            if ("none" != sorts && sorts.isNotEmpty()) {
                var sortRequests = JSONObject.parseArray(sorts, QuerySort::class.java)
                var sort: Sort? = null
                sortRequests.forEach { req ->
                    sort = if (null == sort) {
                        Sort.by(req.field)
                    } else {
                        sort!!.and(Sort.by(req.field))
                    }
                    sort = if ("desc" == req.direction) {
                        sort!!.descending()
                    } else {
                        sort!!.ascending()
                    }
                }
                pageable = PageRequest.of(page, size, sort!!)
            } else {
                pageable = PageRequest.of(page, size)
            }
            if ("none" != filters) {
                filterRequests = JSONObject.parseArray(filters, QueryFilter::class.java)
            }
            if (null == filterRequests) {
                filterRequests = listOf();
            }
            return Query(pageable, filterRequests)
        }
    }
}