package com.hedvig.app.util.apollo

import com.apollographql.apollo.response.CustomTypeAdapter
import com.apollographql.apollo.response.CustomTypeValue
import org.json.JSONObject
import javax.money.Monetary
import javax.money.MonetaryAmount

class MonetaryAmountAdapter : CustomTypeAdapter<MonetaryAmount> {
    override fun encode(value: MonetaryAmount): CustomTypeValue<*> {
        val jsonObject = JSONObject()
        jsonObject.put("amount", value.number.doubleValueExact())
        jsonObject.put("currency", value.currency.currencyCode)
        return CustomTypeValue.fromRawValue(jsonObject.toString())
    }

    override fun decode(value: CustomTypeValue<*>): MonetaryAmount {
        val jsonObject = JSONObject(value.value as String)

        val amount = jsonObject.getDouble("amount")
        val currency = jsonObject.getString("currency")

        return Monetary.getDefaultAmountFactory().setNumber(amount).setCurrency(currency).create()
    }

}
