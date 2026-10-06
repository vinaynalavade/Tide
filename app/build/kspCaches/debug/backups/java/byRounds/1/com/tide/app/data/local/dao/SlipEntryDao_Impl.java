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
import com.tide.app.data.local.entity.SlipEntryEntity;
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
public final class SlipEntryDao_Impl implements SlipEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SlipEntryEntity> __insertionAdapterOfSlipEntryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public SlipEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSlipEntryEntity = new EntityInsertionAdapter<SlipEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `slip_entries` (`id`,`addictionId`,`timestamp`,`amount`,`cost`,`triggerId`,`mood`,`note`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SlipEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAddictionId());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindDouble(4, entity.getAmount());
        statement.bindDouble(5, entity.getCost());
        if (entity.getTriggerId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getTriggerId());
        }
        if (entity.getMood() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getMood());
        }
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
        final String _query = "DELETE FROM slip_entries";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SlipEntryEntity entry, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSlipEntryEntity.insertAndReturnId(entry);
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
  public Flow<List<SlipEntryEntity>> getEntriesForAddiction(final long addictionId) {
    final String _sql = "SELECT * FROM slip_entries WHERE addictionId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"slip_entries"}, new Callable<List<SlipEntryEntity>>() {
      @Override
      @NonNull
      public List<SlipEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfTriggerId = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerId");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<SlipEntryEntity> _result = new ArrayList<SlipEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SlipEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final double _tmpCost;
            _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
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
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new SlipEntryEntity(_tmpId,_tmpAddictionId,_tmpTimestamp,_tmpAmount,_tmpCost,_tmpTriggerId,_tmpMood,_tmpNote);
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
      final Continuation<? super List<SlipEntryEntity>> $completion) {
    final String _sql = "SELECT * FROM slip_entries WHERE addictionId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SlipEntryEntity>>() {
      @Override
      @NonNull
      public List<SlipEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfTriggerId = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerId");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<SlipEntryEntity> _result = new ArrayList<SlipEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SlipEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final double _tmpCost;
            _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
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
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new SlipEntryEntity(_tmpId,_tmpAddictionId,_tmpTimestamp,_tmpAmount,_tmpCost,_tmpTriggerId,_tmpMood,_tmpNote);
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
  public Flow<List<SlipEntryEntity>> getEntriesForAddictionBetween(final long addictionId,
      final long startTime, final long endTime) {
    final String _sql = "SELECT * FROM slip_entries WHERE addictionId = ? AND timestamp >= ? AND timestamp <= ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"slip_entries"}, new Callable<List<SlipEntryEntity>>() {
      @Override
      @NonNull
      public List<SlipEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfTriggerId = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerId");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<SlipEntryEntity> _result = new ArrayList<SlipEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SlipEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final double _tmpCost;
            _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
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
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new SlipEntryEntity(_tmpId,_tmpAddictionId,_tmpTimestamp,_tmpAmount,_tmpCost,_tmpTriggerId,_tmpMood,_tmpNote);
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
  public Object getEntriesForAddictionBetweenSync(final long addictionId, final long startTime,
      final long endTime, final Continuation<? super List<SlipEntryEntity>> $completion) {
    final String _sql = "SELECT * FROM slip_entries WHERE addictionId = ? AND timestamp >= ? AND timestamp <= ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SlipEntryEntity>>() {
      @Override
      @NonNull
      public List<SlipEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfTriggerId = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerId");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<SlipEntryEntity> _result = new ArrayList<SlipEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SlipEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final double _tmpCost;
            _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
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
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new SlipEntryEntity(_tmpId,_tmpAddictionId,_tmpTimestamp,_tmpAmount,_tmpCost,_tmpTriggerId,_tmpMood,_tmpNote);
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
