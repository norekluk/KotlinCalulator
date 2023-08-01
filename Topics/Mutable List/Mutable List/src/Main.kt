fun names(namesList: List<String>): List<String> {
    val nameCountMap = namesList.groupingBy { it }.eachCount()
    return nameCountMap.map { (k, v) -> "The name $k is repeated $v times" }
}