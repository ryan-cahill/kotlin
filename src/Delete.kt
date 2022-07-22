@file:OptIn(KtorExperimentalLocationsAPI::class)

package io.ktor.samples.kweet

import io.ktor.http.*
import io.ktor.samples.kweet.dao.*
import io.ktor.server.application.*
import io.ktor.server.locations.*
import io.ktor.server.locations.post
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

/**
 * Registers a route for deleting deleting kweets.
 */
fun Route.delete(dao: DAOFacade, hashFunction: (String) -> String) {
    // Uses the location feature to register a post route for '/kweet/{id}/delete'.
    post<KweetDelete> {
        // Tries to get (null on failure) the user associated to the current KweetSession
        val user = call.sessions.get<KweetSession>()?.let { dao.user(it.userId) }

        // Receives the Parameters date and code, if any of those fails to be obtained,
        // it redirects to the tweet page without deleting the kweet.
        val post = call.receive<Parameters>()
        val date = post["date"]?.toLongOrNull() ?: return@post call.redirect(ViewKweet(it.id))
        val code = post["code"] ?: return@post call.redirect(ViewKweet(it.id))
        val kweet = dao.getKweet(it.id)

        // Verifies that the kweet user matches the session user and that the code and the date matches, to prevent CSFR.
        if (user == null || kweet.userId != user.userId || !call.verifyCode(date, user, code, hashFunction)) {
            call.redirect(ViewKweet(it.id))
        } else {
            dao.deleteKweet(it.id)
            call.redirect(Index())
        }
    }
}
