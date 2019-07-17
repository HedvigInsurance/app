package com.hedvig.app.react.data

import com.hedvig.android.owldroid.graphql.RedeemReferralCodeMutation


data class RedeemedCode(
    val __typename: String,
    val cost: Cost,
    val campaigns: List<Campaign>
) {
    companion object {
        fun of(redeemCode: RedeemReferralCodeMutation.RedeemCode) = RedeemedCode(
            redeemCode.__typename,
            Cost.of(redeemCode.cost),
            redeemCode.campaigns.map { Campaign.of(it) }
        )
    }
}


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

data class Campaign(
    val __typename: String,
    val code: String,
    val incentive: Incentive
) {
    companion object {
        fun of(campaign: RedeemReferralCodeMutation.Campaign): Campaign {
            val incentive: Incentive = when (val incentive = campaign.incentive) {
                is RedeemReferralCodeMutation.AsFreeMonths ->
                    Incentive.FreeMonths.of(incentive)
                is RedeemReferralCodeMutation.AsMonthlyCostDeduction ->
                    Incentive.MonthlyCostDeduction.of(incentive)
                else -> Incentive.UnknownIncentive
            }


            return Campaign(
                campaign.__typename,
                campaign.code,
                incentive
            )
        }
    }
}

sealed class Incentive {
    data class FreeMonths(
        val __typename: String,
        val quantity: Int): Incentive() {
        companion object {
            fun of(freeMonths: RedeemReferralCodeMutation.AsFreeMonths) = FreeMonths(
                freeMonths.__typename,
                freeMonths.quantity ?: 0
            )
        }
    }

    data class MonthlyCostDeduction(
        val __typename: String,
        val amount: Amount?): Incentive() {
        companion object {
            fun of(monthlyCostDeduction: RedeemReferralCodeMutation.AsMonthlyCostDeduction) = MonthlyCostDeduction(
                monthlyCostDeduction.__typename,
                monthlyCostDeduction.amount?.let { Amount.of(it) }
            )
        }
    }

    object UnknownIncentive: Incentive()
}


data class Amount(
    val __typename: String,
    val amount: String,
    val currency: String) {
    companion object {
        fun of(amount: RedeemReferralCodeMutation.Amount) = Amount(
            amount.__typename,
            amount.amount,
            amount.currency
        )
    }
}
