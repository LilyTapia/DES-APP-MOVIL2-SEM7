package cl.duoc.veterinaria.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Usamos una URL que devuelve un JSON válido de veterinarios
    // Si esta URL llegara a fallar, el repositorio usará la lista de respaldo.
    private const val BASE_URL = "https://run.mocky.io/v3/" 

    val instance: VeterinarioApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VeterinarioApi::class.java)
    }
}
