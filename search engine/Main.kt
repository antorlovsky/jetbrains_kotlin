package search

import java.io.File

class SearchEngine(fileName: String) {
    private val database: MutableList<String> = mutableListOf()
    private val invertedIndex: MutableMap<String, MutableSet<Int>> = mutableMapOf()

    init {
        File(fileName).forEachLine {
            database.add(it)
            val index = database.size - 1

            for (word in it.lowercase().replace("\\s+", " ").split(" ")) {
                if (invertedIndex.containsKey(word)) {
                    invertedIndex[word]!!.add(index)
                } else {
                    invertedIndex += word to mutableSetOf(index)
                }
            }
        }
    }

    fun printResultForQuery(line: String, strategy: String) {
        val query = line.lowercase().split(" ")

        var indexes = setOf<Int>()

        when (strategy) {
            "ALL" -> {
                for (word in query) {
                    indexes = indexes.intersect(findIndexesForWord(word))
                }
            }
            "ANY" -> {
                for (word in query) {
                    indexes = indexes.union(findIndexesForWord(word))
                }
            }
            "NONE" -> {
                indexes = IntArray(database.size) { it }.toSet()
                for (word in query) {
                    indexes = indexes.subtract(findIndexesForWord(word))
                }
            }
        }

        if (indexes.isEmpty()) {
            println("No matching people found.")
        } else {
            println("\nPeople found:")
            for (index in indexes) {
                println(database[index])
            }
        }
    }

    private fun findIndexesForWord(word: String): Set<Int> {
        return if (invertedIndex.containsKey(word)) {
            invertedIndex[word]!!.toSet()
        } else {
            emptySet()
        }
    }

    fun printDatabase() {
        println("=== List of people ===")
        for (line in database) println(line)
    }
}

fun main(args: Array<String>) {
    val se = SearchEngine(args[1])

    while (true) {
        println("=== Menu ===")
        println("1. Find a person")
        println("2. Print all people")
        println("0. Exit")

        when (readLine()!!.toInt()) {
            1 -> {
                println("Select a matching strategy: ALL, ANY, NONE")
                val strategy = readLine()!!

                println("Enter a name or email to search all suitable people.")
                se.printResultForQuery(readLine()!!, strategy)
            }
            2 -> se.printDatabase()
            0 -> break
            else -> println("Incorrect option! Try again.")
        }
    }
}
