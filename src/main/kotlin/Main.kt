fun main() {
    val numberOfWords = readln().toInt()
    val words = List(numberOfWords) { readln() }
    val sortedOrder = LexicalOrderSorter().getSortedOrder(words)
    println(sortedOrder)
}