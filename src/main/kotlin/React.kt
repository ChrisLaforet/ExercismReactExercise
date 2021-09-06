class Reactor<T>() {
    // Your compute cell's addCallback method must return an object
    // that implements the Subscription interface.
    interface Subscription {
        fun cancel()
    }

    abstract class Cell<T> {
        abstract var value: T

        private val observers = mutableListOf<Observer>()

        protected fun onChange() {
            observers.forEach { it.changed() }
        }

        protected fun onChangeComplete() {
            observers.forEach { it.changeComplete() }
        }

        fun addObserver(cell: Observer) {
            observers.add(cell)
        }
    }

    interface Observer {
        fun changed()
        fun changeComplete()
    }

    inner class InputCell(var initialValue: T): Cell<T>() {

        override var value: T = initialValue
        set (value) {
            field = value

            onChange()
            onChangeComplete()
        }
    }

    inner class CallbackSubscription(public val callback: (T) -> Unit, val cancelOperation: () -> Unit): Subscription {
        override fun cancel() {
            cancelOperation.invoke()
        }
    }

    inner class ComputeCell(vararg var inputs: Cell<T>, var lambda: (List<T>) -> (T)): Cell<T>(), Observer {
        override var value: T = computeLambda()
        private var lastValue: T

        init {
            for (input in inputs) {
                input.addObserver(this)
            }
            compute()
            lastValue = value
        }

        private fun computeLambda(): T {
            return lambda.invoke(inputs.map { it.value })
        }

        private fun compute() {
            value = computeLambda()
        }

        override fun changed() {
            compute()
            onChange()
        }

        private var subscriptionIdGenerator: Int = 1
        private val subscriptions = mutableMapOf<Int, CallbackSubscription>()

        fun addCallback(callback: (T) -> Unit): Subscription {
            val key = subscriptionIdGenerator++
            val subscription = CallbackSubscription(callback) { subscriptions.remove(key) }
            subscriptions[key] = subscription
            return subscription
        }

        override fun changeComplete() {
            if (!lastValue!!.equals(value)) {
                subscriptions.forEach {
                    it.value.callback.invoke(value)
                }
                lastValue = value
            }
            onChangeComplete()
        }
    }
}

