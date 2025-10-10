package nc.maxime.expense_manager.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import nc.maxime.expense_manager.user.Role;
import nc.maxime.expense_manager.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

        private static final String SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

        private JwtService jwtService;
        private User user;
        private SecretKey secretKey;

        @BeforeEach
        void setUp() {
                jwtService = new JwtService(new JwtProperties(SECRET, 3600));
                secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
                user = User.builder()
                                .email("john.doe@example.com")
                                .password("secret")
                                .role(Role.USER)
                                .build();
        }

        @Test
        void generateTokenShouldContainExpectedClaims() {
                String token = jwtService.generateToken(user);

                Jws<Claims> parsedToken = Jwts.parserBuilder()
                                .setSigningKey(secretKey)
                                .build()
                                .parseClaimsJws(token);

                assertEquals(user.getUsername(), parsedToken.getBody().getSubject());
                assertEquals(user.getRole().name(), parsedToken.getBody().get("role"));
                assertEquals(user.getEmail(), parsedToken.getBody().get("email"));
                assertTrue(parsedToken.getBody().getExpiration().after(parsedToken.getBody().getIssuedAt()));
        }

        @Test
        void extractUsernameShouldReturnSubject() {
                String token = jwtService.generateToken(user);

                assertEquals(user.getUsername(), jwtService.extractUsername(token));
        }

        @Test
        void isTokenValidShouldReturnTrueForValidToken() {
                String token = jwtService.generateToken(user);

                assertTrue(jwtService.isTokenValid(token, user));
        }

        @Test
        void isTokenValidShouldThrowForExpiredToken() {
                Instant now = Instant.now();
                Instant issuedAt = now.minusSeconds(7200);
                Instant expiredAt = now.minusSeconds(3600);
                String token = Jwts.builder()
                                .setSubject(user.getUsername())
                                .addClaims(Map.of(
                                                "role", user.getRole().name(),
                                                "email", user.getEmail()))
                                .setIssuedAt(Date.from(issuedAt))
                                .setExpiration(Date.from(expiredAt))
                                .signWith(secretKey, SignatureAlgorithm.HS256)
                                .compact();

                assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, user));
        }

        @Test
        void isTokenValidShouldReturnFalseWhenUsernameDoesNotMatch() {
                String token = jwtService.generateToken(user);
                User anotherUser = User.builder()
                                .email("jane.doe@example.com")
                                .password("secret")
                                .role(Role.USER)
                                .build();

                assertFalse(jwtService.isTokenValid(token, anotherUser));
        }
}
