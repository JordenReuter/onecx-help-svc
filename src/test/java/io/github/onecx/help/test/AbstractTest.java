package io.github.onecx.help.test;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static io.restassured.RestAssured.config;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

import java.security.PrivateKey;
import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

import org.eclipse.microprofile.jwt.Claims;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.quarkus.test.junit.QuarkusTestProfile;
import io.restassured.config.RestAssuredConfig;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.util.KeyUtils;

@SuppressWarnings("java:S2187")
public class AbstractTest {

    protected static final String APM_HEADER_PARAM = "apm-principal-token";
    protected static final String CLAIMS_ORG_ID = "orgId";

    static {
        config = RestAssuredConfig.config().objectMapperConfig(
                objectMapperConfig().jackson2ObjectMapperFactory(
                        (cls, charset) -> {
                            var objectMapper = new ObjectMapper();
                            objectMapper.registerModule(new JavaTimeModule());
                            objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
                            return objectMapper;
                        }));
    }

    protected static String createToken(String organizationId) {
        try {
            String userName = "test-user";
            JsonObjectBuilder claims = Json.createObjectBuilder();
            claims.add(Claims.preferred_username.name(), userName);
            claims.add(Claims.sub.name(), userName);
            claims.add(CLAIMS_ORG_ID, organizationId);
            PrivateKey privateKey = KeyUtils.generateKeyPair(2048).getPrivate();
            return Jwt.claims(claims.build()).sign(privateKey);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static class TenantTestProfile implements QuarkusTestProfile {

        @Override
        public String getConfigProfile() {
            return "test";
        }

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "tkit.rs.context.tenant-id.enabled", "true",
                    "tkit.rs.context.tenant-id.mock.enabled", "true",
                    "tkit.rs.context.tenant-id.mock.default-tenant", "test",
                    "tkit.rs.context.tenant-id.mock.claim-org-id", CLAIMS_ORG_ID,
                    "tkit.rs.context.tenant-id.mock.token-header-param", APM_HEADER_PARAM,
                    "tkit.rs.context.tenant-id.mock.data.org1", "tenant-100",
                    "tkit.rs.context.tenant-id.mock.data.org2", "tenant-200");
        }
    }
}
