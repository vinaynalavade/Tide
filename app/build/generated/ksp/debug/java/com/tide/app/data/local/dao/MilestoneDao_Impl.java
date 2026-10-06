package com.tide.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tide.app.data.local.entity.MilestoneEntity;
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
public final class MilestoneDao_Impl implements MilestoneDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MilestoneEntity> __insertionAdapterOfMilestoneEntity;

  private final EntityDeletionOrUpdateAdapter<MilestoneEntity> __updateAdapterOfMilestoneEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public MilestoneDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMilestoneEntity = new EntityInsertionAdapter<MilestoneEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `milestones` (`id`,`type`,`title`,`description`,`targetValue`,`achievedAt`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MilestoneEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getType());
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getDescription());
        statement.bindDouble(5, entity.getTargetValue());
        if (entity.getAchievedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getAchievedAt());
        }
      }
    };
    this.__updateAdapterOfMilestoneEntity = new EntityDeletionOrUpdateAdapter<MilestoneEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `milestones` SET `id` = ?,`type` = ?,`title` = ?,`description` = ?,`targetValue` = ?,`achievedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MilestoneEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getType());
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getDescription());
        statement.bindDouble(5, entity.getTargetValue());
        if (entity.getAchievedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getAchievedAt());
        }
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM milestones";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<MilestoneEntity> milestones,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMilestoneEntity.insert(milestones);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MilestoneEntity milestone,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMilestoneEntity.handle(milestone);
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
  public Flow<List<MilestoneEntity>> getAll() {
    final String _sql = "SELECT * FROM milestones ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"milestones"}, new Callable<List<MilestoneEntity>>() {
      @Override
      @NonNull
      public List<MilestoneEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTargetValue = CursorUtil.getColumnIndexOrThrow(_cursor, "targetValue");
          final int _cursorIndexOfAchievedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "achievedAt");
          final List<MilestoneEntity> _result = new ArrayList<MilestoneEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MilestoneEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpTargetValue;
            _tmpTargetValue = _cursor.getDouble(_cursorIndexOfTargetValue);
            final Long _tmpAchievedAt;
            if (_cursor.isNull(_cursorIndexOfAchievedAt)) {
              _tmpAchievedAt = null;
            } else {
              _tmpAchievedAt = _cursor.getLong(_cursorIndexOfAchievedAt);
            }
            _item = new MilestoneEntity(_tmpId,_tmpType,_tmpTitle,_tmpDescription,_tmpTargetValue,_tmpAchievedAt);
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
  public Object getAllSync(final Continuation<? super List<MilestoneEntity>> $completion) {
    final String _sql = "SELECT * FROM milestones ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MilestoneEntity>>() {
      @Override
      @NonNull
      public List<MilestoneEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTargetValue = CursorUtil.getColumnIndexOrThrow(_cursor, "targetValue");
          final int _cursorIndexOfAchievedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "achievedAt");
          final List<MilestoneEntity> _result = new ArrayList<MilestoneEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MilestoneEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpTargetValue;
            _tmpTargetValue = _cursor.getDouble(_cursorIndexOfTargetValue);
            final Long _tmpAchievedAt;
            if (_cursor.isNull(_cursorIndexOfAchievedAt)) {
              _tmpAchievedAt = null;
            } else {
              _tmpAchievedAt = _cursor.getLong(_cursorIndexOfAchievedAt);
            }
            _item = new MilestoneEntity(_tmpId,_tmpType,_tmpTitle,_tmpDescription,_tmpTargetValue,_tmpAchievedAt);
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
  public Object getByType(final String type,
      final Continuation<? super MilestoneEntity> $completion) {
    final String _sql = "SELECT * FROM milestones WHERE type = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MilestoneEntity>() {
      @Override
      @Nullable
      public MilestoneEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTargetValue = CursorUtil.getColumnIndexOrThrow(_cursor, "targetValue");
          final int _cursorIndexOfAchievedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "achievedAt");
          final MilestoneEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpTargetValue;
            _tmpTargetValue = _cursor.getDouble(_cursorIndexOfTargetValue);
            final Long _tmpAchievedAt;
            if (_cursor.isNull(_cursorIndexOfAchievedAt)) {
              _tmpAchievedAt = null;
            } else {
              _tmpAchievedAt = _cursor.getLong(_cursorIndexOfAchievedAt);
            }
            _result = new MilestoneEntity(_tmpId,_tmpType,_tmpTitle,_tmpDescription,_tmpTargetValue,_tmpAchievedAt);
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
