
import android.util.Log
import com.squareup.moshi.*
import com.example.myapplication.data.remote.ErrorList
import com.example.myapplication.data.remote.ErrorState
import com.example.myapplication.utils.safeLog
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

fun Throwable.toBaseException(): BaseException {
    return when (val throwable = this) {
        is BaseException -> throwable
        is IOException -> BaseException.toNetworkError(throwable)
        is HttpException -> {
            val response = throwable.response()
            val httpCode = throwable.code()
            Log.d("zzz1 toBaseException", response.toString())
            if (response?.errorBody() == null) {
                return BaseException.toHttpError(
                    httpCode = httpCode,
                    response = response
                )
            }
            val serverErrorResponseBody = try {
                response.errorBody()
                    ?.string() ?: ""
            } catch (e: Exception) {
                e.safeLog()
                ""
            }
            Log.d("zzz2 toBaseException", serverErrorResponseBody)

            val serverErrorResponse =
                try {
                    Moshi.Builder()
                        .build()
                        .adapter(ErrorState::class.java)
                        .fromJson(serverErrorResponseBody)
                } catch (e: Exception) {
                    try {
                        Moshi.Builder()
                            .build()
                            .adapter(ErrorList::class.java)
                            .fromJson(serverErrorResponseBody)
                    } catch (e: Exception) {
                        e.safeLog()
                        ErrorState(message = "UNKNOWN_API_ERROR")
                    }
                }
            Log.d("zzz3 toBaseException", serverErrorResponse.toString())

            if (serverErrorResponse != null) {
                when (serverErrorResponse) {
                    is ErrorState -> {
                        Log.d("zzz4","")
                        BaseException.toServerError(
                            serverErrorResponse = serverErrorResponse.apply {
                                code = httpCode
                            },
                            response = response,
                            httpCode = httpCode
                        )
                    }
                    is ErrorList -> {
                        Log.d("zzz5","")
                        BaseException.toServerError(
                            serverErrorResponse = ErrorState(
                                serverErrorResponse.success,
                                serverErrorResponse.error_code,
                                serverErrorResponse.message,
                                serverErrorResponse.errors?.get(0),
                                httpCode,
                            ),
                            response = response,
                            httpCode = httpCode
                        )
                    }
                    else -> BaseException.toHttpError(
                        response = response,
                        httpCode = httpCode
                    )
                }
            } else {
                Log.d("zzz6","")
                BaseException.toHttpError(
                    response = response,
                    httpCode = httpCode
                )
            }
        }
        else -> BaseException.toUnexpectedError(throwable)
    }
}
class BaseException(
    val errorType: ErrorType,
    val serverErrorResponse: ErrorState? = null,
    val response: Response<*>? = null,
    val httpCode: Int = 0,
    cause: Throwable? = null
) : RuntimeException(cause?.message, cause) {
    override val message: String?
        get() = when (errorType) {
            ErrorType.HTTP -> response?.message()
            ErrorType.NETWORK, ErrorType.TIMEOUT -> cause?.message
            ErrorType.SERVER -> serverErrorResponse?.message
            ErrorType.UNEXPECTED -> cause?.message
        }
    companion object {
        fun toHttpError(response: Response<*>?, httpCode: Int) =
            BaseException(
                errorType = ErrorType.HTTP,
                response = response,
                httpCode = httpCode
            )
        fun toNetworkError(cause: Throwable) =
            BaseException(
                errorType = ErrorType.NETWORK,
                cause = cause
            )
        fun toServerError(
            serverErrorResponse: ErrorState,
            response: Response<*>?,
            httpCode: Int
        ) = BaseException(
            errorType = ErrorType.SERVER,
            serverErrorResponse = serverErrorResponse,
            response = response,
            httpCode = httpCode
        )
        fun toUnexpectedError(cause: Throwable) =
            BaseException(
                errorType = ErrorType.UNEXPECTED,
                cause = cause
            )
    }
}
/**
 * Identifies the error type which triggered a [BaseException]
 */
enum class ErrorType(val mode: Int) {
    /**
     * An [IOException] occurred while communicating to the server.
     */
    NETWORK(0),

    /**
     * A non-2xx HTTP status code was received from the server.
     */
    HTTP(1),

    /**
     * A error server with code & message
     */
    SERVER(2),

    /**
     * An internal error occurred while attempting to execute a request. It is best practice to
     * re-throw this exception so your application crashes.
     */
    UNEXPECTED(3),

    TIMEOUT(4)
}