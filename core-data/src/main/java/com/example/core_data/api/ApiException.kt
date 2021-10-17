package com.example.core_data.api

sealed class ApiException {

    object Offline : ApiException()
    object Timeout : ApiException()
    object Network : ApiException()
    data class FailedResponse<T>(val error: T? = null) : ApiException()
    data class InvalidResponse(val throwable: Throwable) : ApiException()
    data class Unknown(val throwable: Throwable) : ApiException()

    companion object {
        const val INVALID_SESSION = 401
        const val BAD_REQUEST = 400
        const val FORBIDDEN = 403
        const val NOT_FOUND = 404
        const val SERVER_ERROR = 500
        const val OTP_STILL_ACTIVE = 432
        const val INVALID_USER = 421
        const val FAILED_TO_LOGIN = 422
        const val FAILED_TO_REQUEST_OTP_STILL_HAVE_ACTIVE_OTP = 423
        const val FAILED_TO_REQUEST_OTP_SERVER_ERROR = 424
        const val INVALID_OTP = 425
        const val OTP_IS_EXPIRED = 433
        const val FAILED_TO_VALIDATE_OTP = 426
        const val LOGOUT_FAILED = 427
        const val VALIDATION_INPUT_REQUIRED = 428
        const val VALIDATION_INPUT_MINIMUM = 431
        const val VALIDATION_INPUT_MAX = 429
        const val INVALID_DATA_ENCRYPTION = 434
        const val INVALID_SECOND_SIGNATURE = 464
        const val SERVER_FAILED_TO_CREATE_PIN = 435
        const val INVALID_PIN = 436
        const val SERVER_FAILED_TO_LOAD_DOCTOR_DETAIL = 437
        const val REACHED_MAXIMUM_PIN_ATTEMPT = 438
        const val REACHED_MAXIMUM_OTP_ATTEMPT = 443
        const val SERVER_FAILED_TO_SET_LANGUAGE = 439
        const val SERVER_FAILED_SUBMIT_REGISTER_REQUEST = 440
        const val INVALID_SIGNATURE = 441
        const val ALREADY_SUBMIT_REGISTER_REQUEST = 442
        const val SERVER_FAILED_TO_LOAD_CALENDAR_LIST = 444
        const val SERVER_FAILED_TO_LOAD_HEALTH_HISTORY = 445
        const val SERVER_FAILED_TO_LOAD_DOCTOR_PROFILE = 446
        const val SERVER_FAILED_TO_UPDATE_DOCTOR_PROFILE = 447
        const val SERVER_FAILED_TO_LOAD_ACTIVE_IPD_DATA = 449
        const val FAILED_TO_SAVE_OT_SCHEDULE_ALREADY_UPDATE = 452
        const val FAILED_TO_SAVE_OT_SCHEDULE = 450
        const val SERVER_FAILED_TO_SAVE_OT_SCHEDULE_UPDATE = 451
        const val FAILED_ALREADY_LOGGED_IN = 460
        const val FAILED_DEVICE_REGISTRATION = 461
        const val OTP_REQUIRE_PARAM = 462
        const val USER_BLOCKED = 460
        const val INVALID_SECOND_SIGN = 464
        const val CITCALL_FAILED_TO_CALL = 465
        const val REQUEST_TIME_OUT = 456
    }

}