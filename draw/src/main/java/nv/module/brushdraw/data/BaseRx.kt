package nv.module.brushdraw.data

import android.annotation.SuppressLint
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class BaseRx {
    val composite = CompositeDisposable()
    inline fun <reified T> observableCreate(
        noinline onSubscribe: ((it: ObservableEmitter<T>) -> Unit),
        noinline onSuccess: ((it: T) -> Unit),
        noinline onError: ((it: Throwable) -> Unit)
    ) {
        composite.add(
            Observable.create<T> {
                onSubscribe(it)
            }.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onSuccess(it)
                }, {
                    onError(it)
                })
        )
    }

}