package com.tide.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tide.app.data.local.entity.UserSettingsEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserSettingsDao_Impl implements UserSettingsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserSettingsEntity> __insertionAdapterOfUserSettingsEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public UserSettingsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserSettingsEntity = new EntityInsertionAdapter<UserSettingsEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_settings` (`id`,`themeMode`,`hapticsEnabled`,`soundEnabled`,`onboardingCompleted`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserSettingsEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getThemeMode());
        final int _tmp = entity.getHapticsEnabled() ? 1 : 0;
        statement.bindLong(3, _tmp);
        final int _tmp_1 = entity.getSoundEnabled() ? 1 : 0;
        statement.bindLong(4, _tmp_1);
        final int _tmp_2 = entity.getOnboardingCompleted() ? 1 : 0;
        statement.bindLong(5, _tmp_2);
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM user_settings";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final UserSettingsEntity settings,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserSettingsEntity.insert(settings);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserSettingsEntity> getSettings() {
    final String _sql = "SELECT * FROM user_settings WHERE id = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_settings"}, new Callable<UserSettingsEntity>() {
      @Override
      @Nullable
      public UserSettingsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfThemeMode = CursorUtil.getColumnIndexOrThrow(_cursor, "themeMode");
          final int _cursorIndexOfHapticsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "hapticsEnabled");
          final int _cursorIndexOfSoundEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "soundEnabled");
          final int _cursorIndexOfOnboardingCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "onboardingCompleted");
          final UserSettingsEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpThemeMode;
            _tmpThemeMode = _cursor.getString(_cursorIndexOfThemeMode);
            final boolean _tmpHapticsEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfHapticsEnabled);
            _tmpHapticsEnabled = _tmp != 0;
            final boolean _tmpSoundEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSoundEnabled);
            _tmpSoundEnabled = _tmp_1 != 0;
            final boolean _tmpOnboardingCompleted;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfOnboardingCompleted);
            _tmpOnboardingCompleted = _tmp_2 != 0;
            _result = new UserSettingsEntity(_tmpId,_tmpThemeMode,_tmpHapticsEnabled,_tmpSoundEnabled,_tmpOnboardingCompleted);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getSettingsSync(final Continuation<? super UserSettingsEntity> $completion) {
    final String _sql = "SELECT * FROM user_settings WHERE id = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserSettingsEntity>() {
      @Override
      @Nullable
      public UserSettingsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfThemeMode = CursorUtil.getColumnIndexOrThrow(_cursor, "themeMode");
          final int _cursorIndexOfHapticsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "hapticsEnabled");
          final int _cursorIndexOfSoundEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "soundEnabled");
          final int _cursorIndexOfOnboardingCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "onboardingCompleted");
          final UserSettingsEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpThemeMode;
            _tmpThemeMode = _cursor.getString(_cursorIndexOfThemeMode);
            final boolean _tmpHapticsEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfHapticsEnabled);
            _tmpHapticsEnabled = _tmp != 0;
            final boolean _tmpSoundEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSoundEnabled);
            _tmpSoundEnabled = _tmp_1 != 0;
            final boolean _tmpOnboardingCompleted;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfOnboardingCompleted);
            _tmpOnboardingCompleted = _tmp_2 != 0;
            _result = new UserSettingsEntity(_tmpId,_tmpThemeMode,_tmpHapticsEnabled,_tmpSoundEnabled,_tmpOnboardingCompleted);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
