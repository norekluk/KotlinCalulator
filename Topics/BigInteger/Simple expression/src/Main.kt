import java.math.BigInteger

fun main() {
    val bigIntList = mutableListOf<BigInteger>()
    repeat(4) {
        bigIntList.add(readln().toBigInteger())
    }
    println(-bigIntList[0] * bigIntList[1] + bigIntList[2] - bigIntList[3])
}