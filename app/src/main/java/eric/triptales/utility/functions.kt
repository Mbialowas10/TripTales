package eric.triptales.utility

/**
 * Extracts the country name from a given address string.
 *
 * Assumes the last part of the address (separated by commas) represents the country.
 * If the address is empty or cannot be parsed, returns "Unknown Country".
 *
 * @param address The full address string to parse (e.g., "123 Main St, City, Country").
 * @return The extracted country name or "Unknown Country" if parsing fails.
 */
fun parseCountry(address: String): String {
    val parts = address.split(",").map { it.trim() } // Split the address by commas and trim whitespace
    return parts.lastOrNull() ?: "Unknown Country" // Return the last part or "Unknown Country" if empty
}
