authorizedUrl.access((authentication, context) -> 
    authentication.get().getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt
);