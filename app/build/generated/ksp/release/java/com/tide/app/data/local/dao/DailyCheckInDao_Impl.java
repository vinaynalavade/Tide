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
import com.tide.app.data.local.entity.DailyCheckInEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DailyCheckInDao_Impl implements DailyCheckInDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyCheckInEntity> __insertionAdapterOfDailyCheckInEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public DailyCheckInDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyCheckInEntity = new EntityInsertionAdapter<DailyCheckInEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_check_ins` (`id`,`addictionId`,`date`,`mood`,`cravingOccurred`,`cravingIntensity`,`note`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyCheckInEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAddictionId());
        statement.bindString(3, entity.getDate());
        statement.bindString(4, entity.getMood());
        final int _tmp = entity.getCravingOccurred() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getCravingIntensity());
        if (entity.getNote() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getNote());
        }
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_check_ins";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final DailyCheckInEntity checkIn,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDailyCheckInEntity.insertAndReturnId(checkIn);
          __db.setTransactionSuccessful();
          return _result;
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
  public Flow<DailyCheckInEntity> getCheckInForDate(final long addictionId, final String date) {
    final String _sql = "SELECT * FROM daily_check_ins WHERE addictionId = ? AND date = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    _argIndex = 2;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_check_ins"}, new Callable<DailyCheckInEntity>() {
      @Override
      @Nullable
      public DailyCheckInEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfCravingOccurred = CursorUtil.getColumnIndexOrThrow(_cursor, "cravingOccurred");
          final int _cursorIndexOfCravingIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "cravingIntensity");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final DailyCheckInEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final boolean _tmpCravingOccurred;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCravingOccurred);
            _tmpCravingOccurred = _tmp != 0;
            final int _tmpCravingIntensity;
            _tmpCravingIntensity = _cursor.getInt(_cursorIndexOfCravingIntensity);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _result = new DailyCheckInEntity(_tmpId,_tmpAddictionId,_tmpDate,_tmpMood,_tmpCravingOccurred,_tmpCravingIntensity,_tmpNote);
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
  public Object getCheckInForDateSync(final long addictionId, final String date,
      final Continuation<? super DailyCheckInEntity> $completion) {
    final String _sql = "SELECT * FROM daily_check_ins WHERE addictionId = ? AND date = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    _argIndex = 2;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyCheckInEntity>() {
      @Override
      @Nullable
      public DailyCheckInEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfCravingOccurred = CursorUtil.getColumnIndexOrThrow(_cursor, "cravingOccurred");
          final int _cursorIndexOfCravingIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "cravingIntensity");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final DailyCheckInEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final boolean _tmpCravingOccurred;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCravingOccurred);
            _tmpCravingOccurred = _tmp != 0;
            final int _tmpCravingIntensity;
            _tmpCravingIntensity = _cursor.getInt(_cursorIndexOfCravingIntensity);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _result = new DailyCheckInEntity(_tmpId,_tmpAddictionId,_tmpDate,_tmpMood,_tmpCravingOccurred,_tmpCravingIntensity,_tmpNote);
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

  @Override
  public Flow<List<DailyCheckInEntity>> getAllCheckIns(final long addictionId) {
    final String _sql = "SELECT * FROM daily_check_ins WHERE addictionId = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_check_ins"}, new Callable<List<DailyCheckInEntity>>() {
      @Override
      @NonNull
      public List<DailyCheckInEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfCravingOccurred = CursorUtil.getColumnIndexOrThrow(_cursor, "cravingOccurred");
          final int _cursorIndexOfCravingIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "cravingIntensity");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<DailyCheckInEntity> _result = new ArrayList<DailyCheckInEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyCheckInEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final boolean _tmpCravingOccurred;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCravingOccurred);
            _tmpCravingOccurred = _tmp != 0;
            final int _tmpCravingIntensity;
            _tmpCravingIntensity = _cursor.getInt(_cursorIndexOfCravingIntensity);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new DailyCheckInEntity(_tmpId,_tmpAddictionId,_tmpDate,_tmpMood,_tmpCravingOccurred,_tmpCravingIntensity,_tmpNote);
            _result.add(_item);
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
  public Object getAllCheckInsSync(final long addictionId,
      final Continuation<? super List<DailyCheckInEntity>> $completion) {
    final String _sql = "SELECT * FROM daily_check_ins WHERE addictionId = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyCheckInEntity>>() {
      @Override
      @NonNull
      public List<DailyCheckInEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfCravingOccurred = CursorUtil.getColumnIndexOrThrow(_cursor, "cravingOccurred");
          final int _cursorIndexOfCravingIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "cravingIntensity");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<DailyCheckInEntity> _result = new ArrayList<DailyCheckInEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyCheckInEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final boolean _tmpCravingOccurred;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCravingOccurred);
            _tmpCravingOccurred = _tmp != 0;
            final int _tmpCravingIntensity;
            _tmpCravingIntensity = _cursor.getInt(_cursorIndexOfCravingIntensity);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new DailyCheckInEntity(_tmpId,_tmpAddictionId,_tmpDate,_tmpMood,_tmpCravingOccurred,_tmpCravingIntensity,_tmpNote);
            _result.add(_item);
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
