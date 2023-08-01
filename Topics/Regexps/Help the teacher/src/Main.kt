fun main() {
    val report = readLine()!!
    val regex = "\\d wrong answers?".toRegex()
    println(regex.matches(report))
}