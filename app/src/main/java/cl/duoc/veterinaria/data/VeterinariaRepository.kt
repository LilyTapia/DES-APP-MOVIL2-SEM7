package cl.duoc.veterinaria.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import cl.duoc.veterinaria.data.local.VeterinariaDatabase
import cl.duoc.veterinaria.data.local.entities.ConsultaEntity
import cl.duoc.veterinaria.data.local.entities.MascotaEntity
import cl.duoc.veterinaria.data.local.entities.PedidoEntity
import cl.duoc.veterinaria.data.local.entities.UsuarioEntity
import cl.duoc.veterinaria.data.remote.RetrofitClient
import cl.duoc.veterinaria.model.Veterinario
import cl.duoc.veterinaria.service.AgendaVeterinario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

interface IVeterinariaRepository {
    val totalMascotasRegistradas: StateFlow<Int>
    val totalConsultasRealizadas: StateFlow<Int>
    val nombreUltimoDueno: StateFlow<String>
    val listaMascotas: StateFlow<List<String>>
    val ultimaAtencionTipo: StateFlow<String?>
    val mascotasLocal: Flow<List<MascotaEntity>>
    val consultasLocal: Flow<List<ConsultaEntity>>
    val usuariosLocal: Flow<List<UsuarioEntity>>
    val pedidosLocal: Flow<List<PedidoEntity>>

    fun init(context: Context)
    fun registrarAtencion(nombreDueno: String, nombreMascota: String, especieMascota: String, tipoServicio: String? = null, edad: Int = 0, peso: Double = 0.0, consultaId: String? = null, fechaHora: String? = null, veterinario: String? = null, costo: Double = 0.0)
    suspend fun registrarUsuario(nombre: String, email: String, pass: String): UsuarioEntity
    suspend fun buscarUsuario(email: String, user: String): UsuarioEntity?
    suspend fun registrarPedidoRoom(pedido: PedidoEntity)
    suspend fun obtenerVeterinariosRemotos(): List<Veterinario>
    suspend fun eliminarMascotaRoom(mascota: MascotaEntity)
}

object VeterinariaRepository : IVeterinariaRepository {
    private const val TAG = "VeterinariaRepo"
    
    private val _totalMascotasRegistradas = MutableStateFlow(0)
    private val _totalConsultasRealizadas = MutableStateFlow(0)
    private val _nombreUltimoDueno = MutableStateFlow("N/A")
    private val _listaMascotas = MutableStateFlow<List<String>>(emptyList())
    private val _ultimaAtencionTipo = MutableStateFlow<String?>(null)
    
    private var prefs: SharedPreferences? = null
    private var database: VeterinariaDatabase? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var isInitialized = false

    override val totalMascotasRegistradas = _totalMascotasRegistradas.asStateFlow()
    override val totalConsultasRealizadas = _totalConsultasRealizadas.asStateFlow()
    override val nombreUltimoDueno = _nombreUltimoDueno.asStateFlow()
    override val listaMascotas = _listaMascotas.asStateFlow()
    override val ultimaAtencionTipo = _ultimaAtencionTipo.asStateFlow()

    override val mascotasLocal: Flow<List<MascotaEntity>> by lazy { 
        database?.mascotaDao()?.getAllMascotas() ?: flowOf(emptyList()) 
    }
    override val consultasLocal: Flow<List<ConsultaEntity>> by lazy { 
        database?.consultaDao()?.getAllConsultas() ?: flowOf(emptyList()) 
    }
    override val usuariosLocal: Flow<List<UsuarioEntity>> by lazy { 
        database?.usuarioDao()?.getAllUsuarios() ?: flowOf(emptyList()) 
    }
    override val pedidosLocal: Flow<List<PedidoEntity>> by lazy { 
        database?.pedidoDao()?.getAllPedidos() ?: flowOf(emptyList()) 
    }

    override fun init(context: Context) {
        if (isInitialized) return
        
        Log.d(TAG, "Levantando repositorio y base de datos...")
        val appContext = context.applicationContext
        prefs = appContext.getSharedPreferences("veterinaria_prefs", Context.MODE_PRIVATE)
        database = VeterinariaDatabase.getDatabase(appContext)
        
        // Carga inicial de veterinarios desde la API
        scope.launch {
            val lista = obtenerVeterinariosRemotos()
            AgendaVeterinario.veterinarios = lista
        }

        // Observamos cambios en mascotas para actualizar contadores de la UI
        scope.launch {
            mascotasLocal.collect { lista ->
                _totalMascotasRegistradas.value = lista.size
                _listaMascotas.value = lista.map { "${it.nombre} (${it.especie})" }
            }
        }
        
        scope.launch {
            consultasLocal.collect { lista ->
                _totalConsultasRealizadas.value = lista.size
            }
        }
        
        isInitialized = true
    }

    override suspend fun obtenerVeterinariosRemotos(): List<Veterinario> {
        return try {
            val apiResponse = RetrofitClient.instance.getVeterinarios()
            Log.d(TAG, "RETROFIT SUCCESS: Datos cargados desde el servidor")
            if (apiResponse.isNotEmpty()) apiResponse else getListaRespaldo()
        } catch (e: Exception) {
            // Si la API falla (ej: sin internet), tiramos de la lista local por seguridad
            Log.e(TAG, "RETROFIT ERROR: Usando protocolo de respaldo (Fallback)")
            getListaRespaldo()
        }
    }

    private fun getListaRespaldo() = listOf(
        Veterinario("Dr. Pérez", "General", "https://randomuser.me/api/portraits/men/1.jpg"),
        Veterinario("Dra. González", "Cirugía", "https://randomuser.me/api/portraits/women/1.jpg"),
        Veterinario("Dr. Soto", "Dermatología", "https://randomuser.me/api/portraits/men/2.jpg")
    )

    override fun registrarAtencion(nombreDueno: String, nombreMascota: String, especieMascota: String, tipoServicio: String?, edad: Int, peso: Double, consultaId: String?, fechaHora: String?, veterinario: String?, costo: Double) {
        _nombreUltimoDueno.value = nombreDueno
        _ultimaAtencionTipo.value = tipoServicio
        
        scope.launch {
            try {
                val nuevaMascota = MascotaEntity(
                    nombre = nombreMascota,
                    especie = especieMascota,
                    edad = edad,
                    pesoKg = peso,
                    ultimaVacunacion = LocalDate.now(),
                    nombreDueno = nombreDueno
                )
                database?.mascotaDao()?.insertMascota(nuevaMascota)

                if (consultaId != null && fechaHora != null && veterinario != null) {
                    val nuevaConsulta = ConsultaEntity(
                        idConsulta = consultaId,
                        mascotaNombre = nombreMascota,
                        duenoNombre = nombreDueno,
                        descripcion = tipoServicio ?: "Consulta General",
                        fechaHora = fechaHora,
                        veterinario = veterinario,
                        costo = costo,
                        estado = "Pendiente"
                    )
                    database?.consultaDao()?.insertConsulta(nuevaConsulta)
                }
                Log.d(TAG, "Atención guardada correctamente en Room")
            } catch (e: Exception) {
                Log.e(TAG, "Error persistiendo la atención: ${e.message}")
            }
        }
    }

    override suspend fun registrarUsuario(nombre: String, email: String, pass: String): UsuarioEntity {
        val user = UsuarioEntity(nombreUsuario = nombre, email = email, pass = pass)
        database?.usuarioDao()?.insertUsuario(user)
        return user
    }

    override suspend fun buscarUsuario(email: String, user: String): UsuarioEntity? = 
        database?.usuarioDao()?.findByEmailOrUser(email, user)

    override suspend fun registrarPedidoRoom(pedido: PedidoEntity) {
        database?.pedidoDao()?.insertPedido(pedido)
    }

    override suspend fun eliminarMascotaRoom(mascota: MascotaEntity) {
        database?.mascotaDao()?.deleteMascota(mascota)
    }
}
