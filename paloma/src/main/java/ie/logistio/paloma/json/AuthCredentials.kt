package ie.logistio.paloma.json

import org.threeten.bp.Instant


/**
 * Access token information that grants authenticated access to a rest API.
 *
 * @param refreshToken: NULL if authenticating using only a Client Id+Secret.
 * @param refreshTokenExpiresOn: NULL if authenticating using only a Client Id+Secret.
 */
data class AuthCredentials(
        val tokenType: String,
        val accessToken: String,
        val accessTokenExpiresOn: Instant,
        val refreshToken: String? = null,
        val refreshTokenExpiresOn: Instant? = null
)