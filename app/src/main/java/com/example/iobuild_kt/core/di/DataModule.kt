package com.example.iobuild_kt.core.di

import com.example.iobuild_kt.core.data.TokenManager
import com.example.iobuild_kt.core.data.local.IoBuildDatabase
import com.example.iobuild_kt.core.i18n.LanguageManager
import org.koin.dsl.module

val dataModule = module {
    single { TokenManager(get()) }
    single { LanguageManager(get()) }
    single { IoBuildDatabase.getInstance(get()) }
    single { get<IoBuildDatabase>().projectDao() }
    single { get<IoBuildDatabase>().clientDao() }
    single { get<IoBuildDatabase>().deviceDao() }
    single { get<IoBuildDatabase>().metadataDao() }
}
