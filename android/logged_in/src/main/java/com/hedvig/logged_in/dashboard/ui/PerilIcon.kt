package com.hedvig.logged_in.dashboard.ui

import android.support.annotation.DrawableRes
import com.hedvig.logged_in.R
import com.hedvig.app.R as appR

object PerilIcon {
    @DrawableRes
    fun from(id: String) = when (id) {
        "ME.LEGAL" -> appR.drawable.ic_legal
        "ME.ASSAULT" -> appR.drawable.ic_assault
        "ME.TRAVEL.SICK" -> appR.drawable.ic_illness
        "ME.TRAVEL.LUGGAGE.DELAY" -> appR.drawable.ic_luggage_delay
        "HOUSE.BRF.FIRE",
        "HOUSE.RENT.FIRE",
        "HOUSE.SUBLET.BRF.FIRE",
        "HOUSE.SUBLET.RENT.FIRE" -> appR.drawable.ic_fire_red
        "HOUSE.BRF.APPLIANCES",
        "HOUSE.RENT.APPLIANCES",
        "HOUSE.SUBLET.BRF.APPLIANCES" -> appR.drawable.ic_appliances
        "HOUSE.BRF.WEATHER",
        "HOUSE.RENT.WEATHER",
        "HOUSE.SUBLET.BRF.WEATHER",
        "HOUSE.SUBLET.RENT.WEATHER" -> appR.drawable.ic_weather_red
        "HOUSE.BRF.WATER",
        "HOUSE.RENT.WATER",
        "HOUSE.SUBLET.BRF.WATER",
        "HOUSE.SUBLET.RENT.WATER" -> appR.drawable.ic_water_red
        "HOUSE.BREAK-IN" -> appR.drawable.ic_break_in
        "HOUSE.DAMAGE" -> appR.drawable.ic_vandalism_red
        "STUFF.CARELESS" -> appR.drawable.ic_accidental_damage
        "STUFF.THEFT" -> appR.drawable.ic_theft
        "STUFF.DAMAGE" -> appR.drawable.ic_vandalism_green
        "STUFF.BRF.FIRE",
        "STUFF.RENT.FIRE",
        "STUFF.SUBLET.BRF.FIRE",
        "STUFF.SUBLET.RENT.FIRE" -> appR.drawable.ic_fire_green
        "STUFF.BRF.WATER",
        "STUFF.RENT.WATER",
        "STUFF.SUBLET.BRF.WATER",
        "STUFF.SUBLET.RENT.WATER" -> appR.drawable.ic_water_green
        "STUFF.BRF.WEATHER" -> appR.drawable.ic_weather_green
        else -> appR.drawable.ic_vandalism_green
    }
}
