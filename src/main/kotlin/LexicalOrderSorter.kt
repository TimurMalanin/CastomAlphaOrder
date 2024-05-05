/**
 * A class that sorts characters lexicographically based on the sequence of words.
 */
class LexicalOrderSorter {
    private val adjacencyList = mutableMapOf<Char, MutableList<Char>>()
    private val allCharacters = ('a'..'z').toMutableSet()

    /**
     * Determines a lexicographical order of characters based on the list of words provided.
     *
     * @param words The list of words to analyze.
     * @return A string representing the ordered characters, or "Impossible" if no valid order exists.
     */
    fun getSortedOrder(words: List<String>): String {
        if (!buildAdjacencyList(words)) return "Impossible"
        return topologicalSort() ?: "Impossible"
    }

    /**
     * Builds an adjacency list for all characters in the input words that have a direct lexicographical order.
     *
     * @param words A list of words used to build the graph.
     * @return true if the adjacency list was built successfully without contradictions; false otherwise.
     */
    private fun buildAdjacencyList(words: List<String>): Boolean {
        for (i in 0..<words.size - 1) {
            if (!processWordPair(words[i], words[i + 1])) return false
        }
        return true
    }

    /**
     * Processes a pair of words and updates the adjacency list if the characters at any position differ.
     *
     * @param firstWord The first word in the comparison.
     * @param secondWord The second word in the comparison.
     * @return true if the words contribute to a valid lexicographical order; false if a contradiction is found.
     */
    private fun processWordPair(firstWord: String, secondWord: String): Boolean {
        val minLength = minOf(firstWord.length, secondWord.length)
        for (j in 0..<minLength) {
            if (firstWord[j] != secondWord[j]) {
                updateAdjacencyList(firstWord[j], secondWord[j])
                return true
            }
            if (j == minLength - 1 && firstWord.length > secondWord.length) {
                return false
            }
        }
        return true
    }

    /**
     * Updates the adjacency list with a directed edge from start to end character.
     *
     * @param start The starting character of the edge.
     * @param end The ending character of the edge.
     */
    private fun updateAdjacencyList(start: Char, end: Char) {
        adjacencyList.getOrPut(start) { mutableListOf() }.add(end)
        allCharacters.add(start)
        allCharacters.add(end)
    }

    /**
     * Performs a topological sort on the directed graph formed by characters and their relationships.
     *
     * @return A string representing the characters in sorted order, or null if sorting is not possible (cycle detected).
     */
    private fun topologicalSort(): String? {
        val visited = mutableMapOf<Char, Boolean>()
        val recursionStack = mutableMapOf<Char, Boolean>()
        val order = mutableListOf<Char>()

        initializeVisited(adjacencyList.keys, visited, recursionStack)

        for (key in adjacencyList.keys) {
            if (!visited[key]!! && !dfs(key, visited, recursionStack, order)) {
                return null
            }
        }

        return appendUnvisitedCharacters(order, visited)
    }

    /**
     * Initializes the visited and recursion stack maps for each character.
     *
     * @param keys The set of characters to be initialized.
     * @param visited Map tracking visited characters.
     * @param recursionStack Map tracking recursion to detect cycles.
     */
    private fun initializeVisited(keys: Set<Char>, visited: MutableMap<Char, Boolean>, recursionStack: MutableMap<Char, Boolean>) {
        keys.forEach {
            visited[it] = false
            recursionStack[it] = false
        }
    }

    /**
     * Appends any characters not yet added to the result to maintain complete character set.
     *
     * @param order The current sorted order of characters.
     * @param visited Map indicating which characters have been visited.
     * @return A complete string of characters maintaining lexicographical order.
     */
    private fun appendUnvisitedCharacters(order: MutableList<Char>, visited: MutableMap<Char, Boolean>): String {
        var result = order.reversed().joinToString("")
        allCharacters.filter { !visited.getOrDefault(it, false) }.forEach { result += it }
        return result
    }

    /**
     * Depth-first search to perform topological sorting.
     *
     * @param current The current character being visited.
     * @param visited Map tracking visited characters.
     * @param recursionStack Map tracking characters in the current recursion stack.
     * @param order List capturing the topological order of characters.
     * @return true if the current branch can be completed without cycles; false if a cycle is detected.
     */
    private fun dfs(
        current: Char,
        visited: MutableMap<Char, Boolean>,
        recursionStack: MutableMap<Char, Boolean>,
        order: MutableList<Char>
    ): Boolean {
        if (recursionStack[current] == true) return false
        if (visited[current] == true) return true

        visited[current] = true
        recursionStack[current] = true

        for (neighbor in adjacencyList[current] ?: emptyList()) {
            if (!dfs(neighbor, visited, recursionStack, order)) {
                return false
            }
        }

        order.add(current)
        recursionStack[current] = false
        return true
    }
}
