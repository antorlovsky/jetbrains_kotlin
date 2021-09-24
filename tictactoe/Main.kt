package tictactoe

fun printGrid(grid: MutableList<MutableList<Char>>) {
    println("---------")

    for (row in grid) {
        print("| ")

        for (col in row) {
            print("$col ")
        }

        println('|')
    }

    println("---------")
}

fun isOccupiedCell(grid: MutableList<MutableList<Char>>, coordX: Int, coordY: Int) = grid[coordX][coordY] != '_'

fun makeMove(grid: MutableList<MutableList<Char>>, player: Char): MutableList<MutableList<Char>> {
    do {
        var correctInput = true

        print("Enter the coordinates:")
        val coordinates = readLine()!!.split(" ")
        val x = coordinates[0].first()
        val y = coordinates[1].first()

        if (!x.isDigit() || !y.isDigit()) {
            println("You should enter numbers!")
            correctInput = false
        } else {
            val digitX = x.digitToInt() - 1
            val digitY = y.digitToInt() - 1

            if (digitX !in 0..2 || digitY !in 0..2) {
                println("Coordinates should be from 1 to 3!")
                correctInput = false
            } else if (isOccupiedCell(grid, digitX, digitY)) {
                println("This cell is occupied! Choose another one!")
                correctInput = false
            } else {
                grid[digitX][digitY] = player
            }
        }
    } while (!correctInput)

    return grid
}

fun countSymbol(grid: MutableList<MutableList<Char>>, symbol: Char): Int {
    var count = 0

    for (row in grid) {
        for (col in row) {
            if (col == symbol) {
                ++count
            }
        }
    }

    return count
}

fun winPlayer(grid: MutableList<MutableList<Char>>, player: Char): Boolean {
    return grid[0][0] == player && grid[0][1] == player && grid[0][2] == player
        || grid[1][0] == player && grid[1][1] == player && grid[1][2] == player
        || grid[2][0] == player && grid[2][1] == player && grid[2][2] == player
        || grid[0][0] == player && grid[1][0] == player && grid[2][0] == player
        || grid[0][1] == player && grid[1][1] == player && grid[2][1] == player
        || grid[0][2] == player && grid[1][2] == player && grid[2][2] == player
        || grid[0][0] == player && grid[1][1] == player && grid[2][2] == player
        || grid[2][0] == player && grid[1][1] == player && grid[0][2] == player
}

fun getState(
        countX: Int,
        countO: Int,
        countEmpty: Int,
        winX: Boolean,
        winO: Boolean
): String {
    return if (countX - countO > 1 || countX - countO < -1 || winX && winO) {
        "Impossible"
    } else if (winX && !winO) {
        "X wins"
    } else if (winO && !winX) {
        "O wins"
    } else if (!winX && !winO && countEmpty == 0) {
        "Draw"
    } else {
        "Game not finished"
    }
}

fun main() {
    var grid = mutableListOf(
        mutableListOf<Char>('_', '_', '_'),
        mutableListOf<Char>('_', '_', '_'),
        mutableListOf<Char>('_', '_', '_')
    )
    printGrid(grid)

    var player = 'X'
    do {
        grid = makeMove(grid, player)
        printGrid(grid)

        val countX = countSymbol(grid, 'X')
        val countO = countSymbol(grid, 'O')
        val countEmpty = countSymbol(grid, '_')

        val winX = winPlayer(grid, 'X')
        val winO = winPlayer(grid, 'O')

        val state = getState(countX, countO, countEmpty, winX, winO)
        val end = state != "Game not finished"

        if (end) {
            println(state)
        } else {
            player = if (player == 'X') 'O' else 'X'
        }
    } while (!end)
}