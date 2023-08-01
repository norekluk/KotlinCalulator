fun main() {
    // write your code here
    val a = readln().toBigDecimal()
    val b = readln().toBigDecimal()
    val (q, r) = a.divideAndRemainder(b)
    println("$a = $b * $q + $r")

}