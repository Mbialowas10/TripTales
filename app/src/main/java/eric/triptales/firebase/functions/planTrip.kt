package eric.triptales.firebase.functions

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import eric.triptales.firebase.entity.PlannedTrip

suspend fun savePlannedTrip(trip: PlannedTrip) {
    val db = FirebaseFirestore.getInstance()
    db.collection("planned_trips")
        .document(trip.tripId)
        .set(trip)
        .addOnSuccessListener {
            Log.d("Firestore", "Trip successfully saved!")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error saving trip", e)
        }
}

fun fetchPlannedTrips(userId: String, onSuccess: (List<PlannedTrip>) -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("planned_trips")
        .whereEqualTo("userId", userId)
        .get()
        .addOnSuccessListener { snapshot ->
            val trips = snapshot.toObjects(PlannedTrip::class.java)
            onSuccess(trips)
        }
        .addOnFailureListener { e ->
            onFailure(e)
        }
}

