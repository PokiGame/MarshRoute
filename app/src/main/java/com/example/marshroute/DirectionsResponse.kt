data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>
)

data class Leg(
    val steps: List<Step>
)

data class Step(
    val start_location: DirectionsLocation,
    val end_location: DirectionsLocation
)

data class DirectionsLocation(
    val lat: Double,
    val lng: Double
)