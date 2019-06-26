package com.hedvig.app.util.extensions

import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.graphql.RedeemReferralCodeMutation


fun ProfileQuery.Campaign.monthlyCostDeductionIncentive() =
    this.incentive as? ProfileQuery.AsMonthlyCostDeduction

fun RedeemReferralCodeMutation.Campaign.monthlyCostDeductionIncentive() =
    this.incentive as? RedeemReferralCodeMutation.AsMonthlyCostDeduction
