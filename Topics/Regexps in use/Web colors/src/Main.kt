fun main() {
    val text = readLine()!!
    val regexColors = "#[0-9a-fA-F]{6}\\b".toRegex()
    // write your code here
    val result = regexColors.findAll(text)
    for (res in result) {
        println(res.value)
    }
}