package com.tide.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tide.app.data.local.entity.ConsumptionEntryEntity;
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
public final class ConsumptionEntryDao_Impl implements ConsumptionEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ConsumptionEntryEntity> __insertionAdapterOfConsumptionEntryEntity;

  private final EntityDeletionOrUpdateAdapter<ConsumptionEntryEntity> __deletionAdapterOfConsumptionEntryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public ConsumptionEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfConsumptionEntryEntity = new EntityInsertionAdapter<ConsumptionEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `consumption_entries` (`id`,`addictionId`,`timestamp`,`amount`,`calculatedCost`,`triggerId`,`note`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ConsumptionEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAddictionId());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindDouble(4, entity.getAmount());
        statement.bindDouble(5, entity.getCalculatedCost());
        if (entity.getTriggerId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getTriggerId());
        }
        if (entity.getNote() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getNote());
        }
      }
    };
    this.__deletionAdapterOfConsumptionEntryEntity = new EntityDeletionOrUpdateAdapter<ConsumptionEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `consumption_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ConsumptionEntryEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM consumption_entries";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ConsumptionEntryEntity entry,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfConsumptionEntryEntity.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ConsumptionEntryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfConsumptionEntryEntity.handle(entry);
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
  public Flow<List<ConsumptionEntryEntity>> getEntriesForAddiction(final long addictionId) {
    final String _sql = "SELECT * FROM consumption_entries WHERE addictionId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"consumption_entries"}, new Callable<List<ConsumptionEntryEntity>>() {
      @Override
      @NonNull
      public List<ConsumptionEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCalculatedCost = CursorUtil.getColumnIndexOrThrow(_cursor, "calculatedCost");
          final int _cursorIndexOfTriggerId = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<ConsumptionEntryEntity> _result = new ArrayList<ConsumptionEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConsumptionEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final double _tmpCalculatedCost;
            _tmpCalculatedCost = _cursor.getDouble(_cursorIndexOfCalculatedCost);
            final Long _tmpTriggerId;
            if (_cursor.isNull(_cursorIndexOfTriggerId)) {
              _tmpTriggerId = null;
            } else {
              _tmpTriggerId = _cursor.getLong(_cursorIndexOfTriggerId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new ConsumptionEntryEntity(_tmpId,_tmpAddictionId,_tmpTimestamp,_tmpAmount,_tmpCalculatedCost,_tmpTriggerId,_tmpNote);
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
      final Continuation<? super List<ConsumptionEntryEntity>> $completion) {
    final String _sql = "SELECT * FROM consumption_entries WHERE addictionId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ConsumptionEntryEntity>>() {
      @Override
      @NonNull
      public List<ConsumptionEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCalculatedCost = CursorUtil.getColumnIndexOrThrow(_cursor, "calculatedCost");
          final int _cursorIndexOfTriggerId = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<ConsumptionEntryEntity> _result = new ArrayList<ConsumptionEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConsumptionEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final double _tmpCalculatedCost;
            _tmpCalculatedCost = _cursor.getDouble(_cursorIndexOfCalculatedCost);
            final Long _tmpTriggerId;
            if (_cursor.isNull(_cursorIndexOfTriggerId)) {
              _tmpTriggerId = null;
            } else {
              _tmpTriggerId = _cursor.getLong(_cursorIndexOfTriggerId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new ConsumptionEntryEntity(_tmpId,_tmpAddictionId,_tmpTimestamp,_tmpAmount,_tmpCalculatedCost,_tmpTriggerId,_tmpNote);
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
  public Flow<List<ConsumptionEntryEntity>> getEntriesForAddictionBetween(final long addictionId,
      final long startTime, final long endTime) {
    final String _sql = "SELECT * FROM consumption_entries WHERE addictionId = ? AND timestamp >= ? AND timestamp <= ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"consumption_entries"}, new Callable<List<ConsumptionEntryEntity>>() {
      @Override
      @NonNull
      public List<ConsumptionEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCalculatedCost = CursorUtil.getColumnIndexOrThrow(_cursor, "calculatedCost");
          final int _cursorIndexOfTriggerId = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<ConsumptionEntryEntity> _result = new ArrayList<ConsumptionEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConsumptionEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final double _tmpCalculatedCost;
            _tmpCalculatedCost = _cursor.getDouble(_cursorIndexOfCalculatedCost);
            final Long _tmpTriggerId;
            if (_cursor.isNull(_cursorIndexOfTriggerId)) {
              _tmpTriggerId = null;
            } else {
              _tmpTriggerId = _cursor.getLong(_cursorIndexOfTriggerId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new ConsumptionEntryEntity(_tmpId,_tmpAddictionId,_tmpTimestamp,_tmpAmount,_tmpCalculatedCost,_tmpTriggerId,_tmpNote);
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
      final long endTime, final Continuation<? super List<ConsumptionEntryEntity>> $completion) {
    final String _sql = "SELECT * FROM consumption_entries WHERE addictionId = ? AND timestamp >= ? AND timestamp <= ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ConsumptionEntryEntity>>() {
      @Override
      @NonNull
      public List<ConsumptionEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCalculatedCost = CursorUtil.getColumnIndexOrThrow(_cursor, "calculatedCost");
          final int _cursorIndexOfTriggerId = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<ConsumptionEntryEntity> _result = new ArrayList<ConsumptionEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConsumptionEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final double _tmpCalculatedCost;
            _tmpCalculatedCost = _cursor.getDouble(_cursorIndexOfCalculatedCost);
            final Long _tmpTriggerId;
            if (_cursor.isNull(_cursorIndexOfTriggerId)) {
              _tmpTriggerId = null;
            } else {
              _tmpTriggerId = _cursor.getLong(_cursorIndexOfTriggerId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new ConsumptionEntryEntity(_tmpId,_tmpAddictionId,_tmpTimestamp,_tmpAmount,_tmpCalculatedCost,_tmpTriggerId,_tmpNote);
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
