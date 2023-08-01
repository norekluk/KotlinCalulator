fun main() {
    val text = readln()
    // write your code here
    val regex = "[aA]+".toRegex()
    print(text.replace(regex, "a"))
}