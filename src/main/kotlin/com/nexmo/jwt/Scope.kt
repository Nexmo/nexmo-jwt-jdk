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

enum class Scope(val path: String, val methods: Set<Method>) {
    ALL_USER_ACCESS("/v1/users/**", Method.values().toSet()),
    LIST_USERS("/v1/users", setOf(Method.GET)),
    CREATE_USER("/v1/users", setOf(Method.POST)),
    RETRIEVE_USER("/v1/users/**", setOf(Method.GET)),
    UPDATE_USER("/v1/users/*", setOf(Method.PUT)),
    DELETE_USER("/v1/users/*", setOf(Method.DELETE));

    enum class Method {
        GET, POST, PUT, PATCH, DELETE;
    }
}
