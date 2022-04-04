package com.paguelofacil.posfacil.data.database.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.data.database.dao.ContactsLocalDao
import com.paguelofacil.posfacil.data.network.response.Contact
import com.paguelofacil.posfacil.tools.TypeConverters


/**
 * Local repository responsible defining tables and DAO for crud operations on local database with ROOM
 *
 * @constructor Create empty Local repository
 */
const val databaseName = "Paguelofacil_db"

/*
@Database(
    version = 33,
    entities = [
        FundRequestEntity::class, BankAccountEntity::class, ServiceEntity::class, CardEntity::class,
        WalletBalanceEntity::class, QuickActionEntity::class, PendingActivityEntity::class, CompletedActivityEntity::class,
        ShopEntity::class, RecentShopEntity::class, Contact::class, RecentPaymentContact::class, RecentRequestContact::class
    ], exportSchema = false)

*/

@Database(
    version = 1,
    entities = [Contact::class
    ], exportSchema = false)

/*
@androidx.room.TypeConverters(
    TypeConverters.AnyConverter::class, TypeConverters.ShopEntityConverter::class,
    TypeConverters.ProcessDataItemConverter::class, TypeConverters.BankAccountConverter::class,
    TypeConverters.BankEntityConverter::class, TypeConverters.RegCardConverter::class,
    TypeConverters.ContactSearchEntityConverter::class, TypeConverters.AnonymousInfoConverter::class,
    TypeConverters.IdMerchantTypeConverter::class, TypeConverters.ServiceResponseConverter::class,
    TypeConverters.ContactConverter::class, TypeConverters.ServiceConverter::class
)

 */
@androidx.room.TypeConverters(
      TypeConverters.ContactConverter::class
)


abstract class LocalDataSource : RoomDatabase() {

  //  abstract fun walletBalanceDao(): WalletBalanceLocalDao
   // abstract fun quickActionsLocalDao(): QuickActionsLocalDao
    //abstract fun recentPendingActivitiesLocalDao(): RecentPendingActivitiesLocalDao
    //abstract fun recentCompletedActivitiesLocalDao(): RecentCompletedActivitiesLocalDao
    //abstract fun recentPaymentContactsLocalDao(): RecentPaymentContactsLocalDao
    //abstract fun recentShopsLocalDao(): RecentShopsLocalDao
    //abstract fun recentRequestContactsLocalDao(): RecentRequestContactsLocalDao
    abstract fun contactsLocalDao(): ContactsLocalDao
    //abstract fun shopsLocalDao(): ShopsLocalDao
    //abstract fun servicesLocalDao(): ServicesLocalDao
    //abstract fun cardsLocalDao(): CardsLocalDao
    //abstract fun bankAccountsLocalDao(): BankAccountsLocalDao
    //abstract fun fundRequestsLocalDao(): FundRequestsLocalDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: LocalDataSource? = null

        val instance: LocalDataSource
            get() {
                // if the INSTANCE is not null, then return it,
                // if it is, then create the database
                return INSTANCE ?: synchronized(this) {
                    val newInstance = Room.databaseBuilder(
                        ApplicationClass.instance,
                        LocalDataSource::class.java,
                        databaseName
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = newInstance
                    // return instance
                    newInstance
                }
            }
    }
}