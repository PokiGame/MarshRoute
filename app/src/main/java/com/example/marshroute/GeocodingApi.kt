import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("geocode/json")
    suspend fun getCoordinatesFromPlusCode(
        @Query("address") plusCode: String,
        @Query("key") apiKey: String
    ): GeocodingResponse
}