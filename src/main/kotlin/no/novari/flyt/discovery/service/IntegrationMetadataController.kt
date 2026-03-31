package no.novari.flyt.discovery.service

import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import no.novari.flyt.discovery.service.model.dtos.InstanceMetadataContentDto
import no.novari.flyt.discovery.service.model.dtos.IntegrationMetadataDto
import no.novari.flyt.discovery.service.model.entities.IntegrationMetadata
import no.novari.flyt.discovery.service.validation.ValidationErrorsFormattingService
import no.novari.flyt.webresourceserver.UrlPaths.INTERNAL_API
import no.novari.flyt.webresourceserver.security.user.UserAuthorizationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RequestMapping("$INTERNAL_API/metadata")
@RestController
class IntegrationMetadataController(
    private val integrationMetadataService: IntegrationMetadataService,
    private val validator: Validator,
    private val validationErrorsFormattingService: ValidationErrorsFormattingService,
    private val userAuthorizationService: UserAuthorizationService,
) {
    @GetMapping(params = ["kildeapplikasjonId", "kildeapplikasjonIntegrasjonId", "bareSisteVersjoner"])
    fun getIntegrationMetadataForSourceApplication(): ResponseEntity<Collection<IntegrationMetadata>> =
        ResponseEntity
            .badRequest()
            .build()

    @GetMapping(params = ["kildeapplikasjonId"])
    fun getIntegrationMetadataForSourceApplication(
        authentication: Authentication,
        @RequestParam(name = "kildeapplikasjonId") sourceApplicationId: Long,
        @RequestParam(name = "bareSisteVersjoner", required = false) onlyLatestVersions: Boolean?,
    ): ResponseEntity<Collection<IntegrationMetadataDto>> {
        userAuthorizationService.checkIfUserHasAccessToSourceApplication(authentication, sourceApplicationId)

        val integrationMetadata =
            integrationMetadataService.getIntegrationMetadataForSourceApplication(
                sourceApplicationId = sourceApplicationId,
                onlyLatestVersions = onlyLatestVersions ?: false,
            )

        return ResponseEntity.ok(integrationMetadata)
    }

    @GetMapping(params = ["kildeapplikasjonIds"])
    fun getIntegrationMetadataForSourceApplications(
        authentication: Authentication,
        @RequestParam(name = "kildeapplikasjonIds") sourceApplicationIds: Collection<Long>,
        @RequestParam(name = "bareSisteVersjoner", required = false) onlyLatestVersions: Boolean?,
    ): ResponseEntity<Map<Long, Collection<IntegrationMetadataDto>>> {
        sourceApplicationIds.forEach { sourceApplicationId ->
            userAuthorizationService.checkIfUserHasAccessToSourceApplication(authentication, sourceApplicationId)
        }

        val integrationMetadata =
            integrationMetadataService.getIntegrationMetadataForSourceApplications(
                sourceApplicationIds = sourceApplicationIds,
                onlyLatestVersions = onlyLatestVersions ?: false,
            )

        return ResponseEntity.ok(integrationMetadata)
    }

    @GetMapping(params = ["kildeapplikasjonId", "kildeapplikasjonIntegrasjonId"])
    fun getIntegrationMetadataForIntegration(
        authentication: Authentication,
        @RequestParam(name = "kildeapplikasjonId") sourceApplicationId: Long,
        @RequestParam(name = "kildeapplikasjonIntegrasjonId") sourceApplicationIntegrationId: String,
    ): ResponseEntity<Collection<IntegrationMetadataDto>> {
        userAuthorizationService.checkIfUserHasAccessToSourceApplication(authentication, sourceApplicationId)

        val integrationMetadata =
            integrationMetadataService.getAllForSourceApplicationIdAndSourceApplicationIntegrationId(
                sourceApplicationId = sourceApplicationId,
                sourceApplicationIntegrationId = sourceApplicationIntegrationId,
            )

        return ResponseEntity.ok(integrationMetadata)
    }

    @GetMapping("{metadataId}/instans-metadata")
    fun getInstanceElementMetadataForIntegrationMetadataWithId(
        authentication: Authentication,
        @PathVariable metadataId: Long,
    ): ResponseEntity<InstanceMetadataContentDto?> {
        val integrationMetadataDto =
            integrationMetadataService.getById(metadataId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        userAuthorizationService.checkIfUserHasAccessToSourceApplication(
            authentication,
            requireNotNull(integrationMetadataDto.sourceApplicationId),
        )

        return ResponseEntity.ok(integrationMetadataDto.instanceMetadata)
    }

    @PostMapping
    fun post(
        authentication: Authentication,
        @RequestBody integrationMetadataDto: IntegrationMetadataDto,
    ): ResponseEntity<Void> {
        val constraintViolations: Set<ConstraintViolation<IntegrationMetadataDto>> =
            validator.validate(
                integrationMetadataDto,
            )
        if (constraintViolations.isNotEmpty()) {
            throw ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                validationErrorsFormattingService.format(constraintViolations),
            )
        }

        userAuthorizationService.checkIfUserHasAccessToSourceApplication(
            authentication,
            requireNotNull(integrationMetadataDto.sourceApplicationId),
        )

        if (integrationMetadataService.versionExists(integrationMetadataDto)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Version already exists")
        }

        integrationMetadataService.save(integrationMetadataDto)
        return ResponseEntity.ok().build()
    }
}
