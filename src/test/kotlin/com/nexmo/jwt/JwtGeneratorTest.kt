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

import io.jsonwebtoken.Jwts
import org.junit.Test
import java.io.File
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val PRIVATE_KEY_PATH = "src/test/resources/private.key"

class JwtGeneratorTest {
    val privateKeyContents = File(PRIVATE_KEY_PATH).readText()

    @Test
    fun `when a jwt has all custom properties those properties are on the generated token`() {
        val expectedToken =
            "eyJ0eXBlIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJhcHBsaWNhdGlvbl9pZCI6ImFwcGxpY2F0aW9uLWlkIiwic3ViIjoic3ViamVjdCIsImV4cCI6NjM2NTA4ODAwLCJuYmYiOjYzNjUwODgwMCwiaWF0Ijo2MzY1MDg4MDAsImp0aSI6ImlkIiwiZm9vIjoiYmFyIn0.TTQqqxtcHeaT9UMU7JIQJILs54U2i-yK-suRJM4xyBxYYTTKCy9emGWIOsiROPOxVahOPzjvGOvNQRilceBaQ2JoYPnrCNn8o0qfctxxRhEOQNHIxOvYfYXjncnLTmY2jiG7q4JVN3_GNr-uIoupKnbXOgcKm-rhDBOu4vlMShVOaThb9HqMMyy0lbfIR6XR4IXiFmOSJ3rQIIqqXzAWWwZmj-_u5-5U2kDc6XR4UWW711zrrR3puvAhbIdzy8gVmGFDXBro8137dv6RKOw4l8KskYbe5o5oF2fQsf6Fmjp5R5ZVSa9Kt9_D-XxvADGj8fwEEePsZ0-Xey9hFN5V-A"
        val jwt = Jwt.builder()
            .applicationId("application-id")
            .privateKeyContents(privateKeyContents)
            .subject("subject")
            .expiresAt(testDate())
            .notBefore(testDate())
            .issuedAt(testDate())
            .id("id")
            .addClaim("foo", "bar")
            .build()

        val token = jwt.generate()
        assertEquals(expectedToken, token)
    }

    @Test
    fun `when a jwt only has an application id and secret the other required properties are on the generated token`() {
        val jwt = Jwt.builder()
            .applicationId("application-id")
            .privateKeyContents(privateKeyContents)
            .build()

        val token = jwt.generate()
        val rsaKey = KeyConverter().privateKey(privateKeyContents)

        val claims = Jwts.parser().setSigningKey(rsaKey).parseClaimsJws(token)
        assertEquals("JWT", claims.header["type"])
        assertEquals("RS256", claims.header["alg"])
        assertEquals("application-id", claims.body["application_id"])
        assertTrue(claims.body.containsKey("iat"))
        assertTrue(claims.body.containsKey("jti"))
        assertTrue(Jwts.parser().isSigned(token))
    }

    private fun testDate() = LocalDateTime.of(1990, 3, 4, 0, 0, 0)
}