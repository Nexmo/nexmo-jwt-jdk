/*
 * Copyright (c) 2011-2019 Nexmo Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.nexmo.jwt

import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

/**
 * Produce a Nexmo-compliant JWS from a JWT.
 */
class JwtGenerator(private val keyConverter: KeyConverter = KeyConverter()) {
    /**
     * Generate a token from a Jwt.
     */
    fun generate(jwt: Jwt): String {
        val privateKey = keyConverter.privateKey(jwt.privateKeyContents)

        val jwtBuilder = Jwts.builder()
            .setHeaderParam("type", "JWT")
            .claim("application_id", jwt.applicationId)
            .addClaims(jwt.claims)

        // Add required claims if they don't already exist
        if (!jwt.claims.containsKey("iat")) jwtBuilder.claim("iat", Instant.now().epochSecond)
        if (!jwt.claims.containsKey("jti")) jwtBuilder.claim("jti", UUID.randomUUID().toString())

        // Modify user date claims as the library expects a Date as a Long and isn't setup to support LocalDateTime
        convertLocalDateTimeClaimToLong("iat", jwt.claims, jwtBuilder)
        convertLocalDateTimeClaimToLong("exp", jwt.claims, jwtBuilder)
        convertLocalDateTimeClaimToLong("nbf", jwt.claims, jwtBuilder)

        return jwtBuilder.signWith(privateKey, SignatureAlgorithm.RS256).compact()
    }

    private fun convertLocalDateTimeClaimToLong(key: String, claims: Map<String, Any>, builder: JwtBuilder) {
        if (claims.containsKey(key) && claims[key] is ZonedDateTime) {
            builder.claim(key, (claims[key] as ZonedDateTime).toEpochSecond())
        }
    }
}
