package qx.aieduserver.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import qx.aieduserver.BusinessException

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler
    fun handleBusinessException(ex:BusinessException):ResponseEntity<BusinessException>{
        return ResponseEntity(ex, HttpStatus.BAD_REQUEST)
    }
}