// GeocodingResponse
data class GeocodingResponse(
    val results: List<Result>
)

data class Result(
    val geometry: Geometry
)

data class Geometry(
    val location: GeocodingLocation
)

data class GeocodingLocation(
    val lat: Double,
    val lng: Double
)

