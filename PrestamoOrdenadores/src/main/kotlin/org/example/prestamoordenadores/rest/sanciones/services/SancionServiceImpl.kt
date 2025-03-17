package org.example.prestamoordenadores.rest.sanciones.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.prestamoordenadores.rest.sanciones.dto.SancionRequest
import org.example.prestamoordenadores.rest.sanciones.dto.SancionResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionUpdateRequest
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError
import org.example.prestamoordenadores.rest.sanciones.mappers.SancionMapper
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.example.prestamoordenadores.rest.sanciones.repositories.SancionRepository
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.lighthousegames.logging.logging
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

private val logger = logging()

@Service
@CacheConfig(cacheNames = ["sanciones"])
class SancionServiceImpl(
    private val repository : SancionRepository,
    private val mapper: SancionMapper,
    private val userRepository: UserRepository
) : SancionService {
    override suspend fun getAllSanciones(): Result<List<SancionResponse>, SancionError> {
        logger.debug { "Obteniendo todas las sanciones" }

        return withContext(Dispatchers.IO) {
            val sanciones = repository.findAll()
            Ok(mapper.toSancionResponseList(sanciones))
        }
    }

    @Cacheable(key = "#guid")
    override suspend fun getSancionByGuid(guid: String): Result<SancionResponse?, SancionError> {
        logger.debug { "Obteniendo sancion con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            val sancion = repository.findByGuid(guid)

            if (sancion == null) {
                Err(SancionError.SancionNotFound(guid))
            } else {
                Ok(mapper.toSancionResponse(sancion))
            }
        }
    }

    @CachePut(key = "#result.guid")
    override suspend fun createSancion(sancion: SancionRequest): Result<SancionResponse, SancionError> {
        logger.debug { "Creando nueva sancion" }

        return withContext(Dispatchers.IO) {
            val user = userRepository.findByGuid(sancion.userGuid)
            if (user == null) {
                return@withContext Err(SancionError.UserNotFound("Usuario con GUID: ${sancion.userGuid} no encontrado"))
            }

            val tipoNormalizado = sancion.tipoSancion.replace(" ", "_").uppercase()
            sancion.tipoSancion = tipoNormalizado

            val sancionCreada = mapper.toSancionFromRequest(sancion)
            repository.save(sancionCreada)

            Ok(mapper.toSancionResponse(sancionCreada))
        }
    }

    @CachePut(key = "#result.guid")
    override suspend fun updateSancion(guid: String, sancion: SancionUpdateRequest): Result<SancionResponse?, SancionError> {
        logger.debug { "Buscando sancion" }

        return withContext(Dispatchers.IO) {
            val existingSancion = repository.findByGuid(guid)
            if (existingSancion == null) {
                return@withContext Err(SancionError.SancionNotFound("Sancion no encontrada"))
            }

            logger.debug { "Actualizando sancion" }
            val tipoNormalizado = sancion.tipo.replace(" ", "_").uppercase()

            existingSancion.tipoSancion = TipoSancion.valueOf(tipoNormalizado)
            existingSancion.updatedDate = LocalDateTime.now()

            repository.save(existingSancion)

            Ok(mapper.toSancionResponse(existingSancion))
        }
    }

    @CachePut(key = "#guid")
    override suspend fun deleteSancionByGuid(guid: String): Result<SancionResponse?, SancionError> {
        logger.debug { "Buscando sancion" }

        return withContext(Dispatchers.IO) {
            val existingSancion = repository.findByGuid(guid)
            if (existingSancion == null) {
                return@withContext Err(SancionError.SancionNotFound("Sancion no encontrada"))
            }

            logger.debug { "Eliminando sancion" }
            repository.delete(existingSancion)

            Ok(mapper.toSancionResponse(existingSancion))
        }
    }

    @Cacheable(key = "#fecha")
    override suspend fun getByFecha(fecha: LocalDate): Result<List<SancionResponse>, SancionError> {
        logger.debug { "Obteniendo sanciones con fecha: $fecha" }

        return withContext(Dispatchers.IO) {
            val sanciones = repository.findSancionByFechaSancion(fecha)
            Ok(mapper.toSancionResponseList(sanciones))
        }
    }

    @Cacheable(key = "#tipo")
    override suspend fun getByTipo(tipo: String): Result<List<SancionResponse>, SancionError> {
        logger.debug { "Obteniendo sanciones de tipo: $tipo" }

        return withContext(Dispatchers.IO) {
            val tipoNormalizado = tipo.replace(" ", "_").uppercase()

            val tipoEnum = TipoSancion.entries.find { it.name == tipoNormalizado }
            if (tipoEnum == null) {
                return@withContext Err(SancionError.SancionNotFound("Sancion de tipo '$tipo' no encontrada"))
            }

            val sanciones = repository.findSancionByTipoSancion(tipoEnum)

            Ok(mapper.toSancionResponseList(sanciones))
        }
    }

    @Cacheable(key = "#userGuid")
    override suspend fun getSancionByUserGuid(userGuid: String): Result<List<SancionResponse>, SancionError> {
        logger.debug { "Obteniendo sanciones de user con GUID: $userGuid" }

        return withContext(Dispatchers.IO) {
            val user = userRepository.findByGuid(userGuid)
            if (user == null) {
                return@withContext Err(SancionError.UserNotFound("Usuario no encontrado"))
            }

            val sanciones = repository.findByUserGuid(userGuid)

            Ok(mapper.toSancionResponseList(sanciones))
        }
    }
}