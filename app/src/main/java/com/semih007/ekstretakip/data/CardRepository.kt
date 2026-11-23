package com.semih007.ekstretakip.data

import android.content.Context
import com.semih007.ekstretakip.model.Card
import org.json.JSONArray
import org.json.JSONObject

class CardRepository(context: Context) {
    private val prefs = context.getSharedPreferences("cards_prefs", Context.MODE_PRIVATE)

    fun load(): List<Card> = try {
        val array = JSONArray(prefs.getString("cards_json", "[]"))
        (0 until array.length()).mapNotNull { index ->
            val item = array.getJSONObject(index)
            val day = item.getInt("day")
            require(day in 1..31)
            val due = item.optString("paymentOffset").toIntOrNull()
            if (due == null || due !in 1..60) return@mapNotNull null
            val hasNewFlags = item.has("wk") || item.has("hol") || item.has("half")
            val legacy = !hasNewFlags && item.optBoolean("adjust", item.optBoolean("adjustToBusinessDay"))
            Card(
                id = item.getString("id"),
                name = item.getString("name"),
                statementDay = day,
                dueOffsetDays = due,
                shiftOnWeekend = item.optBoolean("wk", legacy),
                shiftOnHoliday = item.optBoolean("hol", legacy),
                shiftOnHalfDay = item.optBoolean("half")
            )
        }
    } catch (_: Exception) {
        emptyList()
    }

    fun save(cards: List<Card>) {
        val array = JSONArray()
        for (card in cards) {
            array.put(
                JSONObject()
                    .put("id", card.id)
                    .put("name", card.name)
                    .put("day", card.statementDay)
                    .put("paymentOffset", card.dueOffsetDays)
                    .put("wk", card.shiftOnWeekend)
                    .put("hol", card.shiftOnHoliday)
                    .put("half", card.shiftOnHalfDay)
            )
        }
        prefs.edit().putString("cards_json", array.toString()).apply()
    }
}
