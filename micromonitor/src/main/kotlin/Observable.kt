import io.reactivex.Flowable

/**
 * The interface of Observable element that provide the method to create
 * an observable stream of data according to RxKotlin (ReactiveX API)
 * */
interface Observable<T> {

    /**
     * Create the observable stream according to RxKotlin (ReactiveX API)
     *
     * @param refreshPeriod the period of data stream generation
     * @return a Flowable object (see RxKotlin documentation)
     * */
    fun createObservable(refreshPeriod: Long): Flowable<T>

    fun stopObservation()
}