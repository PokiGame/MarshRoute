import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("geocode/json")
    suspend fun getCoordinatesFromPlusCode(
        @Query("address") plusCode: String,
        @Query("key") apiKey: String
    ): retrofit2.Response<GeocodingResponse>
}
