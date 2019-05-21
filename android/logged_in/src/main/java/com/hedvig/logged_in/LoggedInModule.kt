package com.hedvig.logged_in

import com.hedvig.logged_in.claims.data.ClaimsRepository
import com.hedvig.logged_in.claims.service.ClaimsTracker
import com.hedvig.logged_in.claims.ui.ClaimsViewModel
import com.hedvig.logged_in.dashboard.data.DashboardRepository
import com.hedvig.logged_in.dashboard.service.DashboardTracker
import com.hedvig.logged_in.dashboard.ui.DashboardViewModel
import com.hedvig.logged_in.loggedin.BaseTabViewModel
import com.hedvig.logged_in.profile.data.ProfileRepository
import com.hedvig.logged_in.profile.service.ProfileTracker
import com.hedvig.logged_in.profile.ui.ProfileViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        viewModelModule,
        repositoriesModule,
        trackerModule
    )
}


val viewModelModule = module {
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
    viewModel { ClaimsViewModel(get(), get()) }
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { BaseTabViewModel(get()) }
}

val repositoriesModule = module {
    single { ClaimsRepository(get()) }
    single { DashboardRepository(get()) }
    single { ProfileRepository(get()) }
}

val trackerModule = module {
    single { ClaimsTracker(get()) }
    single { DashboardTracker(get()) }
    single { ProfileTracker(get()) }
}
