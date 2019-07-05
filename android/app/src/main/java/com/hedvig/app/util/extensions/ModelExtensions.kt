package com.hedvig.app.util.extensions

import com.hedvig.android.owldroid.graphql.ChatMessageSubscription
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import com.hedvig.android.owldroid.graphql.ProfileQuery

fun ProfileQuery.Campaign.monthlyCostDeductionIncentive() =
    this.incentive as? ProfileQuery.AsMonthlyCostDeduction

fun ChatMessageSubscription.Message.mapToMessage(): ChatMessagesQuery.Message {
    val body = ChatMessagesQuery.AsMessageBody
        .builder()
        .__typename(this.body!!.__typename)
        .type(this.body!!.type)
        .text(this.body!!.text)
        .build()

    val header = ChatMessagesQuery.Header
        .builder()
        .__typename(this.header.__typename)
        .fromMyself(this.header.isFromMyself)
        .build()

    return ChatMessagesQuery.Message
        .builder()
        .globalId(this.globalId)
        .id(this.id)
        .header(header)
        .body(body)
        .__typename(this.__typename)
        .build()
}

