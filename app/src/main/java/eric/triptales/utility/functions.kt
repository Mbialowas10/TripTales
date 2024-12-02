package eric.triptales.utility

fun parseCountry(address: String): String {
    val parts = address.split(",").map { it.trim() }
    return parts.lastOrNull() ?: "Unknown Country" // Assume the last part of the address is the country
}
