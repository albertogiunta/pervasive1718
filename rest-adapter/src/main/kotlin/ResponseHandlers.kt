import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result

object ResponseHandlers {

    val emptyHandler : (Request, Response, Result<String, FuelError>) -> Unit = {
        _, _, result-> when(result) {
            is Result.Failure -> {
                println("FAILURE:...")
                result.error.exception.printStackTrace()
            }
            is Result.Success -> {
                println("SUCCESS:${result.get()}")
            }
            else -> { }
        }
    }
}