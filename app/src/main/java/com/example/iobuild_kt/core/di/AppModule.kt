package com.example.iobuild_kt.core.di

import com.example.iobuild_kt.auth.data.api.AuthApiService
import com.example.iobuild_kt.auth.data.repository.AuthRepositoryImpl
import com.example.iobuild_kt.auth.domain.repository.AuthRepository
import com.example.iobuild_kt.auth.presentation.LoginViewModel
import com.example.iobuild_kt.auth.presentation.RegisterViewModel
import com.example.iobuild_kt.dashboard.data.api.AnalyticsApiService
import com.example.iobuild_kt.dashboard.data.repository.AnalyticsRepositoryImpl
import com.example.iobuild_kt.dashboard.domain.repository.AnalyticsRepository
import com.example.iobuild_kt.dashboard.domain.usecase.GetBuilderDashboardUseCase
import com.example.iobuild_kt.dashboard.presentation.DashboardViewModel
import com.example.iobuild_kt.clients.data.api.ClientApiService
import com.example.iobuild_kt.clients.data.repository.ClientRepositoryImpl
import com.example.iobuild_kt.clients.domain.repository.ClientRepository
import com.example.iobuild_kt.clients.domain.usecase.CreateClientUseCase
import com.example.iobuild_kt.clients.domain.usecase.DeleteClientUseCase
import com.example.iobuild_kt.clients.domain.usecase.GetClientByIdUseCase
import com.example.iobuild_kt.clients.domain.usecase.GetClientsUseCase
import com.example.iobuild_kt.clients.domain.usecase.UpdateClientUseCase
import com.example.iobuild_kt.clients.presentation.client_list.ClientListViewModel
import com.example.iobuild_kt.projects.data.api.ProjectApiService
import com.example.iobuild_kt.projects.data.repository.ProjectRepositoryImpl
import com.example.iobuild_kt.projects.domain.repository.ProjectRepository
import com.example.iobuild_kt.projects.domain.usecase.CreateProjectUseCase
import com.example.iobuild_kt.projects.domain.usecase.DeleteProjectUseCase
import com.example.iobuild_kt.projects.domain.usecase.GetProjectByIdUseCase
import com.example.iobuild_kt.projects.domain.usecase.GetProjectsUseCase
import com.example.iobuild_kt.projects.domain.usecase.UpdateProjectUseCase
import com.example.iobuild_kt.devices.data.api.DeviceApiService
import com.example.iobuild_kt.devices.data.repository.DeviceRepositoryImpl
import com.example.iobuild_kt.devices.domain.repository.DeviceRepository
import com.example.iobuild_kt.devices.domain.usecase.CreateDeviceUseCase
import com.example.iobuild_kt.devices.domain.usecase.DeleteDeviceUseCase
import com.example.iobuild_kt.devices.domain.usecase.GetDeviceByIdUseCase
import com.example.iobuild_kt.devices.domain.usecase.GetDevicesUseCase
import com.example.iobuild_kt.devices.domain.usecase.UpdateDeviceUseCase
import com.example.iobuild_kt.devices.presentation.device_list.DeviceListViewModel
import com.example.iobuild_kt.projects.presentation.project_detail.ProjectDetailViewModel
import com.example.iobuild_kt.projects.presentation.project_form.ProjectFormViewModel
import com.example.iobuild_kt.projects.presentation.project_list.ProjectListViewModel
import com.example.iobuild_kt.subscription.data.api.SubscriptionApiService
import com.example.iobuild_kt.subscription.data.repository.SubscriptionRepositoryImpl
import com.example.iobuild_kt.subscription.domain.repository.SubscriptionRepository
import com.example.iobuild_kt.subscription.domain.usecase.ConfirmPaymentUseCase
import com.example.iobuild_kt.subscription.domain.usecase.CreateCheckoutSessionUseCase
import com.example.iobuild_kt.subscription.domain.usecase.GetCurrentSubscriptionUseCase
import com.example.iobuild_kt.subscription.domain.usecase.GetPlansUseCase
import com.example.iobuild_kt.subscription.presentation.SubscriptionAccessState
import com.example.iobuild_kt.subscription.presentation.SubscriptionViewModel
import com.example.iobuild_kt.profile.data.api.ProfileApiService
import com.example.iobuild_kt.profile.data.repository.ProfileRepositoryImpl
import com.example.iobuild_kt.profile.domain.repository.ProfileRepository
import com.example.iobuild_kt.profile.domain.usecase.GetProfileUseCase
import com.example.iobuild_kt.profile.domain.usecase.UpdateProfileUseCase
import com.example.iobuild_kt.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit

val authModule = module {
    single<AuthApiService> { get<Retrofit>().create(AuthApiService::class.java) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
}

val analyticsModule = module {
    single<AnalyticsApiService> { get<Retrofit>().create(AnalyticsApiService::class.java) }
    single<AnalyticsRepository> { AnalyticsRepositoryImpl(get()) }
    factory { GetBuilderDashboardUseCase(get()) }
    viewModelOf(::DashboardViewModel)
}

val projectsModule = module {
    single<ProjectApiService> { get<Retrofit>().create(ProjectApiService::class.java) }
    single<ProjectRepository> { ProjectRepositoryImpl(get(), get(), get()) }
    factory { GetProjectsUseCase(get()) }
    factory { GetProjectByIdUseCase(get()) }
    factory { CreateProjectUseCase(get()) }
    factory { UpdateProjectUseCase(get()) }
    factory { DeleteProjectUseCase(get()) }
    viewModelOf(::ProjectListViewModel)
    viewModelOf(::ProjectDetailViewModel)
    viewModelOf(::ProjectFormViewModel)
}

val devicesModule = module {
    single<DeviceApiService> { get<Retrofit>().create(DeviceApiService::class.java) }
    single<DeviceRepository> { DeviceRepositoryImpl(get(), get(), get()) }
    factory { GetDevicesUseCase(get()) }; factory { GetDeviceByIdUseCase(get()) }
    factory { CreateDeviceUseCase(get()) }; factory { UpdateDeviceUseCase(get()) }; factory { DeleteDeviceUseCase(get()) }
    viewModelOf(::DeviceListViewModel)
}

val clientsModule = module {
    single<ClientApiService> { get<Retrofit>().create(ClientApiService::class.java) }
    single<ClientRepository> { ClientRepositoryImpl(get(), get(), get()) }
    factory { GetClientsUseCase(get()) }; factory { GetClientByIdUseCase(get()) }
    factory { CreateClientUseCase(get()) }; factory { UpdateClientUseCase(get()) }; factory { DeleteClientUseCase(get()) }
    viewModelOf(::ClientListViewModel)
}

val subscriptionModule = module {
    single<SubscriptionApiService> { get<Retrofit>().create(SubscriptionApiService::class.java) }
    single<SubscriptionRepository> { SubscriptionRepositoryImpl(get()) }
    factory { GetPlansUseCase(get()) }; factory { GetCurrentSubscriptionUseCase(get()) }
    factory { CreateCheckoutSessionUseCase(get()) }; factory { ConfirmPaymentUseCase(get()) }
    single { SubscriptionAccessState(get()) }
    viewModelOf(::SubscriptionViewModel)
}

val profileModule = module {
    single<ProfileApiService> { get<Retrofit>().create(ProfileApiService::class.java) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    factory { GetProfileUseCase(get()) }; factory { UpdateProfileUseCase(get()) }
    viewModelOf(::ProfileViewModel)
}

val appModule = listOf(networkModule, dataModule, authModule, analyticsModule, projectsModule, devicesModule, profileModule, clientsModule, subscriptionModule)
