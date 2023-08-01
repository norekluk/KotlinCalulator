import java.math.BigInteger
import kotlin.math.abs

fun main() {
    // write your code here
    val a = readln().toBigInteger()
    val b = readln().toBigInteger()
    val max = (a + b + (a - b).abs())/ BigInteger.valueOf(2)
    println(max)
}