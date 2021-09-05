class Reactor<T>() {
    // Your compute cell's addCallback method must return an object
    // that implements the Subscription interface.
    interface Subscription {
        fun cancel()
    }

    interface Cell<T> {
        var value: T
    }

    inner class InputCell<T>(override public var value: T): Cell<T> {

    }

    inner class ComputeCell<T>(vararg var inputs: Cell<T>, var lambda: (List<T>) -> (T)): Cell<T> {

        override var value: T = inputs[0].value

        init {

        }
    }
}

