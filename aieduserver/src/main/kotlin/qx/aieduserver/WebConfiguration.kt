package qx.aieduserver

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter
import com.alibaba.fastjson.support.spring.FastjsonSockJsMessageCodec
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.cors.reactive.CorsUtils
import org.springframework.web.method.HandlerTypePredicate
import org.springframework.web.reactive.config.*
import org.springframework.web.reactive.resource.ResourceWebHandler
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit
import java.util.function.Function

@Configuration
class WebConfiguration : WebFluxConfigurer {

    @Bean
    fun corsFilter(): WebFilter {
        return WebFilter { exchange, chain ->
            var request = exchange.request
            if (CorsUtils.isCorsRequest(request)) {
                var response = exchange.response
                var headers = response.headers
                headers.add("Access-Control-Allow-Origin", "*");
                headers.add("Access-Control-Allow-Methods", "*");
                headers.add("Access-Control-Max-Age", "3600");
                headers.add("Access-Control-Allow-Headers", "*");
                headers.add("Access-Control-Expose-Headers", "*");
                headers.add("Access-Control-Allow-Credentials", "true");
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return@WebFilter Mono.empty();
                }
            }
            chain.filter(exchange)
        }
    }

    override fun configurePathMatching(configurer: PathMatchConfigurer) {
        configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController::class.java))
    }

}