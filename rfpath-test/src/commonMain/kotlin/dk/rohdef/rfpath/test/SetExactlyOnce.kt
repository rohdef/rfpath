package dk.rohdef.rfpath.test

/**
 * Used to set a value once and only once.
 * Throws exceptions if reuse is attempted or i value isn't set when reading
 */
internal data class SetExactlyOnce<T>(
    private var _value: T? = null
) {
    val value: T
        get() {
            return _value ?: throw IllegalStateException("Value must be set")
        }

    // TODO this is not thread safe
    fun setValue(value: T) {
        if (_value == null) {
            _value = value
        } else {
            throw IllegalStateException("Value has already been set")
        }
    }
}