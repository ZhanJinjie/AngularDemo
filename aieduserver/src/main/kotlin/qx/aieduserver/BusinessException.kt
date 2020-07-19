package qx.aieduserver

import java.lang.RuntimeException

class BusinessException(code: String, message: String?) : RuntimeException(message, null, false, false) {
}