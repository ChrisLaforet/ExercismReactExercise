class Reactor<T>() {
    // Your compute cell's addCallback method must return an object
    // that implements the Subscription interface.
    interface Subscription {
        fun cancel()
    }

    class Input<T>(public var value: T) {

    }

    class ValueOutput<T>(val value1: T, val value2: T) {
        var value: T = value1
    }

    class CellOutput<T>(var input: Input<T>) {
        var value: T = input.value
    }

    fun InputCell(value: T) : Input<T> = Input(value)

    fun ComputeCell(value1: T, value2: T) : ValueOutput<T> = ValueOutput(value1, value2)

    fun ComputeCell(input: Input<T>) : CellOutput<T> = CellOutput(input)
}

