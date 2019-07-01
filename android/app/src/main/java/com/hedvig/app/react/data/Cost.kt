package com.hedvig.app.react.data

import com.hedvig.android.owldroid.graphql.RedeemReferralCodeMutation

data class Cost(
    val __typename: String,
    val monthlyDiscount: MonthlyDiscount,
    val monthlyNet: MonthlyNet,
    val monthlyGross: MonthlyGross
) {
    companion object {
        fun of(cost: RedeemReferralCodeMutation.Cost): Cost = Cost(
            cost.__typename,
            MonthlyDiscount(
                cost.monthlyDiscount.__typename,
                cost.monthlyDiscount.amount
            ),
            MonthlyNet(
                cost.monthlyNet.__typename,
                cost.monthlyNet.amount
            ),
            MonthlyGross(
                cost.monthlyGross.__typename,
                cost.monthlyGross.amount
            )
        )
    }
}


