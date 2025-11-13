package com.controlimage.api.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record KeycloakJwtAuthenticationConverter(String clientId) {

    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> realmRoles = extractRoles(jwt);
        List<String> clientRoles = extractRoles(jwt, clientId);

        return Stream.concat(realmRoles.stream(), clientRoles.stream()).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private List<String> extractRoles(Jwt jwt) {
        Map<String, Object> claim = jwt.getClaimAsMap("realm_access");
        if (claim != null && claim.get("roles") instanceof Collection<?> roles) {
            return roles.stream().filter(String.class::isInstance).map(String.class::cast).toList();
        }
        return Collections.emptyList();
    }

    private List<String> extractRoles(Jwt jwt, String clientId) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null && resourceAccess.get(clientId) instanceof Map<?, ?> clientMap) {
            Object rolesObj = clientMap.get("roles");
            if (rolesObj instanceof Collection<?> roles) {
                return roles.stream().filter(String.class::isInstance).map(String.class::cast).toList();
            }
        }
        return Collections.emptyList();
    }
}
