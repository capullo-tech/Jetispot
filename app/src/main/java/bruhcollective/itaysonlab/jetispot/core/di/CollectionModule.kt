package bruhcollective.itaysonlab.jetispot.core.di

import android.content.Context
import androidx.room.Room
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CollectionModule {
  @Provides
  fun provideDatabase (
    @ApplicationContext appCtx: Context
  ): LocalCollectionDatabase = Room.databaseBuilder(appCtx, LocalCollectionDatabase::class.java, "spCollection").build()

  @Provides
  fun provideDao (
    db: LocalCollectionDatabase
  ): LocalCollectionDao = db.dao()
}