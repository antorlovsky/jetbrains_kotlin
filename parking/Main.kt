package parking

class ParkingSpot(val number: Int, var free: Boolean = true) {
    var regNumber: String = ""
    var color: String = ""

    fun isFree() = free

    fun park(regNumber: String, color: String) {
        free = false

        this.regNumber = regNumber
        this.color = color
    }

    fun leave() {
        free = true

        this.regNumber = ""
        this.color = ""
    }

    fun getInfo() = "${number + 1} $regNumber $color"
}

class Parking(val spotCount: Int) {
    val spots: MutableList<ParkingSpot> = mutableListOf()
    var freeSpots: Int = spotCount

    init {
        for (i in 0 until spotCount) {
            spots.add(ParkingSpot(i))
        }
    }

    fun isVoid() = spotCount == 0

    fun getSpotByNumber(number: Int): ParkingSpot {
        return spots[number]
    }

    fun findFreeSpotNumber(): Int {
        for (i in spots.indices) {
            if (spots[i].isFree()) {
                return i
            }
        }

        return -1
    }

    fun park(regNumber: String, color: String) {
        val number = findFreeSpotNumber()

        if (number != -1) {
            spots[number].park(regNumber, color)
            --freeSpots
            println("$color car parked in spot ${number + 1}.")
        } else {
            println("Sorry, the parking lot is full.")
        }
    }

    fun leave(number: Int) {
        val spot = getSpotByNumber(number - 1)

        if (spot.isFree()) {
            println("There is no car in spot $number.")
        } else {
            spot.leave()
            ++freeSpots
            println("Spot $number is free.")
        }
    }

    fun status() {
        if (freeSpots == spotCount) {
            println("Parking lot is empty.")
        } else {
            for (spot in spots) {
                if (!spot.isFree()) println(spot.getInfo())
            }
        }
    }

    fun findByColor(color: String): MutableList<ParkingSpot> {
        val result = mutableListOf<ParkingSpot>()

        for (spot in spots) {
            if (!spot.isFree() && spot.color.uppercase() == color) {
                result.add(spot)
            }
        }

        return result
    }

    fun regByColor(color: String) {
        val queryResult = findByColor(color.uppercase())

        if (queryResult.isEmpty()) {
            println("No cars with color $color were found.")
        } else {
            println(queryResult.joinToString(", ") { it.regNumber })
        }
    }

    fun spotByColor(color: String) {
        val queryResult = findByColor(color.uppercase())

        if (queryResult.isEmpty()) {
            println("No cars with color $color were found.")
        } else {
            println(queryResult.joinToString(", ") { (it.number + 1).toString() })
        }
    }

    fun spotByReg(regNumber: String) {
        for (spot in spots) {
            if (!spot.isFree() && spot.regNumber == regNumber) {
                println(spot.number + 1)
                return
            }
        }

        println("No cars with registration number $regNumber were found.")
    }

    object Factory {
        fun create(spotCount: Int): Parking {
            println("Created a parking lot with $spotCount spots.")
            return Parking(spotCount)
        }
    }
}

fun main() {
    var parking = Parking(0)

    do {
        val userCommand = readLine()!!.split(" ")

        if (parking.isVoid() && userCommand[0] !in mutableListOf("create", "exit")) {
            println("Sorry, a parking lot has not been created.")
            continue
        }

        when (userCommand[0]) {
            "create" -> parking = Parking.Factory.create(userCommand[1].toInt())
            "park" -> parking.park(userCommand[1], userCommand[2])
            "leave" -> parking.leave(userCommand[1].toInt())
            "status" -> parking.status()
            "reg_by_color" -> parking.regByColor(userCommand[1])
            "spot_by_color" -> parking.spotByColor(userCommand[1])
            "spot_by_reg" -> parking.spotByReg(userCommand[1])
        }
    } while (userCommand[0] != "exit")
}
