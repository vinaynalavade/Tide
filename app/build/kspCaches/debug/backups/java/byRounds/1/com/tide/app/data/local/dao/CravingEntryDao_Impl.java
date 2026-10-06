package com.tide.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tide.app.data.local.entity.CravingEntryEntity;
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
public final class CravingEntryDao_Impl implements CravingEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CravingEntryEntity> __insertionAdapterOfCravingEntryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public CravingEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCravingEntryEntity = new EntityInsertionAdapter<CravingEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `craving_entries` (`id`,`addictionId`,`timestamp`,`intensity`,`triggerId`,`mood`,`resisted`,`note`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CravingEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAddictionId());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindLong(4, entity.getIntensity());
        if (entity.getTriggerId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getTriggerId());
        }
        if (entity.getMood() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getMood());
        }
        final int _tmp = entity.getResisted() ? 1 : 0;
        statement.bindLong(7, _tmp);
        if (entity.getNote() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getNote());
        }
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM craving_entries";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final CravingEntryEntity entry,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCravingEntryEntity.insertAndReturnId(entry);
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
  public Flow<List<CravingEntryEntity>> getEntriesForAddiction(final long addictionId) {
    final String _sql = "SELECT * FROM craving_entries WHERE addictionId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"craving_entries"}, new Callable<List<CravingEntryEntity>>() {
      @Override
      @NonNull
      public List<CravingEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "intensity");
          final int _cursorIndexOfTriggerId = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerId");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfResisted = CursorUtil.getColumnIndexOrThrow(_cursor, "resisted");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<CravingEntryEntity> _result = new ArrayList<CravingEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CravingEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpIntensity;
            _tmpIntensity = _cursor.getInt(_cursorIndexOfIntensity);
            final Long _tmpTriggerId;
            if (_cursor.isNull(_cursorIndexOfTriggerId)) {
              _tmpTriggerId = null;
            } else {
              _tmpTriggerId = _cursor.getLong(_cursorIndexOfTriggerId);
            }
            final String _tmpMood;
            if (_cursor.isNull(_cursorIndexOfMood)) {
              _tmpMood = null;
            } else {
              _tmpMood = _cursor.getString(_cursorIndexOfMood);
            }
            final boolean _tmpResisted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfResisted);
            _tmpResisted = _tmp != 0;
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new CravingEntryEntity(_tmpId,_tmpAddictionId,_tmpTimestamp,_tmpIntensity,_tmpTriggerId,_tmpMood,_tmpResisted,_tmpNote);
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
  public Object getEntriesForAddictionSync(final long addictionId,
      final Continuation<? super List<CravingEntryEntity>> $completion) {
    final String _sql = "SELECT * FROM craving_entries WHERE addictionId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CravingEntryEntity>>() {
      @Override
      @NonNull
      public List<CravingEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "intensity");
          final int _cursorIndexOfTriggerId = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerId");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfResisted = CursorUtil.getColumnIndexOrThrow(_cursor, "resisted");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<CravingEntryEntity> _result = new ArrayList<CravingEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CravingEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpIntensity;
            _tmpIntensity = _cursor.getInt(_cursorIndexOfIntensity);
            final Long _tmpTriggerId;
            if (_cursor.isNull(_cursorIndexOfTriggerId)) {
              _tmpTriggerId = null;
            } else {
              _tmpTriggerId = _cursor.getLong(_cursorIndexOfTriggerId);
            }
            final String _tmpMood;
            if (_cursor.isNull(_cursorIndexOfMood)) {
              _tmpMood = null;
            } else {
              _tmpMood = _cursor.getString(_cursorIndexOfMood);
            }
            final boolean _tmpResisted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfResisted);
            _tmpResisted = _tmp != 0;
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new CravingEntryEntity(_tmpId,_tmpAddictionId,_tmpTimestamp,_tmpIntensity,_tmpTriggerId,_tmpMood,_tmpResisted,_tmpNote);
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

  @Override
  public Flow<List<CravingEntryEntity>> getEntriesForAddictionBetween(final long addictionId,
      final long startTime, final long endTime) {
    final String _sql = "SELECT * FROM craving_entries WHERE addictionId = ? AND timestamp >= ? AND timestamp <= ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"craving_entries"}, new Callable<List<CravingEntryEntity>>() {
      @Override
      @NonNull
      public List<CravingEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "intensity");
          final int _cursorIndexOfTriggerId = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerId");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfResisted = CursorUtil.getColumnIndexOrThrow(_cursor, "resisted");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<CravingEntryEntity> _result = new ArrayList<CravingEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CravingEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpIntensity;
            _tmpIntensity = _cursor.getInt(_cursorIndexOfIntensity);
            final Long _tmpTriggerId;
            if (_cursor.isNull(_cursorIndexOfTriggerId)) {
              _tmpTriggerId = null;
            } else {
              _tmpTriggerId = _cursor.getLong(_cursorIndexOfTriggerId);
            }
            final String _tmpMood;
            if (_cursor.isNull(_cursorIndexOfMood)) {
              _tmpMood = null;
            } else {
              _tmpMood = _cursor.getString(_cursorIndexOfMood);
            }
            final boolean _tmpResisted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfResisted);
            _tmpResisted = _tmp != 0;
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new CravingEntryEntity(_tmpId,_tmpAddictionId,_tmpTimestamp,_tmpIntensity,_tmpTriggerId,_tmpMood,_tmpResisted,_tmpNote);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
