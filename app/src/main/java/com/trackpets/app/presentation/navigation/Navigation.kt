package com.trackpets.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Router
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")

    object PetList : Screen("pets")
    object PetDetail : Screen("pets/{id}") { fun createRoute(id: Int) = "pets/$id" }
    object PetForm : Screen("pets/form?id={id}") { fun createRoute(id: Int? = null) = if (id != null) "pets/form?id=$id" else "pets/form" }

    object OwnerList : Screen("owners")
    object OwnerDetail : Screen("owners/{id}") { fun createRoute(id: Int) = "owners/$id" }
    object OwnerForm : Screen("owners/form?id={id}") { fun createRoute(id: Int? = null) = if (id != null) "owners/form?id=$id" else "owners/form" }

    object DeviceList : Screen("devices")
    object DeviceDetail : Screen("devices/{id}") { fun createRoute(id: Int) = "devices/$id" }
    object DeviceForm : Screen("devices/form?id={id}") { fun createRoute(id: Int? = null) = if (id != null) "devices/form?id=$id" else "devices/form" }

    object UserList : Screen("users")
    object UserDetail : Screen("users/{id}") { fun createRoute(id: Int) = "users/$id" }
    object UserForm : Screen("users/form?id={id}") { fun createRoute(id: Int? = null) = if (id != null) "users/form?id=$id" else "users/form" }

    object GeofenceList : Screen("geofences")
    object GeofenceDetail : Screen("geofences/{id}") { fun createRoute(id: Int) = "geofences/$id" }
    object GeofenceForm : Screen("geofences/form?id={id}&petId={petId}") { 
        fun createRoute(id: Int? = null, petId: Int? = null): String {
            return "geofences/form" + 
                if (id != null) "?id=$id" 
                else if (petId != null) "?petId=$petId" 
                else ""
        } 
    }

    object AlertList : Screen("alerts")
    object AlertDetail : Screen("alerts/{id}") { fun createRoute(id: Int) = "alerts/$id" }
    object AlertForm : Screen("alerts/form?id={id}&petId={petId}") { 
        fun createRoute(id: Int? = null, petId: Int? = null): String {
            return "alerts/form" + 
                if (id != null) "?id=$id" 
                else if (petId != null) "?petId=$petId" 
                else ""
        } 
    }

    object MapScreen : Screen("map")

    object More : Screen("more")
}

enum class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    Dashboard(Screen.Dashboard.route, "Inicio", Icons.Filled.Home),
    Pets(Screen.PetList.route, "Mascotas", Icons.Filled.Pets),
    Owners(Screen.OwnerList.route, "Dueños", Icons.Filled.People),
    Map(Screen.MapScreen.route, "Mapa", Icons.Filled.Map),
    Devices(Screen.DeviceList.route, "Dispositivos", Icons.Filled.Router),
    More("more", "Más", Icons.Filled.Menu)
}
