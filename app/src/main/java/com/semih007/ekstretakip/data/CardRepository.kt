package com.semih007.ekstretakip.data

import android.content.Context
import com.semih007.ekstretakip.model.Card
import org.json.JSONArray
import org.json.JSONObject

class CardRepository(context: Context) {
    private val prefs = context.getSharedPreferences("cards_prefs", Context.MODE_PRIVATE)

    fun load(): List<Card> = try {
        val array = JSONArray(prefs.getString("cards_json", "[]"))
        List(array.length()) { index ->
            val item = array.getJSONObject(index)
            val day = item.getInt("day")
            require(day in 1..31)
            Card(
                id = item.getString("id"),
                name = item.getString("name"),
                statementDay = day,
                paymentOffset = item.optInt("paymentOffset").takeIf { it > 0 },
                adjustToBusinessDay = item.optBoolean("adjustToBusinessDay")
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
                    .put("paymentOffset", card.paymentOffset)
                    .put("adjustToBusinessDay", card.adjustToBusinessDay)
            )
        }
        prefs.edit().putString("cards_json", array.toString()).apply()
    }
}
