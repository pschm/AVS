package scheduler

class DistancesOverTime(dist: Double,neededTime: Long) {
    var distance: Double = 0.0
    var time: Long = 0

    init{
        distance = dist
        time = neededTime
    }
}