# FINT Flyt Discovery Service

Spring Boot service that stores and shares integration metadata for FINT Flyt. It ingests integration definitions from Kafka, exposes an internal API for querying and publishing metadata versions, and answers Kafka request/reply lookups for other services that need instance metadata definitions.

## Highlights

- **Internal metadata API** — internal controller under `/api/intern/metadata` serves metadata for source applications and allows trusted clients to publish new versions.
- **Kafka ingestion** — consumes `integration-metadata-received` events and persists previously unseen versions.
- **Request/reply metadata lookups** — answers Kafka `metadata` and `instance-metadata` requests with cached JPA entities to keep downstream services in sync.
- **Postgres persistence** — JPA entities backed by Flyway migrations capture integration- and instance-level metadata structures.
- **Validation & authorization** — Bean Validation plus `UserAuthorizationService` ensure callers can only read/write metadata for permitted source applications.

## Architecture Overview

| Component | Responsibility |
| --- | --- |
| `IntegrationMetadataController` | Internal REST controller for listing metadata by source application/integration, returning instance metadata content, and publishing new versions. |
| `IntegrationMetadataService` | Business logic for fetching, deduplicating, and saving metadata while coordinating DTO ↔ entity mappings. |
| `IntegrationMetadataRepository` | Spring Data JPA repository with helpers for latest-version queries and existence checks per source app/integration/version. |
| `IntegrationMetadataMappingService` | Maps integration metadata between transport DTOs and entities, delegating nested content conversions. |
| `InstanceMetadataContentMappingService` / `InstanceValueMetadataMappingService` / `InstanceObjectCollectionMetadataMappingService` / `InstanceMetadataCategoryMappingService` | Convert nested instance metadata structures between DTOs and entities. |
| `IntegrationMetadataEventConsumerConfiguration` | Kafka event consumer for `integration-metadata-received` that provisions the topic (1 partition, 7-day retention) and persists non-duplicate payloads. |
| `MetadataRequestConsumerConfiguration` | Kafka request/reply consumers for `metadata` and `instance-metadata` topics that respond with full metadata or just instance metadata content by ID. |
| `ValidationErrorsFormattingService` | Formats Bean Validation errors for API responses; used by the controller on POSTs. |

## HTTP API

Base path: `/api/intern/metadata`

| Method | Path | Description | Request body | Response |
| --- | --- | --- | --- | --- |
| `GET` | `/?kildeapplikasjonId={id}&bareSisteVersjoner={bool?}` | List metadata for a source application; optional `bareSisteVersjoner` limits to latest versions per integration. | – | `200 OK` with `IntegrationMetadataDto[]`; `403/404/500` on errors. |
| `GET` | `/?kildeapplikasjonId={id}&kildeapplikasjonIntegrasjonId={integrationId}` | List all versions for a specific integration within a source application. | – | `200 OK` with `IntegrationMetadataDto[]`; `403/404/500` on errors. |
| `GET` | `/{metadataId}/instans-metadata` | Fetch the instance metadata content for a given integration metadata ID. | – | `200 OK` with `InstanceMetadataContentDto`; `404` when missing; `403` if unauthorized. |
| `POST` | `/` | Publish a new integration metadata version. | `IntegrationMetadataDto` (validated; must be a unique version per source application/integration). | `200 OK` on success; `409` if version exists; `422` on validation errors. |

All endpoints are secured; `UserAuthorizationService` checks the caller’s access to the referenced `sourceApplicationId` and returns `403 Forbidden` when claims do not permit access.

## Kafka Integration

- Consumes `integration-metadata-received` events (prefixed with org/context from `TopicNamePrefixParameters`) with 1 partition and 7-day retention; duplicates (same source application, integration ID, and version) are logged and skipped.
- Serves request/reply lookups on two topics:
  - `metadata` requests keyed by `metadata-id` return the persisted `IntegrationMetadata` entity (retention 10 minutes).
  - `instance-metadata` requests keyed by `metadata-id` return only `InstanceMetadataContentDto` (retention 0 to keep the topic transient).
- Topic provisioning and listener containers rely on the shared `no.novari:kafka` utilities; consumer group IDs default to `fint.application-id`.

## Scheduled Tasks

No scheduled jobs run in this service; it reacts to Kafka events and HTTP requests.

## Configuration

The application layers on the shared Spring profiles `flyt-kafka`, `flyt-logging`, `flyt-resource-server`, and `flyt-postgres`.

Key properties:

| Property | Description |
| --- | --- |
| `fint.application-id` | Defaults to `fint-flyt-discovery-service`; reused as Kafka application/group ID. |
| `novari.kafka.topic.domain-context` | Kafka domain context (default `flyt`); overrides combined with org ID in overlays. |
| `novari.kafka.topic.orgId` | Organization ID used when rendering Kafka topic names; set via environment (overlays and local profile). |
| `spring.kafka.bootstrap-servers` | Kafka bootstrap URL (see `application-local-staging.yaml` for localhost defaults). |
| `spring.datasource.*` | JDBC connection details for Postgres plus Hikari schema selection; secrets provided per environment. |
| `spring.security.oauth2.resourceserver.jwt.issuer-uri` | OAuth issuer for validating internal API calls. |
| `novari.flyt.resource-server.security.api.internal.authorized-org-id-role-pairs-json` | Org/role map for internal client authorization (rendered into overlays). |
| `spring.jpa.hibernate.ddl-auto` / `spring.flyway.*` | Schema management; Flyway migrations are applied on startup. |

Secrets referenced in overlays must supply database credentials, Kafka access, OAuth issuer/audience, and any internal client auth settings.

## Running Locally

Prerequisites:
- Java 21+
- Docker (for Postgres helper) and access to a Kafka broker
- Gradle (wrapper included)

Useful commands:

```shell
./start-postgres          # start Postgres on localhost:5437 with default creds
./gradlew clean build     # compile and run tests
SPRING_PROFILES_ACTIVE=local-staging ./gradlew bootRun  # run with local Kafka/Postgres defaults
./gradlew test            # run unit tests
```

Point `spring.kafka.bootstrap-servers` to your dev broker (default `localhost:9092` in `application-local-staging.yaml`). Override datasource settings if you use a different Postgres setup.

## Deployment

Kustomize layout:
- `kustomize/base/` holds the core `Application` resource for fint-flyt-discovery-service.
- `kustomize/overlays/<org>/<env>/` (generated per tenant/env) injects org IDs, Kafka ACLs, ingress paths, and auth role mappings.

Templates live in `kustomize/templates/`:
- `overlay.yaml.tpl` — envsubst template used by all overlays.

Regenerate overlays after changing templates or rendering rules:

```shell
./script/render-overlay.sh
```

The script rewrites each `kustomization.yaml` with namespace-specific paths, Kafka topics, readiness/metrics URLs, and authorized org/role pairs.

## Security

- Runs as an OAuth2 resource server validating JWTs from `spring.security.oauth2.resourceserver.jwt.issuer-uri`.
- Internal APIs are enabled via the shared `flyt-resource-server` profile and gated by `UserAuthorizationService`, which checks caller access to the requested `sourceApplicationId`.

## Observability & Operations

- Readiness/liveness: `/actuator/health`.
- Metrics: `/actuator/prometheus`.
- Structured logging provided by the shared FINT logging profile; Kafka consumers log skipped duplicates and ingestion errors.

## Development Tips

- Versions must be unique per `(sourceApplicationId, sourceApplicationIntegrationId)`; `IntegrationMetadataService.versionExists` enforces this before inserts.
- Adding fields to metadata structures requires updates to DTOs, entities, mapping services, and Flyway migrations.
- Kafka request/reply topics have tight retention (10m/0); keep payloads small and ensure downstream consumers handle missing responses gracefully.
- When exposing new internal endpoints, wire them through the internal API namespace and update authorization rules as needed.

## Contributing

1. Create a topic branch for your change.
2. Run `./gradlew test` (and any additional checks) before opening a PR.
3. If you adjust Kustomize templates or overlay rendering, rerun `./script/render-overlay.sh` and commit the regenerated manifests.
4. Add or update tests to cover new behaviour and edge cases.

FINT Flyt Discovery Service is maintained by the FINT Flyt team. Contact the team via the internal Slack channel or open an issue in this repository for questions or enhancements.
