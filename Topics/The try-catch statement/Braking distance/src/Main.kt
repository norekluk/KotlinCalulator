fun calculateBrakingDistance(v1: String, a: String): Int {
    try {
        val v1Int = v1.toInt()
        return -(v1Int * v1Int) / 2.times(a.toInt())
    } catch (e: ArithmeticException) {
        println("The car does not slow down!")
    } catch (e: java.lang.Exception) {
        println(e.message)
    }
    return -1
}