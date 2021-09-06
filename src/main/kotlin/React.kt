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

        fun addObserver(cell: Observer) {
            observers.add(cell)
        }
    }

    interface Observer {
        fun changed()
    }

    inner class InputCell<T>(var initialValue: T): Cell<T>() {

        override var value: T = initialValue
        set (value) {
            field = value

            onChange()
        }
    }

    inner class ComputeCell<T>(vararg var inputs: Cell<T>, var lambda: (List<T>) -> (T)): Cell<T>(), Observer {
        override var value: T = inputs[0].value

        init {
            for (input in inputs) {
                input.addObserver(this)
            }
            compute()
        }

        private fun compute() {
            value = lambda(inputs.map { it.value })
        }

        override fun changed() {
            compute()
            onChange()
        }
    }
}

