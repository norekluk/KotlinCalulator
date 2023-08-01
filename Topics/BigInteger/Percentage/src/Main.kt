import java.math.BigDecimal
import java.math.BigInteger

fun main() {
    // write your code here
    val ONE_HUNDRED = BigInteger.valueOf(100)
    val a = readln().toBigInteger()
    val b = readln().toBigInteger()
    val sum = a.plus(b)
    val fractalA = a.multiply(ONE_HUNDRED).divide(sum)
    val fractalB = b.multiply(ONE_HUNDRED).divide(sum)
    println("$fractalA% $fractalB%")


}