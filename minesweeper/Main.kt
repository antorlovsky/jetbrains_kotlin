package minesweeper
import kotlin.random.Random

class Field(val rowCount: Int = 0, val colCount: Int = 0, val mineCount: Int = 0) {
    var started: Boolean = false
    val cells: MutableList<MutableList<Cell>> = mutableListOf()

    init {
        for (i in 0 until rowCount) {
            var minesPerRow: Int = mineCount / rowCount
            if (i < mineCount % rowCount)
                ++minesPerRow

            val minesList: MutableList<Int> = generateMinePositions(minesPerRow, colCount)

            val row: MutableList<Cell> = mutableListOf()

            for (j in 0 until colCount) {
                val cell = Cell(i, j)
                cell.value = if (minesList.indexOf(j) == -1) "." else "X"
                row.add(cell)
            }

            cells.add(row)
        }

        fillValues()
    }

    fun getCell(row: Int, col: Int) = cells[row][col]

    fun fillValues() {
        for (row in cells) {
            for (cell in row) {
                if (!cell.hasMine()) {
                    val neighbours = cell.getNeighbours(rowCount, colCount)

                    var mines = 0
                    for (neighbour in neighbours) {
                        if (getCell(neighbour[0], neighbour[1]).hasMine()) {
                            ++mines
                        }
                    }

                    cell.value = if (mines > 0) "$mines" else "."
                }
            }
        }
    }

    fun freeCell(freeCell: Cell) {
        for (row in cells) {
            for (cell in row) {
                if (!cell.hasMine() && !cell.opened && !cell.marked) {
                    cell.value = "X"
                    freeCell.value = "."

                    this.fillValues()
                    return
                }
            }
        }
    }

    fun showMines() {
        for (row in cells) {
            for (cell in row) {
                if (cell.hasMine()) {
                    cell.open()
                }
            }
        }
    }

    fun exploreCell(cell: Cell, auto: Boolean = true): Boolean {
        if (cell.opened) {
            return true
        }

        if (cell.hasMine()) {
            return if (auto) {
                true
            } else {
                this.showMines()
                false
            }
        }

        cell.open()

        if (!cell.hasMinesAround()) {
            val neighbours = cell.getNeighbours(rowCount, colCount)

            for (neighbour in neighbours) {
                exploreCell(getCell(neighbour[0], neighbour[1]))
            }
        }

        return true
    }
}

class Cell(val row: Int, val col: Int) {
    var value: String = "."
        set(newValue) {
            minesAround = if (newValue != "." && newValue != "X") newValue.toInt() else 0
            field = newValue
        }

    var marked: Boolean = false
    var opened: Boolean = false
    var minesAround: Int = 0

    fun hasMine() = value == "X"
    fun hasMinesAround() = minesAround > 0

    fun getVisibleValue(): String {
        return if (opened) {
            when {
                hasMine() -> "X"
                hasMinesAround() -> value
                else -> "/"
            }
        } else {
            if (marked) "*" else "."
        }
    }

    fun mark() {
        if (opened) {
            return
        }

        marked = !marked
    }

    fun open() {
        opened = true
    }

    fun getNeighbours(rowCount: Int, colCount: Int): MutableList<MutableList<Int>> {
        val neighbours: MutableList<MutableList<Int>> = mutableListOf()

        if (row > 0) {
            neighbours.add(mutableListOf(row - 1, col))

            if (col > 0) {
                neighbours.add(mutableListOf(row - 1, col - 1))
            }

            if (col < colCount - 1) {
                neighbours.add(mutableListOf(row - 1, col + 1))
            }
        }

        if (row < rowCount - 1) {
            neighbours.add(mutableListOf(row + 1, col))

            if (col > 0) {
                neighbours.add(mutableListOf(row + 1, col - 1))
            }

            if (col < colCount - 1) {
                neighbours.add(mutableListOf(row + 1, col + 1))
            }
        }

        if (col > 0) {
            neighbours.add(mutableListOf(row, col - 1))
        }

        if (col < colCount - 1) {
            neighbours.add(mutableListOf(row, col + 1))
        }

        return neighbours
    }
}

fun generateMinePositions(mineCount: Int, colCount: Int): MutableList<Int> {
    val mineList = mutableListOf<Int>()

    repeat(mineCount) {
        do {
            val position = Random.nextInt(colCount)
            val isUniquePosition = mineList.indexOf(position) == -1

            if (isUniquePosition) {
                mineList.add(position)
            }
        } while (!isUniquePosition)
    }

    return mineList
}

fun printBorder(length: Int) {
    print("—│")
    for (i in 1..length) {
        print("—")
    }
    println("│")
}

fun Field.print() {
    print(" │")
    for (i in 1..this.rowCount) {
        print(i)
    }
    println("│")

    printBorder(this.rowCount)

    for (i in this.cells.indices) {
        print("${i + 1}|")
        for (cell in this.cells[i]) {
            print(cell.getVisibleValue())
        }
        println("|")
    }

    printBorder(this.rowCount)
}

fun Field.makeMove(): Boolean {
    print("Set/unset mines marks or claim a cell as free:")
    val (y, x, command) = readLine()!!.split(" ")

    val cell = this.getCell(x.toInt() - 1, y.toInt() - 1)

    var safeMove = true

    if (command == "mine") {
        cell.mark()
    } else if (command == "free") {
        if (!this.started) {
            this.started = true

            if (cell.hasMine()) {
                this.freeCell(cell)
            }
        }

        safeMove = this.exploreCell(cell, false)
    }

    this.print()
    return safeMove
}

fun Field.checkState(): Boolean {
    var allMarked = true
    var allOpened = true

    for (row in this.cells) {
        for (cell in row) {
            if (cell.hasMine() && !cell.marked || !cell.hasMine() && cell.marked) {
                allMarked = false
            }

            if (!cell.hasMine() && !cell.opened) {
                allOpened = false
            }
        }
    }

    return allMarked || allOpened
}

fun main() {
    print("How many mines do you want on the field?")
    val mineCount = readLine()!!.toInt()

    val field = Field(9, 9, mineCount)
    field.print()

    do {
        if (!field.makeMove()) {
            println("You stepped on a mine and failed!")
            return
        }

        val state = field.checkState()
    } while (!state)

    println("Congratulations! You found all the mines!")
}
