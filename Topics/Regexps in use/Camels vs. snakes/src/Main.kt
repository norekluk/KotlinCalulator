fun getCamelStyleString(inputString: String): String {
    // put your code here
    val words = inputString.split("_")
    val finalWords = mutableListOf<String>()
    for (word in words) {
        val firstChar = word[0]
        finalWords.add(firstChar.titlecaseChar() + word.removePrefix(firstChar.toString()))
    }

    return finalWords.joinToString("")
}